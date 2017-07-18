package cn.edu.xmu.yeahbuddy;

import org.jetbrains.annotations.NonNls;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Rollback
public class RestTemplateTest extends ApplicationTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @Transactional
    public void restTemplateTest() throws Exception {
        ResponseEntity<String> response = testRestTemplate.getForEntity(new URI("http", null, "localhost", port, "/tutor/token", String.format("auth_token=%s", token), null), String.class);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.FOUND);
        @NonNls URI redirect = response.getHeaders().getLocation();
        if (redirect == null) throw new RuntimeException();
        Assert.assertTrue(redirect.getPath().equals(String.format("/tutor/%d/review", tutor.getId())));
    }
}
