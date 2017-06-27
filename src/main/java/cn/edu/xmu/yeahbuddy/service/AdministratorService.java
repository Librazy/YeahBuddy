package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.utils.AdministratorNoPermissionException;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdministratorService implements UserDetailsService {

    private final YbPasswordEncoder ybPasswordEncoder;

    private final AdministratorRepository administratorRepository;

    @Autowired
    public AdministratorService(AdministratorRepository administratorRepository, YbPasswordEncoder ybPasswordEncoder) {
        this.administratorRepository = administratorRepository;
        this.ybPasswordEncoder = ybPasswordEncoder;
    }

    @Override
    public Administrator loadUserByUsername(String username) throws UsernameNotFoundException {
        Administrator admin = administratorRepository.findByName(username);
        if (admin == null) {
            throw new UsernameNotFoundException(username);
        }
        return admin;
    }

    @Transactional
    public Administrator registerNewAdministrator(AdministratorDto dto, Administrator actor) throws UsernameAlreadyExistsException, AdministratorNoPermissionException {
        if(administratorRepository.findByName(dto.getName()) != null){
            throw new UsernameAlreadyExistsException("administrator.name.exist");
        }
        Administrator admin = new Administrator(dto.getName(), ybPasswordEncoder.encode(dto.getPassword()));

        admin.setAuthorities(dto.getAuthorities().stream().map(AdministratorPermission::valueOf).collect(Collectors.toSet()));

        final Set<AdministratorPermission> actorLacks = admin.getAuthorities().stream().filter(p -> !actor.getAuthorities().contains(p)).collect(Collectors.toSet());

        if(!actorLacks.isEmpty()){
            throw new AdministratorNoPermissionException("administrator.permission.lacks", actorLacks);
        }

        return administratorRepository.saveAndFlush(admin);
    }
}
