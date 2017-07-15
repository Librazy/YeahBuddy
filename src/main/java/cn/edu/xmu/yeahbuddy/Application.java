package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.domain.repo.StageRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
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
                           final StageRepository stageRepository,
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
            int teamId = 0;
            if (!teamService.findByUsername("team").isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                Team team = teamService.registerNewTeam(
                        new TeamDto()
                                .setUsername("team")
                                .setPassword("team")
                                .setDisplayName("Team 1")
                                .setEmail("a@b.com")
                                .setPhone("18988888888")
                                .setProjectName("yeahbuddy"));
                teamService.registerNewTeam(
                        new TeamDto()
                                .setUsername("team2")
                                .setPassword("team2")
                                .setDisplayName("Team 2")
                                .setEmail("c@b.com")
                                .setPhone("18908888888")
                                .setProjectName("yeaddy"));
                teamId = team.getId();
                SecurityContextHolder.getContext().setAuthentication(null);
            }
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
                String token = tokenService.createToken(tutor.get(), 201701, Collections.singletonList(teamId));

                SecurityContextHolder.getContext().setAuthentication(null);

                log.info("Token created: " + token);
            }

            stageRepository.save(new Stage(201701, Timestamp.valueOf("2017-01-01 20:00:00"), Timestamp.valueOf("2017-03-01 20:00:00")));

            reportService.createReport(teamId, 201701, "Report");

            reviewService.createReview(teamId, 201701, tutor.get().getId(), false);
        };
    }
}