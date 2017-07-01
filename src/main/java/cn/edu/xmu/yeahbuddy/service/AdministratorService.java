package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.utils.AdministratorNoPermissionException;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdministratorService implements UserDetailsService {

    private static Log log = LogFactory.getLog(AdministratorService.class);

    private final YbPasswordEncodeService ybPasswordEncodeService;

    private final AdministratorRepository administratorRepository;

    @Autowired
    public AdministratorService(AdministratorRepository administratorRepository, YbPasswordEncodeService ybPasswordEncodeService) {
        this.administratorRepository = administratorRepository;
        this.ybPasswordEncodeService = ybPasswordEncodeService;
    }

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

    @Transactional(readOnly = true)
    public Administrator findByName(String name) {
        return administratorRepository.findByName(name);
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterAdministrator')")
    public Administrator registerNewAdministrator(AdministratorDto dto, Administrator actor) throws UsernameAlreadyExistsException, AdministratorNoPermissionException {
        log.debug("Trying to register new Administrator " + dto.getName());
        if (administratorRepository.findByName(dto.getName()) != null) {
            log.info("Failed to register Administrator " + dto.getName() + ": name already exist");
            throw new UsernameAlreadyExistsException("administrator.name.exist");
        }

        log.debug("Determing if actor " + actor.getName() + " have sufficient authorities");

        Administrator admin = new Administrator(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));

        admin.setAuthorities(dto.getAuthorities().stream().map(AdministratorPermission::valueOf).collect(Collectors.toSet()));

        final Set<AdministratorPermission> actorLacks = admin.getAuthorities().stream().filter(p -> !actor.getAuthorities().contains(p)).collect(Collectors.toSet());

        if (!actorLacks.isEmpty()) {
            log.warn("Actor " + actor.getName() + " didn't have required authorities" + actorLacks.stream().map(AdministratorPermission::getAuthority).reduce("", (a, b) -> a + "," + b));
            throw new AdministratorNoPermissionException("administrator.permission.lacks", actorLacks);
        }
        Administrator result = administratorRepository.save(admin);
        log.debug("Registered new Administrator " + result.toString());
        return result;
    }

    @Transactional
    public void deleteAdministrator(int id) {
        log.debug("Deleting Administrator " + id);
        administratorRepository.deleteById(id);
    }
}
