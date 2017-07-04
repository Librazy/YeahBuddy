package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
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
        Administrator admin = administratorRepository.findByName(username);
        if (admin == null) {
            log.info("Failed to load Administrator " + username + ": not found");
            throw new UsernameNotFoundException(username);
        }
        log.debug("Loaded Administrator " + username);
        return admin;
    }

    /**
     * 查找管理员 代理{@link AdministratorRepository#findByName(String)}
     *
     * @param name 查找的管理员用户名
     * @return 管理员或null
     */
    @Nullable
    @Transactional(readOnly = true)
    public Administrator findByName(String name) {
        log.debug("Finding Administrator " + name);
        return administratorRepository.findByName(name);
    }

    /**
     * 注册管理员
     *
     * @param dto 管理员DTO
     * @return 新注册的管理员
     * @throws UsernameAlreadyExistsException 用户名已存在
     */
    @Transactional
    @PreAuthorize("hasAuthority('ManageAdministrator') and  authentication.authorities.containsAll(#dto.authorities)")
    public Administrator registerNewAdministrator(AdministratorDto dto) throws UsernameAlreadyExistsException {
        log.debug("Trying to register new Administrator " + dto.getName());
        if (administratorRepository.findByName(dto.getName()) != null) {
            log.info("Failed to register Administrator " + dto.getName() + ": name already exist");
            throw new UsernameAlreadyExistsException("admin.name.exist");
        }

        Administrator admin = new Administrator(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));

        admin.setAuthorities(dto.getAuthorities());

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
     *
     * @param id  管理员ID
     * @param dto 管理员DTO
     * @return 修改后的管理员
     * @throws UsernameAlreadyExistsException 如果修改用户名，用户名已存在
     */
    @Transactional
    @PreAuthorize("(hasAuthority('ManageAdministrator') and authentication.authorities.containsAll(#dto.authorities)) " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal) && T(cn.edu.xmu.yeahbuddy.service.AdministratorService).asAdministrator(principal).id == #id)")
    public Administrator updateAdminstrator(int id, AdministratorDto dto) {
        Administrator administrator = administratorRepository.getOne(id);
        if (!dto.getAuthorities().isEmpty()) {
            administrator.setAuthorities(dto.getAuthorities());
        }
        if (dto.getName() != null) {
            if (administratorRepository.findByName(dto.getName()) != null) {
                log.info("Fail to update Administrator " + administrator.getName() + ": name already exist");
                throw new UsernameAlreadyExistsException("admin.name.exist");
            } else {
                administrator.setName(dto.getName());
            }
        }
        return administratorRepository.save(administrator);

    }

    /**
     * 修改管理员密码
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
        Administrator admin = administratorRepository.getOne(id);
        if (ybPasswordEncodeService.matches(oldPassword, admin.getPassword())) {
            admin.setPassword(ybPasswordEncodeService.encode(newPassword));
            return administratorRepository.save(admin);
        } else {
            throw new BadCredentialsException("admin.update.password");
        }
    }
}
