package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
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

    @Contract(pure = true)
    public static Team asTeam(Object obj) {
        return ((Team) obj);
    }

    @Contract(pure = true)
    public static boolean isTeam(Object obj) {
        return obj instanceof Team;
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
        Team team = teamRepository.findByUsername(username);
        if (team == null) {
            log.info("Failed to load Team " + username + ": not found");
            throw new UsernameNotFoundException(username);
        }
        log.debug("Loaded Team " + username);
        return team;
    }

    /**
     * 查找团队 代理{@link TeamRepository#findByUsername(String)}
     *
     * @param username 查找的团队用户名
     * @return 团队或null
     */
    @Nullable
    @Transactional(readOnly = true)
    public Team findByUsername(String username) {
        log.debug("Finding Team " + username);
        return teamRepository.findByUsername(username);
    }

    /**
     * 注册团队
     *
     * @param dto 团队DTO
     * @return 新注册的团队
     * @throws IdentifierAlreadyExistsException 用户名已存在
     * @throws IllegalArgumentException DTO中未填满所需信息
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTeam')")
    public Team registerNewTeam(TeamDto dto) throws IdentifierAlreadyExistsException {
        log.debug("Trying to register new Team " + dto.getUsername());

        if(!dto.ready()){
            log.info("Failed to register Team " + dto.getUsername() + ": data not ready yet");
            throw new IllegalArgumentException("team.register.not_ready");
        }

        if (teamRepository.findByUsername(dto.getUsername()) != null) {
            log.info("Failed to register Team " + dto.getUsername() + ": username already exist");
            throw new IdentifierAlreadyExistsException("team.username.exist");
        }

        Team team = new Team(dto.getUsername(), ybPasswordEncodeService.encode(dto.getPassword()));
        team.setEmail(dto.getEmail());
        team.setPhone(dto.getPhone());
        team.setProjectName(dto.getProjectName());
        team.setDisplayName(dto.getDisplayName());

        Team result = teamRepository.save(team);
        log.debug("Registered new Team " + result.toString());
        return result;
    }

    /**
     * 按ID删除管理员
     *
     * @param id 管理员ID
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTeam')")
    public void deleteTeam(int id) {
        log.debug("Deleting Team " + id);
        teamRepository.deleteById(id);
    }

    /**
     * 修改团队信息
     * 需要当前主体有ManageTeam权限或当前主体即为被修改用户
     *
     * @param id  团队ID
     * @param dto 团队DTO
     * @return 修改后的团队
     * @throws IdentifierAlreadyExistsException 如果修改用户名，用户名已存在
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTeam') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.TeamService).isTeam(principal) && T(cn.edu.xmu.yeahbuddy.service.TeamService).asTeam(principal).id == #id)")
    public Team updateTeam(int id, TeamDto dto) {
        log.debug("Trying to update Team " + id);
        Team team = teamRepository.getOne(id);

        if(dto.getDisplayName() != null ){
            log.trace("Updated display name for Team " + id + ":" + team.getDisplayName() +
                              " -> " + dto.getDisplayName());
            team.setDisplayName(dto.getDisplayName());
        }

        if (dto.getEmail() != null) {
            log.trace("Updated email for Team " + id + ":" + team.getEmail() +
                              " -> " + dto.getEmail());
            team.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            log.trace("Updated phone for Team " + id + ":" + team.getPhone() +
                              " -> " + dto.getPhone());
            team.setPhone(dto.getPhone());
        }

        if (dto.getProjectName() != null) {
            log.trace("Updated project name for Team " + id + ":" + team.getProjectName() +
                              " -> " + dto.getProjectName());
            team.setProjectName(dto.getProjectName());
        }

        if (dto.getUsername() != null) {
            if (teamRepository.findByUsername(dto.getUsername()) != null) {
                log.info("Failed to update username for Team " + team.getUsername() + ": username already exist");
                throw new IdentifierAlreadyExistsException("team.username.exist");
            } else {
                log.trace("Updated username for Team " + id + ":" + team.getUsername() +
                                  " -> " + dto.getUsername());
                team.setUsername(dto.getUsername());
            }
        }
        return teamRepository.save(team);
    }

    /**
     * 修改团队密码
     * 需要当前主体有ManageTeam权限或当前主体即为被修改用户
     *
     * @param id          团队ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改后的团队
     * @throws BadCredentialsException 原密码不正确
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageTeam') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.TeamService).isTeam(principal) && T(cn.edu.xmu.yeahbuddy.service.TeamService).asTeam(principal).id == #id)")
    public Team updateTeamPassword(int id, CharSequence oldPassword, String newPassword) throws BadCredentialsException {
        log.info("Trying to update password for Team " + id);
        Team team = teamRepository.getOne(id);
        if (ybPasswordEncodeService.matches(oldPassword, team.getPassword())) {
            team.setPassword(ybPasswordEncodeService.encode(newPassword));
            log.info("Updated password for Team " + id);
            return teamRepository.save(team);
        } else {
            log.warn("Failed to update password for Team " + id + ": old password doesn't match");
            throw new BadCredentialsException("team.update.password");
        }
    }

    /**
     * 重置团队密码
     * 需要当前主体有ManageTeam权限与ResetPassword权限
     *
     * @param id          团队ID
     * @param newPassword 新密码
     * @return 修改后的团队
     */
    @Transactional
    @PreAuthorize("hasAuthority('ResetPassword') && hasAuthority('ManageTeam')")
    public Team resetTeamPassword(int id, String newPassword) {
        Team team = teamRepository.getOne(id);
        log.info("Reset password for Team " + id);
        team.setPassword(ybPasswordEncodeService.encode(newPassword));
        return teamRepository.save(team);
    }
}
