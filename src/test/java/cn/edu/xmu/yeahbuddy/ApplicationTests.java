package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@DataJpaTest
public class ApplicationTests {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Test
    public void administratorRepositoryTest() throws Exception {

        // 创建10条记录
        administratorRepository.save(new Administrator("AAA", "xxx", "yyy"));
        administratorRepository.save(new Administrator("BBB", "xxx", "xxx"));

        // 测试findAll, 查询所有记录
        Assert.assertEquals(2, administratorRepository.findAll().size());

        // 测试findByName, 查询姓名为FFF的User
        Assert.assertEquals("yyy", administratorRepository.findByName("AAA").getSalt());

        // 测试删除姓名为AAA的User
        administratorRepository.delete(administratorRepository.findByName("BBB"));

        // 测试findAll, 查询所有记录, 验证上面的删除是否成功
        Assert.assertEquals(1, administratorRepository.findAll().size());

    }


}
