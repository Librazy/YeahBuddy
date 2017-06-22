package cn.edu.xmu.yeahbuddy.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {
    Administrator findByName(String name);
}
