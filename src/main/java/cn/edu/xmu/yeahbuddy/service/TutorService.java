package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.TutorRepository;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static Log log = LogFactory.getLog(TutorService.class);

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
        log.debug("Trying to load Tutor " + username);
        Tutor tutor = tutorRepository.findByName(username);
        if (tutor == null) {
            log.info("Failed to load Tutor " + username + ": not found");
            throw new UsernameNotFoundException(username);
        }
        log.debug("Loaded Tutor " + username);
        return tutor;
    }

    @Transactional(readOnly = true)
    public Tutor loadTutorById(int id) throws UsernameNotFoundException {
        log.debug("Trying to load Tutor id " + id);
        Optional<Tutor> tutor = tutorRepository.findById(id);
        if (!tutor.isPresent()) {
            log.info("Failed to load Tutor id" + id + ": not found");
            throw new UsernameNotFoundException(Integer.toString(id));
        }
        log.debug("Loaded Tutor id " + id);
        return tutor.get();
    }

    @Transactional(readOnly = true)
    public Tutor findByName(String name) {
        return tutorRepository.findByName(name);
    }

    @Transactional
    @PreAuthorize("hasAuthority('RegisterTutor')")
    public Tutor registerNewTutor(TutorDto dto) throws UsernameAlreadyExistsException {
        log.debug("Trying to register new Tutor " + dto.getName());
        if (tutorRepository.findByName(dto.getName()) != null) {
            log.info("Failed to register Tutor " + dto.getName() + ": name already exist");
            throw new UsernameAlreadyExistsException("administrator.name.exist");
        }

        Tutor tutor = new Tutor(dto.getName(), ybPasswordEncodeService.encode(dto.getPassword()));
        tutor.setEmail(dto.getEmail());
        tutor.setPhone(dto.getPhone());
        Tutor result = tutorRepository.save(tutor);
        log.debug("Registered new Tutor " + result.toString());
        return result;
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        log.debug("Trying to load Tutor PreAuthenticatedAuthenticationToken " + token);
        return (Tutor) token.getPrincipal();
    }
}
