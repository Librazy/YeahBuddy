package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService implements UserDetailsService {

    private static Log log = LogFactory.getLog(TeamService.class);

    private final YbPasswordEncodeService ybPasswordEncodeService;

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, YbPasswordEncodeService ybPasswordEncodeService) {
        this.teamRepository = teamRepository;
        this.ybPasswordEncodeService = ybPasswordEncodeService;
    }

    @Override
    @Transactional(readOnly = true)
    public Team loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Trying to load Team " + username);
        Team team = teamRepository.findByName(username);
        if (team == null) {
            log.info("Failed to load Team " + username + ": not found");
            throw new UsernameNotFoundException(username);
        }
        log.debug("Loaded Team " + username);
        return team;
    }

    @Transactional(readOnly = true)
    public Team findByName(String name) {
        return teamRepository.findByName(name);
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterTeam')")
    public Team registerNewTeam(TeamDto dto) throws UsernameAlreadyExistsException {
        log.debug("Trying to register new Team " + dto.getName());
        if (teamRepository.findByName(dto.getName()) != null) {
            log.info("Failed to register Team " + dto.getName() + ": name already exist");
            throw new UsernameAlreadyExistsException("team.name.exist");
        }

        Team team = new Team(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));
        team.setEmail(dto.getEmail());
        team.setPhone(dto.getPhone());
        team.setProjectName(dto.getProjectName());
        Team result = teamRepository.save(team);
        log.debug("Registered new Team " + result.toString());
        return result;
    }
}
