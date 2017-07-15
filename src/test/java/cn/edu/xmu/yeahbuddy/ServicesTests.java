package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import cn.edu.xmu.yeahbuddy.model.AdministratorDtoImpl;
import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.YbPasswordEncodeService;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class ServicesTests extends AbstractTransactionalJUnit4SpringContextTests {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Autowired
    private YbPasswordEncodeService ybPasswordEncodeService;
    @Autowired
    private AdministratorService administratorService;

    @Test
    public void administratorServiceTest1() throws Exception {
        Administrator ultimate = new Administrator();
        ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
        SecurityContextHolder.getContext().setAuthentication(ultimate);
        Administrator admin1 = administratorService.registerNewAdministrator(new AdministratorDtoImpl().setUsername("AAA").setPassword("BBB").setDisplayName("AAA").setAuthorities(Collections.singleton(AdministratorPermission.ManageAdministrator.getAuthority())));
        SecurityContextHolder.getContext().setAuthentication(admin1);
        Administrator admin2 = administratorService.registerNewAdministrator(new AdministratorDtoImpl().setUsername("BBB").setPassword("CCC").setDisplayName("BBB").setAuthorities(new HashSet<>()));
        SecurityContextHolder.getContext().setAuthentication(ultimate);
        Assert.assertTrue(ybPasswordEncodeService.matches("BBB", admin1.getPassword()));
        Assert.assertTrue(ybPasswordEncodeService.matches("CCC", administratorService.loadUserByUsername("BBB").getPassword()));

        admin1 = administratorService.updateAdministrator(admin1.getId(), new AdministratorDtoImpl().setUsername("DDD"));

        Assert.assertNotEquals(admin1.getId(), admin2.getId());
        Assert.assertEquals("DDD", admin1.getUsername());

        exception.expect(IdentifierAlreadyExistsException.class);
        administratorService.registerNewAdministrator(new AdministratorDtoImpl().setUsername("BBB").setPassword("BBB").setDisplayName("BBB").setAuthorities(new HashSet<>()));
    }

    @Test
    public void administratorServiceTest2() throws Exception {
        Administrator ultimate = new Administrator();
        ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
        SecurityContextHolder.getContext().setAuthentication(ultimate);
        Administrator admin1 = administratorService.registerNewAdministrator(new AdministratorDtoImpl().setUsername("DDD").setPassword("BBB").setDisplayName("DDD").setAuthorities(new HashSet<>()));
        Assert.assertTrue(ybPasswordEncodeService.matches("BBB", admin1.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(null);
        exception.expect(AuthenticationCredentialsNotFoundException.class);
        administratorService.registerNewAdministrator(new AdministratorDtoImpl().setUsername("EEE").setPassword("BBB").setDisplayName("DDD").setAuthorities(new HashSet<>()));
    }

    @Test
    public void administratorServiceTest3() throws Exception {
        Administrator ultimate = new Administrator();
        ultimate.setAuthorities(Arrays.asList(AdministratorPermission.values()));
        SecurityContextHolder.getContext().setAuthentication(ultimate);
        Administrator admin1 = administratorService.registerNewAdministrator(new AdministratorDtoImpl().setUsername("DDD").setPassword("BBB").setDisplayName("DDD").setAuthorities(Collections.singleton(AdministratorPermission.ManageAdministrator.getAuthority())));
        Assert.assertTrue(ybPasswordEncodeService.matches("BBB", admin1.getPassword()));

        administratorService.updateAdministrator(admin1.getId(), new AdministratorDtoImpl().setDisplayName("FFF"));
        Assert.assertEquals("FFF", admin1.getDisplayName());

        SecurityContextHolder.getContext().setAuthentication(admin1);
        exception.expect(AccessDeniedException.class);
        administratorService.registerNewAdministrator(new AdministratorDtoImpl().setUsername("EEE").setPassword("BBB").setDisplayName("EEE").setAuthorities(Arrays.asList(AdministratorPermission.CreateTask.getAuthority(), AdministratorPermission.ManageAdministrator.getAuthority())));
    }

    private class AdministratorDtoTester extends AdministratorDtoImpl {

        private static final long serialVersionUID = -1L;

        @Override
        public String getDisplayName() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return super.getDisplayName();
        }
    }
}
