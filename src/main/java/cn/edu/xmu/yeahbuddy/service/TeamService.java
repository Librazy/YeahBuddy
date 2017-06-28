package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.utils.AdministratorNoPermissionException;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService implements UserDetailsService {

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
        Team team = teamRepository.findByName(username);
        if (team == null) {
            throw new UsernameNotFoundException(username);
        }
        return team;
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterTeam')")
    public Team registerNewTeam(TeamDto dto) throws UsernameAlreadyExistsException {
        if(teamRepository.findByName(dto.getName()) != null){
            throw new UsernameAlreadyExistsException("administrator.name.exist");
        }

        Team team = new Team(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));

        return teamRepository.saveAndFlush(team);
    }
}
