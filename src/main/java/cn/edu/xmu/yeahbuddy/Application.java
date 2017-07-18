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

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @NonNls
    private static Log log = LogFactory.getLog(MainController.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

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
            ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
            if (!administratorService.findByUsername("admin").isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                administratorService.registerNewAdministrator(
                        new AdministratorDto()
                                .setUsername("admin")
                                .setPassword("admin")
                                .setDisplayName("Admin 1")
                                .setAuthorities(
                                        Arrays.stream(AdministratorPermission.values())
                                              .map(AdministratorPermission::name)
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

            Optional<Tutor> tutor;
            if (!(tutor = tutorService.findByUsername("tutor")).isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                tutor = Optional.of(tutorService.registerNewTutor(
                        new TutorDto()
                                .setUsername("tutor")
                                .setPassword("tutor")
                                .setDisplayName("Tutor 1")
                                .setEmail("c@b.com")
                                .setPhone("13988888888")));
                String token = tokenService.createToken(tutor.get(), stage, Collections.singletonList(team.get())).getTokenValue();

                SecurityContextHolder.getContext().setAuthentication(null);

                log.info("Token created: " + token);
            }

            reportService.createReport(team.get(), stage, "Report");

            reviewService.createReview(team.get(), stage, tutor.get());
        };
    }
}