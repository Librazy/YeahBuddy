package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByReport(Report report);

    List<Review> findByTutor(Tutor tutor);

    @NotNull
    Optional<Review> findByReportAndTutor(Report report, Tutor tutor);

    @NotNull
    default Optional<Review> find(Report report, Tutor tutor) {
        return findByReportAndTutor(report, tutor);
    }

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Review> queryById(int id);
}
