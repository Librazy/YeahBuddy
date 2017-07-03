package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService implements UserDetailsService {

    @NonNls
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

    /**
     * 修改团队信息
     *
     * @param id 团队iD
     * @param dto 团队DTO
     * @return 修改后的团队
     * @throws UsernameAlreadyExistsException 如果修改用户名，用户名已存在
     */
    @Transactional
    public Team updateTeam(int id, TeamDto dto) {
        Team team = teamRepository.getOne(id);
        if(dto.getEmail() != null){
            team.setEmail(dto.getEmail());
        }
        if(dto.getPhone() != null){
            team.setPhone(dto.getPhone());
        }
        if(dto.getProjectName() != null){
            team.setProjectName(dto.getProjectName());
        }
        if(dto.getName() != null){
            if (teamRepository.findByName(dto.getName()) != null) {
                log.info("Failed to update Team " + team.getName() + ": name already exist");
                throw new UsernameAlreadyExistsException("team.name.exist");
            }
        }
        return teamRepository.save(team);
    }

    /**
     * 修改团队信息
     *
     * @param id 团队iD
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改后的团队
     * @throws BadCredentialsException 原密码不正确
     */
    @Transactional
    public Team updateTeamPassword(int id, CharSequence oldPassword, String newPassword) throws BadCredentialsException{
        Team team = teamRepository.getOne(id);
        if(ybPasswordEncodeService.matches(oldPassword, team.getPassword())){
            team.setPassword(ybPasswordEncodeService.encode(newPassword));
            return teamRepository.save(team);
        } else {
            throw new BadCredentialsException("team.update.password");
        }
    }
}
