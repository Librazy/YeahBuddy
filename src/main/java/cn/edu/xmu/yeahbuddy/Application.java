package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.model.*;
import cn.edu.xmu.yeahbuddy.service.*;
import cn.edu.xmu.yeahbuddy.web.MainController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.TestOnly;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Boot 入口类
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @NonNls
    private static Log log = LogFactory.getLog(MainController.class);

    /**
     * 标准Jar入口点
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * WAR入口点配置
     *
     * @param application SpringApplicationBuilder
     * @return SpringApplicationBuilder
     * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/howto-traditional-deployment.html">Spring Boot Reference Guide Part IX. ‘How-to’ guides	 85. Traditional deployment</a>
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /**
     * 生成调试用预置数据
     *
     * @param administratorService Autowire
     * @param teamService          Autowire
     * @param tutorService         Autowire
     * @param tokenService         Autowire
     * @param reportService        Autowire
     * @param stageService         Autowire
     * @param reviewService        Autowire
     * @return CommandLineRunner
     */
    @Bean
    @TestOnly
    @ConditionalOnProperty(name = "debug")
    CommandLineRunner init(final AdministratorService administratorService,
                           final TeamService teamService,
                           final TutorService tutorService,
                           final TokenService tokenService,
                           final ReportService reportService,
                           final StageService stageService,
                           final ReviewService reviewService) {
        return args -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(Administrator.AdministratorPermission.values()));
            SecurityContextHolder.getContext().setAuthentication(ultimate);
            administratorService.registerNewAdministrator(
                    new AdministratorDto()
                            .setUsername("admin")
                            .setPassword("admin")
                            .setDisplayName("Admin 1")
                            .setAuthorities(
                                    Arrays.stream(Administrator.AdministratorPermission.values())
                                          .map(Administrator.AdministratorPermission::name)
                                          .collect(Collectors.toSet())));


            Team team = teamService.registerNewTeam(
                    new TeamDto()
                            .setUsername("team")
                            .setPassword("team")
                            .setDisplayName("Team 1")
                            .setEmail("a@b.com")
                            .setPhone("18988888888")
                            .setProjectName("yeahbuddy"));

            Team team2 = teamService.registerNewTeam(
                    new TeamDto()
                            .setUsername("team2")
                            .setPassword("team2")
                            .setDisplayName("Team 2")
                            .setEmail("c@b.com")
                            .setPhone("18908888888")
                            .setProjectName("yeaddy"));

            Team team3 = teamService.registerNewTeam(
                    new TeamDto()
                            .setUsername("team3")
                            .setPassword("team3")
                            .setDisplayName("Team 3")
                            .setEmail("e@b.com")
                            .setPhone("18908888888")
                            .setProjectName("yeaddy"));

            Stage stage = stageService.createStage(201701,
                    new StageDto()
                            .setTitle("2017 01")
                            .setStart(Timestamp.valueOf("2017-01-01 20:00:00"))
                            .setEnd(Timestamp.valueOf("2017-08-01 20:00:00")));
            List<String> content = new ArrayList<>();
            content.add("报告内容1");
            content.add("报告内容2");
            content.add("报告内容3");

            Report report = reportService.createReport(team, stage, "Report");
            reportService.updateReport(report.getId(), new ReportDto().setContent(content));
            Report report2 = reportService.createReport(team2, stage, "Report 2");
            reportService.updateReport(report2.getId(), new ReportDto().setContent(content));

            Tutor tutor = tutorService.registerNewTutor(
                    new TutorDto()
                            .setUsername("tutor")
                            .setPassword("tutor")
                            .setDisplayName("Tutor 1")
                            .setEmail("c@b.com")
                            .setPhone("13988888888"));

            Tutor tutor2 = tutorService.registerNewTutor(
                    new TutorDto()
                            .setUsername("tutor2")
                            .setPassword("tutor2")
                            .setDisplayName("Tutor 2")
                            .setEmail("c@b.com")
                            .setPhone("13988888888"));

            Tutor tutor3 = tutorService.registerNewTutor(
                    new TutorDto()
                            .setUsername("tutor3")
                            .setPassword("tutor3")
                            .setDisplayName("Tutor 3")
                            .setEmail("c@b.com")
                            .setPhone("13988888888"));
            Review review = reviewService.createReview(report, tutor);
            reviewService.createReview(report, tutor2);
            reviewService.createReview(report, tutor3);
            String token = tokenService.createToken(tutor, Collections.singleton(review), stage.getEnd()).getTokenValue();

            SecurityContextHolder.getContext().setAuthentication(null);

            log.info("Token created: " + token);

        };
    }
}