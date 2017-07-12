package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByTeamIdAndStageId(int teamId, int stageId);

    Optional<Review> findByTeamIdAndStageIdAndViewerAndViewerIsAdmin(int teamId, int stageId, int viewer, boolean viewerIsAdmin);

    default Optional<Review> find(int teamId, int stageId, int viewer, boolean viewerIsAdmin){
        return findByTeamIdAndStageIdAndViewerAndViewerIsAdmin(teamId, stageId, viewer, viewerIsAdmin);
    }
}
