package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.TutorRepository;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TutorService implements UserDetailsService {

    private final YbPasswordEncodeService ybPasswordEncodeService;

    private final TutorRepository tutorRepository;

    @Autowired
    public TutorService(TutorRepository tutorRepository, YbPasswordEncodeService ybPasswordEncodeService) {
        this.tutorRepository = tutorRepository;
        this.ybPasswordEncodeService = ybPasswordEncodeService;
    }

    @Override
    @Transactional(readOnly = true)
    public Tutor loadUserByUsername(String username) throws UsernameNotFoundException {
        Tutor tutor = tutorRepository.findByName(username);
        if (tutor == null) {
            throw new UsernameNotFoundException(username);
        }
        return tutor;
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterAdministrator')")
    public Tutor registerNewTeam(TutorDto dto) throws UsernameAlreadyExistsException {
        if(tutorRepository.findByName(dto.getName()) != null){
            throw new UsernameAlreadyExistsException("administrator.name.exist");
        }

        Tutor tutor = new Tutor(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));

        return tutorRepository.saveAndFlush(tutor);
    }
}
