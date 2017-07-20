package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Result;
import cn.edu.xmu.yeahbuddy.domain.repo.ResultRepository;
import cn.edu.xmu.yeahbuddy.model.ResultDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import cn.edu.xmu.yeahbuddy.utils.IdentifierNotExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class ResultService {

    @NonNls
    private static Log log = LogFactory.getLog(ResultService.class);

    private final ResultRepository resultRepository;

    /**
     * 构造函数
     * Spring Boot自动装配
     *
     * @param resultRepository Autowired
     */
    @Autowired
    public ResultService(ResultRepository resultRepository){ this.resultRepository = resultRepository; }

    /**
     * 查找综合评审报告
     *
     * @param id 评审报告报告主键
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Result> findById(int id) {
        log.debug("Finding Result with key " + id);
        return resultRepository.findById(id);
    }

    /**
     * 查找评审报告
     *
     * @param report 报告
     * @param viewer 审核者
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Result> find(Report report, Administrator viewer) {
        log.debug("Finding Result");
        return resultRepository.find(report, viewer);
    }

    /**
     * 查找某个团队项目报告的综合评审报告
     *
     * @param report 项目报告
     * @return 综合评审报告
     */
    @Transactional
    public Optional<Result> findByReport(Report report) {
        return resultRepository.findByReport(report);
    }

    /**
     * 新建综合评审报告
     *
     * @param report 目标报告
     * @param viewer 审核管理员
     * @return 新注册的综合评审报告
     */
    @Transactional
    public Result createReview(Report report, Administrator viewer) throws IdentifierAlreadyExistsException {
        log.debug(String.format("Trying to create Result: %s %s", report, viewer));
        if (resultRepository.find(report, viewer).isPresent()) {
            log.info(String.format("Fail to create Result with id: %s %s: id already exist", report, viewer));
            throw new IdentifierAlreadyExistsException("review.id.exist", null);
        }

        Result result = new Result(report, viewer);
        result = resultRepository.save(result);
        log.debug(String.format("Created new Result with id: %s %s", report, viewer));
        return result;
    }

    /**
     * 删除团队综合评审报告
     *
     * @param id 综合评审报告主键
     */
    @Transactional
    public void deleteResult(int id) {
        log.debug("Delete Report with id" + id);
        resultRepository.deleteById(id);
    }

    @Transactional
    public Result updateResult(int id, ResultDto dto) {
        log.debug("Trying to update Result with id" + id);
        Optional<Result> r = resultRepository.queryById(id);

        if (!r.isPresent()) {
            log.info("Failed to load Result " + id + ": not found");
            throw new IdentifierNotExistsException("result.id.not_found", id);
        }
        Result result = r.get();
        if (dto.getSubmitted() != null) {
            log.trace("Updated submitted for Result with id " + id + ":" + result.isSubmitted() +
                    " -> " + dto.getSubmitted());
            result.setSubmitted(dto.getSubmitted());
        }

        if (dto.getContent() != null) {
            log.trace("Updated content for Result with id " + id);
            result.setContent(dto.getContent());
        }

        if (dto.getRank() != null) {
            log.trace("Updated rank for Result with id " + id + ":" + result.getRank() +
                    " -> " + dto.getRank());
            result.setRank(dto.getRank());
        }

        return resultRepository.save(result);
    }
}
