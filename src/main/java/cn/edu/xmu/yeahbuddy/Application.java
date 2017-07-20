package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.StageDto;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
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
            if (!administratorService.findByUsername("admin").isPresent()) {
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
                SecurityContextHolder.getContext().setAuthentication(null);
            }
            Optional<Team> team;
            if (!(team = teamService.findByUsername("team")).isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                team = Optional.of(teamService.registerNewTeam(
                        new TeamDto()
                                .setUsername("team")
                                .setPassword("team")
                                .setDisplayName("Team 1")
                                .setEmail("a@b.com")
                                .setPhone("18988888888")
                                .setProjectName("yeahbuddy")));
                teamService.registerNewTeam(
                        new TeamDto()
                                .setUsername("team2")
                                .setPassword("team2")
                                .setDisplayName("Team 2")
                                .setEmail("c@b.com")
                                .setPhone("18908888888")
                                .setProjectName("yeaddy"));
                SecurityContextHolder.getContext().setAuthentication(null);
            }

            Stage stage = stageService.createStage(201701,
                    new StageDto()
                            .setTitle("2017 01")
                            .setStart(Timestamp.valueOf("2017-01-01 20:00:00"))
                            .setEnd(Timestamp.valueOf("2017-03-01 20:00:00")));

            Report report = reportService.createReport(team.get(), stage, "Report");

            if (!tutorService.findByUsername("tutor").isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                Optional<Tutor> tutor = Optional.of(tutorService.registerNewTutor(
                        new TutorDto()
                                .setUsername("tutor")
                                .setPassword("tutor")
                                .setDisplayName("Tutor 1")
                                .setEmail("c@b.com")
                                .setPhone("13988888888")));
                Review review = reviewService.createReview(report, tutor.get());
                String token = tokenService.createToken(tutor.get(), Collections.singletonList(review), stage.getEnd()).getTokenValue();

                SecurityContextHolder.getContext().setAuthentication(null);

                log.info("Token created: " + token);
            }
        };
    }
}