package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByTeamIdAndStage(int teamId, int stage);

    Optional<Review> findByTeamIdAndStageAndViewerAndViewerIsAdmin(int teamId, int stage, int viewer, boolean viewerIsAdmin);

    default Optional<Review> find(int teamId, int stage, int viewer, boolean viewerIsAdmin){
        return findByTeamIdAndStageAndViewerAndViewerIsAdmin(teamId, stage, viewer, viewerIsAdmin);
    }
}
