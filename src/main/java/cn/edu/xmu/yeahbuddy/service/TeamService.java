package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
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

    /**
     * @param teamRepository          Autowired
     * @param ybPasswordEncodeService Autowired
     */
    @Autowired
    public TeamService(TeamRepository teamRepository, YbPasswordEncodeService ybPasswordEncodeService) {
        this.teamRepository = teamRepository;
        this.ybPasswordEncodeService = ybPasswordEncodeService;
    }

    /**
     * 查找团队 提供{@link UserDetailsService#loadUserByUsername(String)}
     *
     * @param username 查找的团队用户名
     * @return 团队
     * @throws UsernameNotFoundException 找不到团队
     */
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

    /**
     * 查找团队 代理{@link TeamRepository#findByName(String)}
     *
     * @param name 查找的团队用户名
     * @return 团队或null
     */
    @Nullable
    @Transactional(readOnly = true)
    public Team findByName(String name) {
        log.debug("Finding Team " + name);
        return teamRepository.findByName(name);
    }

    /**
     * 注册团队
     *
     * @param dto 团队DTO
     * @return 新注册的团队
     * @throws UsernameAlreadyExistsException 用户名已存在
     */
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
