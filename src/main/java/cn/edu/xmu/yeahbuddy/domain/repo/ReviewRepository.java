package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Stage;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByTeamAndStage(Team team, Stage stage);

    @NotNull
    Optional<Review> findByTeamAndStageAndViewer(Team team, Stage stage, Tutor viewer);

    @NotNull
    default Optional<Review> find(Team team, Stage stage, Tutor viewer) {
        return findByTeamAndStageAndViewer(team, stage, viewer);
    }

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Review> queryById(int id);
}
