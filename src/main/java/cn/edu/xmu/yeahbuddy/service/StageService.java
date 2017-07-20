package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Stage;
import cn.edu.xmu.yeahbuddy.domain.repo.StageRepository;
import cn.edu.xmu.yeahbuddy.model.StageDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import cn.edu.xmu.yeahbuddy.utils.IdentifierNotExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class StageService {

    @NonNls
    private static Log log = LogFactory.getLog(StageService.class);

    private StageRepository stageRepository;

    /**
     * 构造函数
     * Spring Boot自动装配
     *
     * @param stageRepository Autowired
     */
    public StageService(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    /**
     * 查找阶段
     *
     * @param id 阶段主键
     * @return 阶段
     */
    @Transactional
    public Optional<Stage> findById(int id) {
        log.debug("Finding Stage with key " + id);
        return stageRepository.findById(id);
    }

    /**
     * 查找所有阶段
     *
     * @return 阶段
     */
    @Transactional
    public List<Stage> findAllStages(){
        log.debug("Finding all Stages");
        return stageRepository.findAll();
    }

    /**
     * 查找所有未截止阶段
     *
     * @return 阶段
     */
    @Transactional
    public List<Stage> findByEndAfter(Timestamp timestamp){ return stageRepository.findByEndAfter(timestamp); }

    /**
     * 查找所有已截止阶段
     *
     * @return 阶段
     */
    @Transactional
    public List<Stage> findByEndBefore(Timestamp timestamp){ return stageRepository.findByEndBefore(timestamp); }

    /**
     * 按ID查找阶段
     *
     * @param id 查找的阶段id
     * @return 团队
     * @throws IdentifierNotExistsException 找不到阶段
     */
    @Transactional(readOnly = true)
    public Stage loadById(int id) throws IdentifierNotExistsException {
        log.debug("Trying to load Stage id " + id);
        Optional<Stage> stage = stageRepository.findById(id);
        if (!stage.isPresent()) {
            log.info("Failed to load Stage id" + id + ": not found");
            throw new IdentifierNotExistsException("stage.id.not_found", id);
        }
        log.debug("Loaded Stage id " + id);
        return stage.get();
    }

    /**
     * 新建阶段
     *
     * @param stageId  阶段ID
     * @param stageDto 阶段DTO
     * @return 新建的阶段
     */
    public Stage createStage(int stageId, StageDto stageDto) {
        log.debug(String.format("Trying to create Stage: %d", stageId));
        if (stageRepository.findById(stageId).isPresent()) {
            log.info(String.format("Fail to create Stage with id: %d : id already exist", stageId));
            throw new IdentifierAlreadyExistsException("stage.id.exist", null);
        }

        Stage stage = new Stage(stageId, stageDto.getStart(), stageDto.getEnd());
        stage.setDescription(stageDto.getDescription());
        stage.setTitle(stageDto.getTitle());

        Stage result = stageRepository.save(stage);
        log.debug(String.format("Created new Stage with id: %d", stageId));
        return result;
    }
}
