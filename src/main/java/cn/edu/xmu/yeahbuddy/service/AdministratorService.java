package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import cn.edu.xmu.yeahbuddy.utils.IdentifierNotExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 管理员账户服务
 */
@Service
public class AdministratorService implements UserDetailsService {

    @NonNls
    private static Log log = LogFactory.getLog(AdministratorService.class);

    private final YbPasswordEncodeService ybPasswordEncodeService;

    private final AdministratorRepository administratorRepository;

    /**
     * @param administratorRepository Autowired
     * @param ybPasswordEncodeService Autowired
     */
    @Autowired
    public AdministratorService(AdministratorRepository administratorRepository, YbPasswordEncodeService ybPasswordEncodeService) {
        this.administratorRepository = administratorRepository;
        this.ybPasswordEncodeService = ybPasswordEncodeService;
    }

    @Contract(pure = true)
    public static Administrator asAdministrator(Object obj) {
        return ((Administrator) obj);
    }

    @Contract(pure = true)
    public static boolean isAdministrator(Object obj) {
        return obj instanceof Administrator;
    }

    /**
     * 查找管理员 提供{@link UserDetailsService#loadUserByUsername(String)}
     *
     * @param username 查找的管理员用户名
     * @return 管理员
     * @throws UsernameNotFoundException 找不到管理员
     */
    @Override
    @Transactional(readOnly = true)
    public Administrator loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Trying to load Administrator " + username);
        Optional<Administrator> admin = administratorRepository.findByUsername(username);
        if (!admin.isPresent()) {
            log.info("Failed to load Administrator " + username + ": not found");
            throw new UsernameNotFoundException(username);
        }
        log.debug("Loaded Administrator " + username);
        return admin.get();
    }

    /**
     * 查找管理员 代理{@link AdministratorRepository#findByUsername(String)}
     *
     * @param username 查找的管理员用户名
     * @return 管理员
     */
    @NotNull
    @Transactional(readOnly = true)
    public Optional<Administrator> findByUsername(String username) {
        log.debug("Finding Administrator " + username);
        return administratorRepository.findByUsername(username);
    }

    /**
     * 按ID查找管理员
     *
     * @param id 查找的管理员用户名
     * @return 管理员
     */
    @Transactional(readOnly = true)
    public Optional<Administrator> findById(int id) {
        log.debug("Finding Administrator " + id);
        return administratorRepository.findById(id);
    }

    /**
     * 注册管理员
     *
     * @param dto 管理员DTO
     * @return 新注册的管理员
     * @throws IdentifierAlreadyExistsException 用户名已存在
     * @throws IllegalArgumentException         DTO中未填满所需信息
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageAdministrator') and  authentication.authorities.containsAll(#dto.authorities)")
    public Administrator registerNewAdministrator(AdministratorDto dto) throws IdentifierAlreadyExistsException {
        log.debug("Trying to register new Administrator " + dto.getUsername());

        if (!dto.ready()) {
            log.info("Failed to register Administrator " + dto.getUsername() + ": data not ready yet");
            throw new IllegalArgumentException("admin.register.not_ready");
        }

        if (administratorRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.info("Failed to register Administrator " + dto.getUsername() + ": username already exist");
            throw new IdentifierAlreadyExistsException("admin.username.exist", dto.getUsername());
        }

        Administrator admin = new Administrator(dto.getUsername(), ybPasswordEncodeService.encode(dto.getPassword()));
        admin.setAuthorities(dto.getAuthorities());
        admin.setDisplayName(dto.getDisplayName());
        admin.setEmail(dto.getEmail());
        admin.setPhone(dto.getPhone());

        Administrator result = administratorRepository.save(admin);
        log.debug("Registered new Administrator " + result.toString());
        return result;
    }

    /**
     * 按ID删除管理员
     *
     * @param id 管理员ID
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageAdministrator')")
    public void deleteAdministrator(int id) {
        log.debug("Deleting Administrator " + id);
        administratorRepository.deleteById(id);
    }

    /**
     * 修改管理员信息
     * 需要当前主体有ManageAdministrator权限或当前主体即为被修改用户
     *
     * @param id  管理员ID
     * @param dto 管理员DTO
     * @return 修改后的管理员
     * @throws IdentifierAlreadyExistsException 如果修改用户名，用户名已存在
     */
    @Transactional
    @PreAuthorize("(hasAuthority('ManageAdministrator') && ((#dto.authorities == null) || authentication.authorities.containsAll(#dto.authorities))) " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal) && T(cn.edu.xmu.yeahbuddy.service.AdministratorService).asAdministrator(principal).id == #id)")
    public Administrator updateAdministrator(int id, AdministratorDto dto) {

        log.debug("Trying to update Administrator " + id);
        Administrator administrator = loadForUpdate(id);

        if (dto.getAuthorities() != null) {
            log.trace("Updated authorities for Administrator " + id);
            administrator.setAuthorities(dto.getAuthorities());
        }

        if (dto.getDisplayName() != null) {
            log.trace("Updated display name for Administrator " + id + ":" + administrator.getDisplayName() +
                              " -> " + dto.getDisplayName());
            administrator.setDisplayName(dto.getDisplayName());
        }

        if (dto.getEmail() != null) {
            log.trace("Updated email for Administrator " + id + ":" + administrator.getEmail() +
                              " -> " + dto.getEmail());
            administrator.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            log.trace("Updated phone for Administrator " + id + ":" + administrator.getPhone() +
                              " -> " + dto.getPhone());
            administrator.setPhone(dto.getPhone());
        }

        if (dto.getUsername() != null) {
            if (administratorRepository.findByUsername(dto.getUsername()).isPresent()) {
                log.info("Fail to update username for Administrator " + dto.getUsername() + ": name already exist");
                throw new IdentifierAlreadyExistsException("admin.username.exist", dto.getUsername());
            } else {
                log.trace("Updated username for Administrator " + id + ":" + administrator.getUsername() +
                                  " -> " + dto.getUsername());
                administrator.setUsername(dto.getUsername());
            }
        }

        return administratorRepository.save(administrator);

    }

    /**
     * 修改管理员密码
     * 需要当前主体有ManageAdministrator权限或当前主体即为被修改用户
     *
     * @param id          管理员ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改后的管理员
     * @throws BadCredentialsException 原密码不正确
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageAdministrator') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal) && T(cn.edu.xmu.yeahbuddy.service.AdministratorService).asAdministrator(principal).id == #id)")
    public Administrator updateAdministratorPassword(int id, CharSequence oldPassword, String newPassword) throws BadCredentialsException {
        log.info("Trying to update password for Administrator " + id);
        Administrator administrator = loadForUpdate(id);
        if (ybPasswordEncodeService.matches(oldPassword, administrator.getPassword())) {
            administrator.setPassword(ybPasswordEncodeService.encode(newPassword));
            log.info("Updated password for Administrator " + id);
            return administratorRepository.save(administrator);
        } else {
            log.warn("Failed to update password for Administrator " + id + ": old password doesn't match");
            throw new BadCredentialsException("admin.update.password");
        }
    }

    /**
     * 重置管理员密码
     * 需要当前主体有ManageAdministrator权限与ResetPassword权限
     *
     * @param id          管理员ID
     * @param newPassword 新密码
     * @return 修改后的管理员
     */
    @Transactional
    @PreAuthorize("hasAuthority('ResetPassword') && hasAuthority('ManageAdministrator')")
    public Administrator resetAdministratorPassword(int id, String newPassword) {
        Administrator administrator = loadForUpdate(id);
        log.info("Reset password for Administrator " + id);
        administrator.setPassword(ybPasswordEncodeService.encode(newPassword));
        return administratorRepository.save(administrator);
    }

    @NotNull
    private Administrator loadForUpdate(int id) {
        Optional<Administrator> admin = administratorRepository.queryById(id);

        if (!admin.isPresent()) {
            log.info("Failed to load Administrator " + id + ": not found");
            throw new IdentifierNotExistsException("admin.id.not_found", id);
        }

        return admin.get();
    }
}
