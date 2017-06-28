package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {

    List<Token> findByTutorId(int tutorId);

    Token findByTutorIdAndStage(int tutorId, int stage);

    List<Token> findByTimeBefore(Time time);
}
