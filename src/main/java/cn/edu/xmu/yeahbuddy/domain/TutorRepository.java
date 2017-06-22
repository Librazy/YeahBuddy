package cn.edu.xmu.yeahbuddy.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Integer> {
    Tutor findByName(String name);
}
