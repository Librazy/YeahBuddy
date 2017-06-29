package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(final AdministratorRepository administratorRepository,
                           final AdministratorService administratorService,
                           final TeamRepository teamRepository,
                           final TeamService teamService) {
        return args -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
            if(administratorRepository.findByName("admin") == null){

                SecurityContextHolder.getContext().setAuthentication(ultimate);
                administratorService.registerNewAdministrator(
                        new AdministratorDto()
                                .setName("admin")
                                .setPassword("admin")
                                .setAuthorities(
                                        Arrays.stream(AdministratorPermission.values())
                                              .map(Enum::name)
                                              .collect(Collectors.toSet())),
                        ultimate);
                SecurityContextHolder.getContext().setAuthentication(null);
            }

            if(teamRepository.findByName("team") == null){
                SecurityContextHolder.getContext().setAuthentication(ultimate);
                teamService.registerNewTeam(
                        new TeamDto()
                                .setName("team")
                                .setPassword("team")
                                .setEmail("a@b.com")
                                .setPhone("18988888888")
                                .setProjectName("yeahbuddy"));
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        };

    }

}