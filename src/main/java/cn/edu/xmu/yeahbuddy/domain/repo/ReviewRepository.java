package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Review;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByTeamIdAndStageId(int teamId, int stageId);

    @NotNull
    Optional<Review> findByTeamIdAndStageIdAndViewerAndViewerIsAdmin(int teamId, int stageId, int viewer, boolean viewerIsAdmin);

    @NotNull
    default Optional<Review> find(int teamId, int stageId, int viewer, boolean viewerIsAdmin) {
        return findByTeamIdAndStageIdAndViewerAndViewerIsAdmin(teamId, stageId, viewer, viewerIsAdmin);
    }

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Review> queryById(int id);
}
