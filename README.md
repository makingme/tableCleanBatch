## 1. 설정(conf/config.json)
    1. 동작 관련 설정
        1) cycle(Long)
            - 수행 주기
            - 기본 값 60 * 1000
        2) startTime(String)
            - 수행 유효 시작 시간
            - HH:mm 포맷으로 지정
            - endTime 보다 작음 값
        3) endTime(String)
            - 수행 유효 종료 시간
            - HH:mm 포맷으로 지정
            - startTime 보다 큰 값
    2. DBMS 관련(jdbcInfo) 설정
        1) jdbcUrl(String) 
            - JDBC URL 정보
            - @ENC: 시작 시 암호화된 문자열 판단
        2) username(String) 
            - JDBC 연결 유저 정보
            - @ENC: 시작 시 암호화된 문자열 판단
        3) password(String)
            - JDBC 연결 유저 비밀번호
            - @ENC: 시작 시 암호화된 문자열 판단
        4) maximumPoolSize(Integer)
            - HikariCP 설정
            - 기본 값 5
        5) minimumIdl(Integer)
            - HikariCP 설정
            - 기본 값 5
        6) idleTimeout(Long)
            - HikariCP 설정
            - 기본 값 5 * 60 * 1000
        7) maxLifetime(Long)
            - HikariCP 설정
            - 기본 값 30 * 60 * 1000
        8) connectionTimeout(Long)
            - HikariCP 설정
            - 기본 값 30 * 1000
        9) connectionTestQuery(String)
            - HikariCP 설정
        10) poolName(String)
            - HikariCP 설정
            - 기본 값 jdbcUrl Hash Code
    3. 배치 JOB(cleanJob)
        1) pkColumnList(List<String>)
            - 대상 테이블의 기본키 지정
            - 지정 순서 중요(DELETE 쿼리)
        2) selectQuery(String)
            - 삭제 대상을 추출하는 조회 쿼리
            - pkColumnList에 지정한 컬럼 데이터를 추출하도록 지정해야 함
        3) deleteQuery(String)
            - 조회 데이터를 기본 키 조건으로 삭제하는 쿼리
            - pkColumnList에 지정 한 컬럼 순서대로 조건절 순서를 맞춰야 함
        4) maxBatchCount(Integer)
            - 1회 수행에 최대 삭제 건수
            - 기본값 1000000
## 2. 문자열 암호화 가이드
    1. shell 경로로 이동
    2. ./cipher.sh ${plain text}
    3. 출력된 암호화 문자열 앞에 @ENC: 예약어를 사용하여 설정(config.json)에 사용
## 3. 참조 라이브러리
    1. JSON 관련
        - gson-2.10.1.jar 
    2. LOG 관련
        - logback-classic-1.0.13.jar 
        - logback-core-1.0.13.jar 
        - logback-access-1.2.9.jar 
        - slf4j-api-1.7.5.jar
        - jcl-over-slf4j-1.7.5.jar
    3. DBMS 관련
        - postgresql-42.6.0.jar 
        - HikariCP-4.0.3.jar