package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.domain.repo.StageRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.TokenRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.StageDto;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.service.*;
import org.jetbrains.annotations.NonNls;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
@AutoConfigureMockMvc
public abstract class ApplicationTestBase extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    TutorService tutorService;

    @Autowired
    TeamService teamService;

    @Autowired
    TokenService tokenService;

    @Autowired
    StageService stageService;

    @Autowired
    AdministratorService administratorService;

    @Autowired
    ReportService reportService;

    @Autowired
    ReviewService reviewService;

    @NonNls
    String token;

    Tutor tutor1;

    Team team1;

    Team team2;

    Report report;

    Review review;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeTransaction
    public void setUp() throws Exception {
        new TransactionTemplate(transactionManager).execute(status -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(Administrator.AdministratorPermission.values()));
            SecurityContextHolder.getContext().setAuthentication(ultimate);

            administratorService.registerNewAdministrator(
                    new AdministratorDto()
                            .setUsername("some")
                            .setPassword("one")
                            .setDisplayName("some")
                            .setAuthorities(
                                    Stream.of(Administrator.AdministratorPermission.values())
                                          .map(Administrator.AdministratorPermission::name)
                                          .collect(Collectors.toSet())));

            Stage stage = stageService.createStage(201701, new StageDto()
                                                                   .setTitle("2017 01")
                                                                   .setStart(Timestamp.valueOf("2017-01-01 20:00:00"))
                                                                   .setEnd(Timestamp.valueOf("2017-03-01 20:00:00")));


            team1 = teamService.registerNewTeam(
                    new TeamDto()
                            .setUsername("testteam")
                            .setPassword("testteam")
                            .setDisplayName("testteam")
                            .setEmail("a@b.com")
                            .setPhone("18988888888")
                            .setProjectName("yeahbuddy"));
            team2 = teamService.registerNewTeam(
                    new TeamDto()
                            .setUsername("test2team")
                            .setPassword("test2team")
                            .setDisplayName("test2team")
                            .setEmail("a2@b.com")
                            .setPhone("18288888888")
                            .setProjectName("nyaacat"));
            tutor1 = tutorService.registerNewTutor(
                    new TutorDto()
                            .setUsername("testtutor")
                            .setPassword("testtutor")
                            .setDisplayName("testtutor")
                            .setEmail("c@b.com")
                            .setPhone("13988888888"));

            report = reportService.createReport(team1, stage, "Report");

            review = reviewService.createReview(report, tutor1);

            token = tokenService.createToken(tutor1, Collections.singletonList(review)).getTokenValue();

            SecurityContextHolder.getContext().setAuthentication(null);
            return null;
        });
    }

    @AfterTransaction
    public void tearDown() {
        new TransactionTemplate(transactionManager).execute(status -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(Administrator.AdministratorPermission.values()));
            SecurityContextHolder.getContext().setAuthentication(ultimate);
            tokenRepository.deleteAll();
            reviewService.deleteReview(review.getId());
            reportService.deleteReport(report.getId());
            teamService.deleteTeam(teamService.loadUserByUsername("testteam").getId());
            teamService.deleteTeam(teamService.loadUserByUsername("test2team").getId());
            tutorService.deleteTutor(tutorService.loadUserByUsername("testtutor").getId());
            stageRepository.deleteAllInBatch();
            administratorService.deleteAdministrator(administratorService.loadUserByUsername("some").getId());
            SecurityContextHolder.getContext().setAuthentication(null);
            return null;
        });
    }
}
