package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@TestExecutionListeners(listeners = {WithSecurityContextTestExecutionListener.class})
public class AdministratorAuthenticationTests extends AbstractTransactionalJUnit4SpringContextTests {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Autowired
    private AdministratorService administratorService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeTransaction
    public void setUp() {
        new TransactionTemplate(transactionManager).execute(status -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
            SecurityContextHolder.getContext().setAuthentication(ultimate);
            administratorService.registerNewAdministrator(
                    new AdministratorDto()
                            .setUsername("some")
                            .setPassword("one")
                            .setDisplayName("some")
                            .setAuthorities(
                                    Stream.of(AdministratorPermission.values())
                                          .map(AdministratorPermission::name)
                                          .collect(Collectors.toSet())));
            administratorService.registerNewAdministrator(
                    new AdministratorDto()
                            .setUsername("other")
                            .setPassword("one")
                            .setDisplayName("other")
                            .setAuthorities(new ArrayList<>(0)));
            SecurityContextHolder.getContext().setAuthentication(null);
            return null;
        });
    }

    @AfterTransaction
    public void tearDown() {
        new TransactionTemplate(transactionManager).execute(status -> {
            Administrator ultimate = new Administrator();
            ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
            SecurityContextHolder.getContext().setAuthentication(ultimate);
            administratorService.deleteAdministrator(administratorService.loadUserByUsername("some").getId());
            administratorService.deleteAdministrator(administratorService.loadUserByUsername("other").getId());
            SecurityContextHolder.getContext().setAuthentication(null);
            return null;
        });
    }

    @Test
    @WithUserDetails(value = "some", userDetailsServiceBeanName = "administratorService")
    public void registerNewAdministratorTest() {
        administratorService.registerNewAdministrator(
                new AdministratorDto()
                        .setUsername("admin2")
                        .setPassword("admin2")
                        .setDisplayName("admin2")
                        .setAuthorities(
                                Stream.of(AdministratorPermission.ViewReport)
                                      .map(AdministratorPermission::name)
                                      .collect(Collectors.toSet())));
    }

    @Test
    @WithUserDetails(value = "other", userDetailsServiceBeanName = "administratorService")
    public void registerNewAdministratorWithoutAuthTest() {
        exception.expect(AccessDeniedException.class);
        administratorService.registerNewAdministrator(
                new AdministratorDto()
                        .setUsername("admin3")
                        .setPassword("admin3")
                        .setAuthorities(
                                Stream.of(AdministratorPermission.ViewReport)
                                      .map(AdministratorPermission::name)
                                      .collect(Collectors.toSet())));
    }

}