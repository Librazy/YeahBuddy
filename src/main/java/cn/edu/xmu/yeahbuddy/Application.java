package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.service.TokenService;
import cn.edu.xmu.yeahbuddy.service.TutorService;
import cn.edu.xmu.yeahbuddy.web.MainController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.TestOnly;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
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
    CommandLineRunner init(final AdministratorService administratorService,
                           final TeamService teamService,
                           final TutorService tutorService,
                           final TokenService tokenService) {
        return args -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
            if (administratorService.findByUsername("admin") == null) {
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
            if (teamService.findByUsername("team") == null) {
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

            if (tutorService.findByUsername("tutor") == null) {
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                Tutor tutor = tutorService.registerNewTutor(
                        new TutorDto()
                                .setUsername("tutor")
                                .setPassword("tutor")
                                .setDisplayName("Tutor 1")
                                .setEmail("c@b.com")
                                .setPhone("13988888888"));
                String token = tokenService.createToken(tutor, 1, Collections.singletonList(teamId));

                SecurityContextHolder.getContext().setAuthentication(null);

                log.info("Token created: " + token);
            }
        };

    }

}