package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Token;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    List<Token> findByTutor(Tutor tutor);

    List<Token> findByEndBefore(Timestamp t);

    List<Token> findByEndAfter(Timestamp t);

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Token> queryByTokenValue(String value);
}
