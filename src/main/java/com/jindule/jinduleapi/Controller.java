package com.jindule.jinduleapi;

import com.jindule.jinduleapi.dto.CommonReq;
import com.jindule.jinduleapi.dto.LoginReq;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController // Restful Controller annotation
@Slf4j // 로그 기록하기 위한 annotation
public class Controller {
    final String RIOT_HEADER = "X-Riot-Token";
    final String RIOT_TOKEN = "RGAPI-3fd01589-6c40-4aed-a4b5-1f0383f0e0b7";
    final String HC_ID = "jindule";
    final String HC_PWD = "0000";

    // RestTeamplate 로 lol api (matches) 호출 시 원인 불명 에러 발생 (헤더, 바디 문제 X)
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

    // 캘린더 info API. resource의 calendar.json 데이터 파싱 // https://www.baeldung.com/spring-load-resource-as-string
    @PostMapping("/calendar")
    public ResponseEntity<?> calendar() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("calendar.json");
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return new ResponseEntity<> (FileCopyUtils.copyToString(reader), HttpStatus.OK);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
