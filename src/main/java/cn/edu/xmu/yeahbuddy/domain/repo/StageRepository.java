package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Stage;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Integer> {

    List<Stage> findByEndAfter(Timestamp timestamp);

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Stage> queryById(int id);
}
