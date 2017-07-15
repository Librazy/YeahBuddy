package cn.edu.xmu.yeahbuddy;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
public class TutorTokenAuthTest extends ApplicationTestBase {

    @Test
    @Transactional
    public void tutorTokenAuthTest() throws Exception {
        Assert.assertTrue(tutorService.findByUsername("testtutor").isPresent());

        mvc.perform(get("/tutor/token?auth_token=" + token))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl(String.format("/tutor/%d/review", tutor.getId())))
           .andExpect(authenticated().withAuthenticationPrincipal(tutorService.findByUsername("testtutor").get()));
    }
}
