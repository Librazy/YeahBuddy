package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Stage;
import cn.edu.xmu.yeahbuddy.domain.Token;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {

    List<Token> findByTutor(Tutor tutor);

    @NotNull
    Optional<Token> findByTutorAndStage(Tutor tutor, Stage stage);

    List<Token> findByTimeBefore(Time time);

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Token> queryByTokenValue(String value);
}
