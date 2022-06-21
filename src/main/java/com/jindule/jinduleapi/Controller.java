package com.jindule.jinduleapi;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

@RestController // Restful Controller annotation
@Slf4j // 로그 기록하기 위한 annotation
public class Controller {
    final String RIOT_HEADER = "X-Riot-Token";
    final String RIOT_TOKEN = "RGAPI-d2263abd-3e15-4158-a594-747093ff8530";
    final String HC_ID = "jindule";
    final String HC_PWD = "0000";

    // RestTeamplate 사용 시 원인 불명 에러 발생 (헤더, 바디 문제 X)
    @PostMapping("/v1/riot/api")
    public ResponseEntity<?> riotApi(@RequestBody CommonReq req) {
        String url = "https://" + (req.getRegion() == null ? "kr" : req.getRegion()) + ".api.riotgames.com/lol";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add(RIOT_HEADER, RIOT_TOKEN);
        try {
            return new ResponseEntity<>(
                    restTemplate.exchange(
                            url + req.getPath(),
                            HttpMethod.resolve(req.getMethodType()),
                            new HttpEntity<>(headerMap),
                            String.class
                    ).getBody(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // https://limdevbasic.tistory.com/14
    @PostMapping("/v2/riot/api")
    public ResponseEntity<?> riotApi2(@RequestBody CommonReq req) {
        try {
            URL url = new URL("https://" + (req.getRegion() == null ? "kr" : req.getRegion()) + ".api.riotgames.com/lol" + req.getPath());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(req.getMethodType());
            connection.setRequestProperty(RIOT_HEADER, RIOT_TOKEN);

            int responseCode = connection.getResponseCode();

            // https://stackoverflow.com/questions/4633048/httpurlconnection-reading-response-content-on-403-error
            InputStream inputStream = connection.getErrorStream() == null ? connection.getInputStream() : connection.getErrorStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)  {
                stringBuffer.append(inputLine);
            }
            bufferedReader.close();
            connection.disconnect();

            return new ResponseEntity<>(stringBuffer.toString(), HttpStatus.resolve(responseCode));

        } catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 로그인 API. id, pwd 하드코딩된 데이터 사용
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        return new ResponseEntity<> (Objects.equals(req.getId(), HC_ID) && Objects.equals(req.getPwd(), HC_PWD), HttpStatus.OK);
    }

    // 캘린더 info API. 하드코드 데이터
    @PostMapping("/calendar")
    public ResponseEntity<?> calendar() {
        return new ResponseEntity<> (
                "[\n" +
                        "    {\n" +
                        "        \"name\": \"KT VS DK\",\n" +
                        "        \"start\": \"2022-05-29\",\n" +
                        "        \"color\": \"blue\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"T1 VS DRX\",\n" +
                        "        \"start\": \"2022-05-29\",\n" +
                        "        \"color\": \"indigo\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"LSB VS BRO\",\n" +
                        "        \"start\": \"2022-06-01\",\n" +
                        "        \"color\": \"deep-purple\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"GEN VS KDF\",\n" +
                        "        \"start\": \"2022-06-01\",\n" +
                        "        \"color\": \"cyan\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DK VS NS\",\n" +
                        "        \"start\": \"2022-06-02\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KT VS HLE\",\n" +
                        "        \"start\": \"2022-06-02\",\n" +
                        "        \"color\": \"green\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"NS VS KR\",\n" +
                        "        \"start\": \"2022-06-03\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KDF VS BRO\",\n" +
                        "        \"start\": \"2022-06-03\",\n" +
                        "        \"color\": \"orange\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DK VS KRX\",\n" +
                        "        \"start\": \"2022-06-04\",\n" +
                        "        \"color\": \"orange\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS LSB\",\n" +
                        "        \"start\": \"2022-06-04\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"GEN VS T1\",\n" +
                        "        \"start\": \"2022-06-05\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KDF VS KT\",\n" +
                        "        \"start\": \"2022-06-05\",\n" +
                        "        \"color\": \"deep-purple\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"NS VS LSB\",\n" +
                        "        \"start\": \"2022-06-08\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS DK\",\n" +
                        "        \"start\": \"2022-06-08\",\n" +
                        "        \"color\": \"green\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DRX VS GEN\",\n" +
                        "        \"start\": \"2022-06-09\",\n" +
                        "        \"color\": \"indigo\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"T1 VS BRO\",\n" +
                        "        \"start\": \"2022-06-09\",\n" +
                        "        \"color\": \"deep-purple\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS T1\",\n" +
                        "        \"start\": \"2022-06-10\",\n" +
                        "        \"color\": \"orange\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"GEN VS LSB\",\n" +
                        "        \"start\": \"2022-06-10\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KT VS DRX\",\n" +
                        "        \"start\": \"2022-06-11\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DK VS KDF\",\n" +
                        "        \"start\": \"2022-06-11\",\n" +
                        "        \"color\": \"blue\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"BRO VS NS\",\n" +
                        "        \"start\": \"2022-06-12\",\n" +
                        "        \"color\": \"green\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"LSB VS HLE\",\n" +
                        "        \"start\": \"2022-06-12\",\n" +
                        "        \"color\": \"blue\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KT VS KDF\",\n" +
                        "        \"start\": \"2022-06-15\",\n" +
                        "        \"color\": \"blue\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DRX VS T1\",\n" +
                        "        \"start\": \"2022-06-15\",\n" +
                        "        \"color\": \"indigo\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DK VS BRO\",\n" +
                        "        \"start\": \"2022-06-16\",\n" +
                        "        \"color\": \"orange\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"GEN VS NS\",\n" +
                        "        \"start\": \"2022-06-16\",\n" +
                        "        \"color\": \"deep-purple\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"BRO VS DRX\",\n" +
                        "        \"start\": \"2022-06-17\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"NS VS DK\",\n" +
                        "        \"start\": \"2022-06-17\",\n" +
                        "        \"color\": \"blue\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"T1 VS KDF\",\n" +
                        "        \"start\": \"2022-06-18\",\n" +
                        "        \"color\": \"orange\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS KT\",\n" +
                        "        \"start\": \"2022-06-18\",\n" +
                        "        \"color\": \"indigo\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"LSB VS GEN\",\n" +
                        "        \"start\": \"2022-06-19\",\n" +
                        "        \"color\": \"blue\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DRX VS DK\",\n" +
                        "        \"start\": \"2022-06-19\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"BRO VS T1\",\n" +
                        "        \"start\": \"2022-06-22\",\n" +
                        "        \"color\": \"indigo\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KT VS NS\",\n" +
                        "        \"start\": \"2022-06-22\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KDF VS LSB\",\n" +
                        "        \"start\": \"2022-06-23\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS GEN\",\n" +
                        "        \"start\": \"2022-06-23\",\n" +
                        "        \"color\": \"blue\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS KDF\",\n" +
                        "        \"start\": \"2022-06-24\",\n" +
                        "        \"color\": \"orange\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"NS VS BRO\",\n" +
                        "        \"start\": \"2022-06-24\",\n" +
                        "        \"color\": \"cyan\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"T1 VS KT\",\n" +
                        "        \"start\": \"2022-06-25\",\n" +
                        "        \"color\": \"green\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS DK\",\n" +
                        "        \"start\": \"2022-06-25\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"LSB VS NS\",\n" +
                        "        \"start\": \"2022-06-26\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"GEN VS DRX\",\n" +
                        "        \"start\": \"2022-06-26\",\n" +
                        "        \"color\": \"green\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"BRO VS KDF\",\n" +
                        "        \"start\": \"2022-06-29\",\n" +
                        "        \"color\": \"orange\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DK VS KT\",\n" +
                        "        \"start\": \"2022-06-29\",\n" +
                        "        \"color\": \"deep-purple\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"DRX VS LSB\",\n" +
                        "        \"start\": \"2022-06-30\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"K1 VS GEN\",\n" +
                        "        \"start\": \"2022-06-30\",\n" +
                        "        \"color\": \"green\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"KT VS DK\",\n" +
                        "        \"start\": \"2022-07-01\",\n" +
                        "        \"color\": \"deep-purple\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"HLE VS DK\",\n" +
                        "        \"start\": \"2022-07-01\",\n" +
                        "        \"color\": \"indigo\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"GEN VS DRX\",\n" +
                        "        \"start\": \"2022-07-02\",\n" +
                        "        \"color\": \"indigo\",\n" +
                        "        \"timed\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"name\": \"BRO VS NS\",\n" +
                        "        \"start\": \"2022-07-02\",\n" +
                        "        \"color\": \"grey darken-1\",\n" +
                        "        \"timed\": false\n" +
                        "    }\n" +
                        "]\n",
                HttpStatus.OK
        );
    }


    @Data
    public static class CommonReq {
        String region;
        String methodType;
        String path;
        String params;
    }

    @Data
    public static class LoginReq {
        String id;
        String pwd;
    }
}
