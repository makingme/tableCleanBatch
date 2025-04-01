package com.clean.batch.extra;

import com.clean.batch.cipher.ICipher;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final Gson gson = new Gson();

    public static final String ENCRYPTION_KEYWORD = "@ENC:";

    public static ConfigManager getInstance() {
        return ConfigManager.Singleton.instance;
    }

    private static class Singleton{
        private static final ConfigManager instance = new ConfigManager();
    }

    private String baseFilePath ="./conf/config.json";
    private String baseCharset = "UTF-8";

    public ConfigManager() {
        this.baseFilePath = System.getenv("CLEAN_BATCH_CONFIG") != null ? System.getenv("CLEAN_BATCH_CONFIG") : this.baseFilePath;
        this.baseCharset = System.getenv("CLEAN_BATCH_CONFIG_CHARSET") != null ? System.getenv("CLEAN_BATCH_CONFIG_CHARSET") : this.baseCharset;
    }

    public ConfigVo load() throws Exception {
        // 파일 내용 담을 변수
        String fileContent = null;
        ConfigVo configVo = null;
        try{
            // 설정 파일 Instance
            File resource=new File(baseFilePath);

            // 설정 파일 이 없다면 기동 중지
            if(!resource.exists()) {
                throw new Exception(baseFilePath+"파일을 찾을 수 없습니다.");
            }

            // 설정 파일 IO
            try(InputStream is = new FileInputStream(resource);
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName(baseCharset)))	) {

                StringBuilder sb=new StringBuilder();
                String val=null;
                while((val=br.readLine()) !=null) sb.append(val);
                fileContent = sb.toString();
            }

            // 설정 파일 내용이 없다면 기동 중지
            if(fileContent.trim().isEmpty())  {
                throw new Exception(baseFilePath+"설정 파일에 내용이 없음으로 인해 설정 로드 실패");
            }

            // configVo 추출
            configVo = gson.fromJson(fileContent,  new TypeToken<ConfigVo>(){}.getType());
            if(configVo == null) {
                throw new Exception(baseFilePath+"설정 파일에 내용이 없음으로 인해 설정 로드 실패");
            }
            String encryptClass = configVo.getEncryptClass();
            encryptClass = encryptClass == null||encryptClass.trim().isEmpty() ? "com.clean.batch.cipher.AESCipherUtil" : encryptClass;
            ICipher cipher = ReflectionUtil.getInstance().generate(ICipher.class, encryptClass, null);

            JdbcInfoVo jdbcInfoVo = configVo.getJdbcInfo();
            if(jdbcInfoVo == null) {
                throw new Exception(baseFilePath+"설정 파일에 jdbcInfoVo 설정이 없음, 설정 로드 실패");
            }
            // jdbcUrl 값 확인
            String jdbcUrl = jdbcInfoVo.getJdbcUrl();
            if(jdbcUrl ==null|| jdbcUrl.trim().isEmpty()) {
                throw new Exception(baseFilePath+"설정 파일에 jdbcUrl 설정이 없음, 설정 로드 실패");
            }
            if(jdbcUrl.trim().startsWith(ENCRYPTION_KEYWORD)) {
                String encryptData = jdbcUrl.substring(ENCRYPTION_KEYWORD.length());
                jdbcInfoVo.setJdbcUrl(cipher.decrypt(encryptData));
            }

            // userName 값 확인
            String userName = jdbcInfoVo.getUsername();
            if(userName ==null|| userName.trim().isEmpty()) {
                throw new Exception(baseFilePath+"설정 파일에 userName 설정이 없음, 설정 로드 실패");
            }
            if(userName.trim().startsWith(ENCRYPTION_KEYWORD)) {
                String encryptData = userName.substring(ENCRYPTION_KEYWORD.length());
                jdbcInfoVo.setUsername(cipher.decrypt(encryptData));
            }

            // password 값 확인
            String password = jdbcInfoVo.getPassword();
            if(password ==null|| password.trim().isEmpty()) {
                throw new Exception(baseFilePath+"설정 파일에 password 설정이 없음, 설정 로드 실패");
            }
            if(password.trim().startsWith(ENCRYPTION_KEYWORD)) {
                String encryptData = password.substring(ENCRYPTION_KEYWORD.length());
                jdbcInfoVo.setPassword(cipher.decrypt(encryptData));
            }

            // startTime 값 확인
            String startTime = configVo.getStartTime();
            if(startTime ==null|| startTime.trim().isEmpty()) {
                throw new Exception(baseFilePath+"설정 파일에 startTime 설정이 없음, 설정 로드 실패");
            }

            // endTime 값 확인
            String endTime = configVo.getEndTime();
            if(endTime ==null|| endTime.trim().isEmpty()) {
                throw new Exception(baseFilePath+"설정 파일에 endTime 설정이 없음, 설정 로드 실패");
            }
            
            // startTime, endTime 포맷 확인
            if(isNotValidTime(startTime)){
                throw new Exception(baseFilePath+"설정 파일에 startTime 유효하지 않은 값, 설정 로드 실패");
            }
            if(isNotValidTime(endTime)){
                throw new Exception(baseFilePath+"설정 파일에 endTime 유효하지 않은 값, 설정 로드 실패");
            }
            // startTime, endTime 입력 순서 확인
            if(startTime.compareTo(endTime) > 0) {
                throw new Exception(baseFilePath+"설정 파일에 startTime 값이 endTime 값보다 큼, 설정 로드 실패");
            }

            // CleanJob 설정 유무 확인
            if(configVo.getCleanJob() ==null || configVo.getCleanJob().isEmpty()) {
                throw new Exception(baseFilePath+"설정 파일에 CLEAN JOB 설정이 없음, 설정 로드 실패");
            }
            // CleanJob 설정 검증
            for(Map.Entry<String, CleanJobConfigVo> entry: configVo.getCleanJob().entrySet()) {
                String jobId = entry.getKey();
                CleanJobConfigVo cleanJobConfigVo = entry.getValue();

                String selectQuery = cleanJobConfigVo.getSelectQuery();
                if(selectQuery==null || selectQuery.trim().isEmpty()) {
                    throw new Exception(jobId+"설정에 selectQuery 설정이 없음, 설정 로드 실패");
                }
                if(selectQuery.endsWith(";")){
                    throw new Exception(jobId+"설정에 selectQuery 끝 문자 ; 제거 필요, 설정 로드 실패");
                }
                String deleteQuery = cleanJobConfigVo.getDeleteQuery();
                if(deleteQuery==null || deleteQuery.trim().isEmpty()) {
                    throw new Exception(jobId+"설정에 deleteQuery 설정이 없음, 설정 로드 실패");
                }
                if(deleteQuery.endsWith(";")){
                    throw new Exception(jobId+"설정에 deleteQuery 끝 문자 ; 제거 필요, 설정 로드 실패");
                }
                List<String> pkColumnList =cleanJobConfigVo.getPkColumnList();
                if(pkColumnList==null || pkColumnList.isEmpty()) {
                    throw new Exception(jobId+"설정에 pkColumnList 설정이 없음, 설정 로드 실패");
                }
            }

        }catch (FileNotFoundException e) {
            throw new RuntimeException(baseFilePath+"설정 파일을 찾을 수 없음", e);
        }catch (IOException e) {
            throw new RuntimeException(baseFilePath+"설정 파일 처리중 IO 에러 발생", e);
        }catch(JsonSyntaxException e) {
            throw new RuntimeException(baseFilePath+"설정 파일 JSON 포맷 이상", e);
        }catch(Exception e) {
            throw new RuntimeException(baseFilePath+"설정 파일 로딩 중 예상 외 에러 발생", e);
        }

        return configVo;
    }

    private boolean isNotValidTime(String time){
        // HHmm 형식의 정규 표현식
        String regex = "^(?:[01]\\d|2[0-3]):[0-5]\\d$";

        // 정규 표현식으로 형식 검사
        if (!time.matches(regex)) {
            return true;
        }

        // DateTimeFormatter를 사용하여 시간 파싱
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime.parse(time, formatter);
        } catch (DateTimeParseException e) {
            return true;
        }

        return false;
    }
}
