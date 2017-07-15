package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.ReviewRepository;
import cn.edu.xmu.yeahbuddy.utils.PasswordUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback
public class RepositoriesAndUtilsTests extends AbstractTransactionalJUnit4SpringContextTests {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void administratorRepositoryTest() throws Exception {

        administratorRepository.deleteAll();

        // 创建2条记录
        administratorRepository.save(new Administrator("AAA", "xxx"));
        administratorRepository.save(new Administrator("BBB", "xxx"));

        // 测试findAll, 查询所有记录
        Assert.assertEquals(2, administratorRepository.findAll().size());

        // 测试findByName, 查询姓名为AAA的Administrator
        Assert.assertEquals("xxx", administratorRepository.findByUsername("AAA").getPassword());

        // 测试删除姓名为BBB的Administrator
        administratorRepository.delete(administratorRepository.findByUsername("BBB"));

        // 测试findAll, 查询所有记录, 验证上面的删除是否成功
        Assert.assertEquals(1, administratorRepository.findAll().size());

        // 测试删除姓名为AAA的Administrator
        administratorRepository.delete(administratorRepository.findByUsername("AAA"));
    }

    @Test
    public void reviewRepositoryTest() throws Exception {

        // 创建9条记录
        Review review1 = new Review(1, 201701, 1, false);
        Map<Integer, String> content = new HashMap<>();
        content.put(0, "test text");
        review1.setContent(content);
        reviewRepository.save(review1);
        reviewRepository.save(new Review(1, 201701, 2, false));
        reviewRepository.save(new Review(1, 201701, 3, false));
        reviewRepository.save(new Review(2, 201701, 1, false));
        reviewRepository.save(new Review(2, 201701, 2, false));
        reviewRepository.save(new Review(2, 201701, 3, false));
        reviewRepository.save(new Review(2, 201701, 1, true));
        reviewRepository.save(new Review(2, 201702, 2, false));
        reviewRepository.save(new Review(2, 201702, 3, false));

        // 测试findAll, 查询所有记录
        Assert.assertEquals(9, reviewRepository.findAll().size());

        Optional<Review> review2 = reviewRepository.find(1, 201701, 1, false);

        Assert.assertTrue(review2.isPresent());

        Assert.assertEquals("test text", review2.get().getContent().get(0));
    }

    @Test
    public void passwordUtilsTest() throws Exception {
        byte[] salt = PasswordUtils.generateSalt();

        Assert.assertEquals(15, salt.length);

        byte[] hash = PasswordUtils.hash("password".toCharArray(), salt);

        Assert.assertTrue(PasswordUtils.isExpectedPassword("password".toCharArray(), salt, hash));
    }
}
