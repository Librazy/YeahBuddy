package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.service.TokenService;
import cn.edu.xmu.yeahbuddy.service.TutorService;
import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@AutoConfigureMockMvc
public abstract class ApplicationTestBase extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    TutorService tutorService;

    @Autowired
    TeamService teamService;

    @NonNls
    String token;

    @Autowired
    private TokenService tokenService;

    @Before
    public void setUp() throws Exception {
        Administrator ultimate = new Administrator();
        ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
        SecurityContextHolder.getContext().setAuthentication(ultimate);
        Team team = teamService.registerNewTeam(
                new TeamDto()
                        .setUsername("testteam")
                        .setPassword("testteam")
                        .setDisplayName("testteam")
                        .setEmail("a@b.com")
                        .setPhone("18988888888")
                        .setProjectName("yeahbuddy"));
        int teamId = team.getId();

        Tutor tutor = tutorService.registerNewTutor(
                new TutorDto()
                        .setUsername("testtutor")
                        .setPassword("testtutor")
                        .setDisplayName("testtutor")
                        .setEmail("c@b.com")
                        .setPhone("13988888888"));
        token = tokenService.createToken(tutor, 2017, Collections.singletonList(teamId));

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
