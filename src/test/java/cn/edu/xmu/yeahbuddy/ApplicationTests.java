package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void administratorRepositoryTest() throws Exception {

        // 创建2条记录
        administratorRepository.save(new Administrator("AAA", "xxx", "yyy"));
        administratorRepository.save(new Administrator("BBB", "xxx", "xxx"));

        // 测试findAll, 查询所有记录
        Assert.assertEquals(2, administratorRepository.findAll().size());

        // 测试findByName, 查询姓名为AAA的Administrator
        Assert.assertEquals("yyy", administratorRepository.findByName("AAA").getSalt());

        // 测试删除姓名为AAA的Administrator
        administratorRepository.delete(administratorRepository.findByName("BBB"));

        // 测试findAll, 查询所有记录, 验证上面的删除是否成功
        Assert.assertEquals(1, administratorRepository.findAll().size());

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

        Assert.assertEquals("test text", reviewRepository.findOne(new ReviewKey(1 ,201701 ,1 ,false)).getText());

        Assert.assertEquals(2, reviewRepository.findAll(Example.of(new Review(0 ,201702 ,0 , false), ExampleMatcher.matching().withIgnoreNullValues().withIgnorePaths("teamId", "viewer", "viewerIsAdmin", "rank", "submitted"))).size());
    }


}
