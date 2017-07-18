package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Stage;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.ReportRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.StageRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamRepository;
import cn.edu.xmu.yeahbuddy.utils.PasswordUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback
public class RepositoriesAndUtilsTests extends AbstractTransactionalJUnit4SpringContextTests {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    public void administratorRepositoryTest() throws Exception {

        // 创建2条记录
        administratorRepository.save(new Administrator("AAA", "xxx"));
        administratorRepository.save(new Administrator("BBB", "xxx"));

        // 测试findAll, 查询所有记录
        Assert.assertEquals(2, administratorRepository.findAll().size());

        // 测试findByName, 查询姓名为AAA的Administrator
        Assert.assertTrue(administratorRepository.findByUsername("AAA").isPresent());
        Assert.assertEquals("xxx", administratorRepository.findByUsername("AAA").get().getPassword());

        // 测试删除姓名为BBB的Administrator
        Assert.assertTrue(administratorRepository.findByUsername("BBB").isPresent());
        administratorRepository.delete(administratorRepository.findByUsername("BBB").get());

        // 测试findAll, 查询所有记录, 验证上面的删除是否成功
        Assert.assertEquals(1, administratorRepository.findAll().size());

        // 测试删除姓名为AAA的Administrator
        administratorRepository.delete(administratorRepository.findByUsername("AAA").get());
    }

    @Test
    public void passwordUtilsTest() throws Exception {
        byte[] salt = PasswordUtils.generateSalt();

        Assert.assertEquals(15, salt.length);

        byte[] hash = PasswordUtils.hash("password".toCharArray(), salt);

        Assert.assertTrue(PasswordUtils.isExpectedPassword("password".toCharArray(), salt, hash));
    }

    @Test
    public void reportRepositoryTest() throws Exception {
        Team team1 = new Team("Team1", "Team2");
        Team team2 = new Team("Team2", "Team2");
        Stage stage1 = new Stage(201701, Timestamp.valueOf("2017-01-01 10:00:00"), Timestamp.valueOf("2017-01-31 23:00:00"));
        Stage stage2 = new Stage(201702, Timestamp.valueOf("2017-02-01 10:00:00"), Timestamp.valueOf("2017-02-28 23:00:00"));

        team1 = teamRepository.save(team1);
        teamRepository.save(team2);

        stageRepository.save(stage1);
        stage2 = stageRepository.save(stage2);

        Assert.assertNotEquals(team1.getId(), Integer.MIN_VALUE);

        Report report = new Report(team1, stage2);
        report.setTitle("Title");

        int id = reportRepository.save(report).getId();

        Optional<Report> read = reportRepository.queryById(id);

        Assert.assertTrue(read.isPresent());
        Assert.assertEquals(team1.getId(), read.get().getTeam().getId());
        Assert.assertEquals(team1.getId(), read.get().getTeamId());
    }
}
