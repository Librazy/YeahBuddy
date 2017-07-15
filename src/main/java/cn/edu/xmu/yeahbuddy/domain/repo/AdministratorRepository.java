package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {

    Optional<Administrator> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Administrator> queryById(int id);
}
