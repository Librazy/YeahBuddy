package cn.edu.xmu.yeahbuddy.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    Team findByName(String name);

    Team findByProjectName(String projectName);
}
