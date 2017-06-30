package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.utils.AdministratorNoPermissionException;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdministratorService implements UserDetailsService {

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
        Administrator admin = administratorRepository.findByName(username);
        if (admin == null) {
            throw new UsernameNotFoundException(username);
        }
        return admin;
    }

    @Transactional(readOnly = true)
    public Administrator findByName(String name){
        return administratorRepository.findByName(name);
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterAdministrator')")
    public Administrator registerNewAdministrator(AdministratorDto dto, Administrator actor) throws UsernameAlreadyExistsException, AdministratorNoPermissionException {
        if(administratorRepository.findByName(dto.getName()) != null){
            throw new UsernameAlreadyExistsException("administrator.name.exist");
        }
        Administrator admin = new Administrator(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));

        admin.setAuthorities(dto.getAuthorities().stream().map(AdministratorPermission::valueOf).collect(Collectors.toSet()));

        final Set<AdministratorPermission> actorLacks = admin.getAuthorities().stream().filter(p -> !actor.getAuthorities().contains(p)).collect(Collectors.toSet());

        if(!actorLacks.isEmpty()){
            throw new AdministratorNoPermissionException("administrator.permission.lacks", actorLacks);
        }

        return administratorRepository.saveAndFlush(admin);
    }

    @Transactional
    public void deleteAdministrator(int id){
        administratorRepository.deleteById(id);
    }
}
