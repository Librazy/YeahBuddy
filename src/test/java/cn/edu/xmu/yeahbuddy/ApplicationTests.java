package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import cn.edu.xmu.yeahbuddy.domain.repo.ReviewRepository;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.YbPasswordEncodeService;
import cn.edu.xmu.yeahbuddy.utils.PasswordUtils;
import cn.edu.xmu.yeahbuddy.utils.UsernameAlreadyExistsException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class ApplicationTests extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private YbPasswordEncodeService ybPasswordEncodeService;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void administratorRepositoryTest() throws Exception {

        administratorRepository.deleteAll();

        // 创建2条记录
        administratorRepository.save(new Administrator("AAA", "xxx"));
        administratorRepository.save(new Administrator("BBB", "xxx"));

        // 测试findAll, 查询所有记录
        Assert.assertEquals(2, administratorRepository.findAll().size());

        // 测试findByName, 查询姓名为AAA的Administrator
        Assert.assertEquals("xxx", administratorRepository.findByName("AAA").getPassword());

        // 测试删除姓名为BBB的Administrator
        administratorRepository.delete(administratorRepository.findByName("BBB"));

        // 测试findAll, 查询所有记录, 验证上面的删除是否成功
        Assert.assertEquals(1, administratorRepository.findAll().size());

        // 测试删除姓名为AAA的Administrator
        administratorRepository.delete(administratorRepository.findByName("AAA"));
    }

    @Test
    public void administratorServiceTest() throws Exception{

        administratorRepository.deleteAll();

        Administrator ultimate = new Administrator();
        ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
        SecurityContextHolder.getContext().setAuthentication(ultimate);
        Administrator admin1 = administratorService.registerNewAdministrator(new AdministratorDto().setName("AAA").setPassword("BBB").setAuthorities(new HashSet<>()), ultimate);
        Administrator admin2 = administratorService.registerNewAdministrator(new AdministratorDto().setName("BBB").setPassword("CCC").setAuthorities(new HashSet<>()), admin1);

        Assert.assertTrue(ybPasswordEncodeService.matches("BBB", admin1.getPassword()));
        Assert.assertTrue(ybPasswordEncodeService.matches("CCC", administratorService.loadUserByUsername("BBB").getPassword()));

        Assert.assertNotEquals(admin1.getId(),admin2.getId());

        exception.expect(UsernameAlreadyExistsException.class);
        administratorService.registerNewAdministrator(new AdministratorDto().setName("AAA").setPassword("BBB").setAuthorities(new HashSet<>()), ultimate);
    }

    @Test
    public void administratorService2Test() throws Exception{

        administratorRepository.deleteAll();

        Administrator ultimate = new Administrator();
        ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
        SecurityContextHolder.getContext().setAuthentication(ultimate);
        Administrator admin1 = administratorService.registerNewAdministrator(new AdministratorDto().setName("DDD").setPassword("BBB").setAuthorities(new HashSet<>()), ultimate);
        Assert.assertTrue(ybPasswordEncodeService.matches("BBB", admin1.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(null);
        exception.expect(AuthenticationCredentialsNotFoundException.class);
        administratorService.registerNewAdministrator(new AdministratorDto().setName("EEE").setPassword("BBB").setAuthorities(new HashSet<>()), ultimate);
    }

    @Test
    public void reviewRepositoryTest() throws Exception {

        // 创建9条记录
        Review review1 = new Review(1 ,201701 ,1 ,false);
        review1.setText("test text");
        reviewRepository.save(review1);
        reviewRepository.save(new Review(1 ,201701 ,2 , false));
        reviewRepository.save(new Review(1 ,201701 ,3 , false));
        reviewRepository.save(new Review(2 ,201701 ,1 , false));
        reviewRepository.save(new Review(2 ,201701 ,2 , false));
        reviewRepository.save(new Review(2 ,201701 ,3 , false));
        reviewRepository.save(new Review(2 ,201701 ,1 , true));
        reviewRepository.save(new Review(2 ,201702 ,2 , false));
        reviewRepository.save(new Review(2 ,201702 ,3 , false));

        // 测试findAll, 查询所有记录
        Assert.assertEquals(9, reviewRepository.findAll().size());

        Optional<Review> review2 = reviewRepository.findById(new ReviewKey(1 ,201701 ,1 ,false));

        Assert.assertTrue(review2.isPresent());

        Assert.assertEquals("test text", review2.get().getText());

        Assert.assertEquals(2, reviewRepository.findAll(Example.of(new Review(0 ,201702 ,0 , false), ExampleMatcher.matching().withIgnoreNullValues().withIgnorePaths("teamId", "viewer", "viewerIsAdmin", "rank", "submitted"))).size());
    }

    @Test
    public void passwordUtilsTest() throws Exception {
        byte[] salt = PasswordUtils.generateSalt();

        Assert.assertEquals(16, salt.length);

        byte[] hash = PasswordUtils.hash("password".toCharArray(), salt);

        Assert.assertTrue(PasswordUtils.isExpectedPassword("password".toCharArray(), salt, hash));
    }
}
