package cn.edu.xmu.yeahbuddy.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByTutorId(int tutorId);

    List<Token> findByTimeBefore(Time time);
}
