package cn.edu.xmu.yeahbuddy;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
public class TeamFormLoginTest extends ApplicationTestBase {

    @Test
    @Transactional
    public void teamFormLoginTest() throws Exception {

        Assert.assertTrue(teamService.findByUsername("testteam").isPresent());
        mvc.perform(get("/team"))
           .andExpect(status().is3xxRedirection())
           .andExpect(unauthenticated());

        mvc.perform(formLogin("/team/login").user("testteam").password("testteam"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/team"))
           .andExpect(authenticated().withAuthenticationPrincipal(teamService.findByUsername("testteam").get()));

        mvc.perform(logout("/team/logout"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/team/login"))
           .andExpect(unauthenticated());

        mvc.perform(formLogin("/team/login").user("does").password("notcorrect"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/team/login?error"))
           .andExpect(unauthenticated());
    }
}
