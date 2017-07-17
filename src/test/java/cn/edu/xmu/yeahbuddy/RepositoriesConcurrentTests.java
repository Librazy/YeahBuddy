package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.repo.AdministratorRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RepositoriesConcurrentTests {

    @NonNls
    private static final String INNODB = "innodb";

    @NonNls
    private static Log log = LogFactory.getLog(RepositoriesConcurrentTests.class);

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private Environment environment;

    @PersistenceContext
    private EntityManager em;

    private int id;

    private boolean usingInnoDB = false;

    @Before
    public void setUp() throws Exception {
        usingInnoDB = INNODB.equalsIgnoreCase(environment.getProperty("spring.jpa.properties.hibernate.dialect.storage_engine"));
        id = administratorRepository.save(new Administrator("AAA", "xxx")).getId();
    }

    @After
    public void tearDown() {
        administratorRepository.deleteAll();
    }

    @Test
    @Repeat(10)
    public void administratorRepositoryConcurrentTest() throws Exception {
        log.info("==================Start================\\n\\n");
        AtomicInteger counter = new AtomicInteger(0);
        final CyclicBarrier gate = new CyclicBarrier(4);
        Runnable r = () -> transactionTemplate.execute((status) -> {

            if(usingInnoDB)em.createNativeQuery("set innodb_lock_wait_timeout = 1;").executeUpdate();
            try {
                gate.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            Optional<Administrator> administrator = administratorRepository.queryById(id);
            log.info(String.format("Sleeping: %d", Thread.currentThread().getId()));
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ignored) {
            }
            log.info(String.format("Waken: %d", Thread.currentThread().getId()));
            Assert.assertTrue(administrator.isPresent());
            administrator.get().setDisplayName("DP");
            administratorRepository.save(administrator.get());
            return null;
        });

        Thread.UncaughtExceptionHandler handler = (t, e) -> {
            counter.incrementAndGet();
            e.printStackTrace();
        };

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        Thread t3 = new Thread(r);
        t1.setUncaughtExceptionHandler(handler);
        t2.setUncaughtExceptionHandler(handler);
        t3.setUncaughtExceptionHandler(handler);
        log.info("==================READY================\n\n");
        t1.start();
        t2.start();
        t3.start();
        Thread.sleep(100);
        gate.await();

        t1.join();
        t2.join();
        t3.join();
        Assert.assertEquals(2, counter.get());
        log.info("==================End================\n\n");
    }
}
