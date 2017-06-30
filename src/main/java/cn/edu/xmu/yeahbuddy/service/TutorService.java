package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.TutorRepository;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TutorService implements UserDetailsService, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

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

    @Transactional(readOnly = true)
    public Tutor loadTutorById(int id) throws UsernameNotFoundException {
        Optional<Tutor> tutor = tutorRepository.findById(id);
        if (!tutor.isPresent()) {
            throw new UsernameNotFoundException(Integer.toString(id));
        }
        return tutor.get();
    }

    @Transactional(readOnly = true)
    public Tutor findByName(String name) {
        return tutorRepository.findByName(name);
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterTutor')")
    public Tutor registerNewTutor(TutorDto dto) throws UsernameAlreadyExistsException {
        if (tutorRepository.findByName(dto.getName()) != null) {
            throw new UsernameAlreadyExistsException("administrator.name.exist");
        }

        Tutor tutor = new Tutor(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));
        tutor.setEmail(dto.getEmail());
        tutor.setPhone(dto.getPhone());
        return tutorRepository.save(tutor);
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        return (Tutor) token.getPrincipal();
    }
}
