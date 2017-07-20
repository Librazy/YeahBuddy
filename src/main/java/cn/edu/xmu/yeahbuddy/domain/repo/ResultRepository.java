package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Integer>  {
    Optional<Result> findByReport(Report report);

    @NotNull
    Optional<Result> findByReportAndAdministrator(Report report, Administrator administrator);

    @NotNull
    default Optional<Result> find(Report report, Administrator viewer) {
        return findByReportAndAdministrator(report, viewer);
    }

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Result> queryById(int id);
}
