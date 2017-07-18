package cn.edu.xmu.yeahbuddy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NonNls;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Rollback
public class RestTemplateTest extends ApplicationTestBase {

    @Autowired
    private MessageSource messageSource;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @Transactional
    public void tutorTokenRestTemplateTest() throws Exception {
        ResponseEntity<String> response = testRestTemplate.getForEntity(new URI("http", null, "localhost", port, "/tutor/token", String.format("auth_token=%s", token), null), String.class);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.FOUND);
        @NonNls URI redirect = response.getHeaders().getLocation();
        Assert.assertNotNull(redirect);
        Assert.assertTrue(redirect.getPath().equals(String.format("/tutor/%d/review", tutor.getId())));
    }

    @Test
    public void teamProfileRestTemplateTest() throws Exception {

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "testteam");
        map.add("password", "testteam");

        HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(map, loginHeaders);
        ResponseEntity<String> loginResponse = testRestTemplate.exchange(new URI("http", null, "localhost", port, "/team/login", null, null), HttpMethod.POST, loginEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> cookies = new HashMap<>();
        List<String> setCookie = loginResponse.getHeaders().get("Set-Cookie");
        if (setCookie != null && !setCookie.isEmpty()) {
            String cookiesStr = setCookie.get(0);
            String[] cookiesSplit = cookiesStr.split(";");
            for (String cookieStr : cookiesSplit) {
                if (!cookieStr.contains("=")) continue;
                String[] keyValueSplit = cookieStr.split("=");
                cookies.put(keyValueSplit[0], keyValueSplit[1]);
            }
        }

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cookie", cookies.entrySet().stream().map((entry) -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(";")));
        HttpEntity<String> httpEntity = new HttpEntity<>("displayName=qwerteam", headers);

        ResponseEntity<String> response = testRestTemplate.exchange(new URI("http", null, "localhost", port, String.format("/team/%d", team1.getId()), "locale=zh", null), HttpMethod.PUT, httpEntity, String.class);
        JsonNode root = mapper.readTree(response.getBody());
        Assert.assertEquals(messageSource.getMessage("response.ok", new Object[]{}, Locale.CHINESE), root.get("status").asText());


        httpEntity = new HttpEntity<>("username=test2team", headers);
        response = testRestTemplate.exchange(new URI("http", null, "localhost", port, String.format("/team/%d", team1.getId()), "locale=en", null), HttpMethod.PUT, httpEntity, String.class);

        root = mapper.readTree(response.getBody());
        Assert.assertEquals(HttpStatus.CONFLICT.value(), root.get("status").asInt());
        Assert.assertEquals(messageSource.getMessage("team.username.exist", new Object[]{"test2team"}, Locale.ENGLISH), root.get("message").asText());
    }
}
