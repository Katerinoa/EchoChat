package cn.edu.xmu.echochat.Bo;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class Translation {
    @Value("${spring.Baidu.appid}")
    private String appid;

    @Value("${spring.Baidu.key}")
    private String key;

    @Value("${spring.Baidu.url}")
    private String url;

    public String translate(String content, Integer op) {
        Random random = new Random(10);
        String salt = Integer.toString(random.nextInt());
        String sign = DigestUtils.md5DigestAsHex((appid + content + salt + key).getBytes());

        String from = "", to = "";
        if (op == 1) {
            from = "zh";
            to = "en";
        } else if (op == 2) {
            from = "en";
            to = "zh";
        }
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("q",content);
        paramMap.add("from", from);
        paramMap.add("to", to);
        paramMap.add("appid", appid);
        paramMap.add("salt",salt);
        paramMap.add("sign",sign);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(paramMap,headers);


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.postForEntity(url, httpEntity, Object.class);

        String pattern = "dst=([^\\]}]*)";;
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response.getBody().toString());
        if (m.find()) {
            return m.group(1);
        } else {
            return "不正确的请求";
        }
    }
}
