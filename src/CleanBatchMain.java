import com.clean.batch.extra.*;
import com.clean.batch.vo.CleanJobConfigVo;
import com.clean.batch.vo.ConfigVo;
import com.clean.batch.vo.JobResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CleanBatchMain extends Thread{
    private final static Logger logger = LoggerFactory.getLogger("MAIN");

    private final DateTimeFormatter TIME_FORMATTER  = DateTimeFormatter.ofPattern("HH:mm");

    private final ConfigVo configVo;

    private final LocalTime START_TIME;
    private final LocalTime END_TIME;

    private final List<CleanJob> jobs = new ArrayList<>();
    private final ExecutorService executorService;

    public CleanBatchMain(ConfigVo configVo) {
        this.configVo = configVo;
        START_TIME = LocalTime.parse(configVo.getStartTime(), TIME_FORMATTER);
        END_TIME = LocalTime.parse(configVo.getEndTime(), TIME_FORMATTER);
        int threadPoolSize = Math.min(configVo.getCleanJob().size(), Runtime.getRuntime().availableProcessors() * 2);
        executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    private volatile boolean isRunning = true;

    public void init(){
        for(Map.Entry<String, CleanJobConfigVo> entry: configVo.getCleanJob().entrySet()){
            String jobId = entry.getKey();
            CleanJobConfigVo jobConfig = entry.getValue();
            CleanJob cleanJob = new CleanJob(jobId, jobConfig);
            jobs.add(cleanJob);
            logger.info("{} JOB 등록", jobId);
        }
    }

    private void executeJobsAsync() {
        currentThread().setName("CLEAN BATCH-SCHEDULER");

        // 모든 작업에 대해 CompletableFuture 생성
        List<CompletableFuture<JobResultVo>> futures = jobs.stream()
                .map(job -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.currentThread().setName(job.getJobId());
                        logger.info("{} JOB 수행 시작", job.getJobId());
                        long startTime = System.currentTimeMillis();
                        int deleteCount = job.execute();
                        long endTime = System.currentTimeMillis();

                        return new JobResultVo(job.getJobId(), deleteCount, endTime - startTime);
                    } catch (Exception e) {
                        logger.error("{} JOB 수행 중 오류 발생: {}", job.getJobId(), e.getMessage(), e);
                        return new JobResultVo(job.getJobId(), -1, 0);
                    }
                }, executorService))
                .collect(Collectors.toList());

        // 모든 작업이 완료될 때까지 대기하고 결과 처리
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenAccept(v -> {
                    currentThread().setName("CLEAN BATCH");
                    logger.info("###### 모든 작업이 완료되었습니다");

                    // 각 작업의 결과 로깅
                    futures.forEach(future -> {
                        try {
                            JobResultVo result = future.get();
                            if (result.getDeleteCount() >= 0) {
                                logger.info("{} JOB 처리 완료, 처리 건수:{}, 소요시간:{}ms",
                                        result.getJobId(), result.getDeleteCount(), result.getExecutionTime());
                            }
                        } catch (Exception e) {
                            logger.error("결과 처리 중 오류 발생", e);
                        }
                    });
                    logger.info("####################################");
        }).exceptionally(ex -> {
                    logger.error("작업 처리 중 오류 발생", ex);
                    return null;
        });
        // 모든 작업이 완료 될때까지 Blocking
        allTasks.join();
    }

    @Override
    public void run() {
        while (isRunning && !isInterrupted()) {
            try{
                // 다음 주기까지 대기 (1분)
                sleep(configVo.getCycle());

                LocalTime now = LocalTime.now();
                if (isWithinTimeRange(now)) {
                    executeJobsAsync();
                }else{
                    logger.info("CLEAN JOB 수행 외 시간, START_TIME:{}, END_TIME:{}", START_TIME, END_TIME);
                }
            }catch (InterruptedException e){
                // 인터럽트 상태 복원 및 루프 종료
                Thread.currentThread().interrupt();
                break;
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }

    }

    private boolean isWithinTimeRange(LocalTime currentTime) {
        return !currentTime.isBefore(START_TIME) && !currentTime.isAfter(END_TIME);
    }

    public static void main(String[] args) {
        // 메인 쓰레드 이름 변경
        currentThread().setName("CONFIG LOADER");
        logger.info("################## 프로그램 기동 ####################");
        ConfigVo configVo = null;
        try{
            logger.info("######### STEP1. 설정 로드");
            configVo = ConfigManager.getInstance().load();
        }catch (Exception e){
            logger.error(e.getMessage());
            logger.error("설정 로드 예외 상세:", e);
            logger.error("프로그램 종료");
            System.exit(0);
        }
        if(configVo == null){
            System.exit(0);
        }
        currentThread().setName("DBMS POOL INITIALIZER");
        logger.info("######### STEP2. DBMS CONNECTION POLL 초기화");
        try{
            DatabaseConnectionManager.getInstance().initialize(configVo.getJdbcInfo());
        }catch (Exception e){
            logger.error("DBMS POOL 초기화 중 예외 발생", e);
        }
        currentThread().setName("CLEAN BATCH");
        logger.info("######### STEP3. CLEAN BATCH JOB 등록");
        CleanBatchMain cleanBatchMain = new CleanBatchMain(configVo);
        cleanBatchMain.init();
        logger.info("######### STEP4. CLEAN BATCH JOB 수행");
        cleanBatchMain.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cleanBatchMain.stopThread();
            try {
                cleanBatchMain.join(); // 스레드 종료 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void stopThread() {
        logger.info("################## 프로그램 종료 시그널 감지 ####################");
        isRunning = false;
        DatabaseConnectionManager.getInstance().close();
        interrupt();
    }
}