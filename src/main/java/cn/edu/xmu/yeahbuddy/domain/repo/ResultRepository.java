package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Integer>  {
    Optional<Result> findByReport(Report report);

    @NotNull
    Optional<Result> findByReportAndViewer(Report report, Administrator viewer);

    @NotNull
    default Optional<Result> find(Report report, Administrator viewer) { return findByReportAndViewer(report, viewer); }

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Result> queryById(int id);
}
