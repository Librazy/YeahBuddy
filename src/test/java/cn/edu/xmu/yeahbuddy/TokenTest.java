package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Stage;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Token;
import cn.edu.xmu.yeahbuddy.model.StageDto;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Rollback
@TestExecutionListeners(listeners = {WithSecurityContextTestExecutionListener.class})
public class TokenTest extends ApplicationTestBase {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    @Transactional
    public void tokenReadTest() throws Exception {
        Token t = tokenService.loadAndValidateToken(token).getSecond();
        Assert.assertEquals(1, t.getTeams().size());
        Assert.assertTrue(t.getTeams().stream().findFirst().isPresent());
        Assert.assertTrue(t.getTeams().stream().findFirst().get().equals(team1));

        Collection<Token> tokens = tutorService.loadById(tutor.getId()).getTokens();
        Assert.assertEquals(1, tokens.size());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "some", userDetailsServiceBeanName = "administratorService")
    public void tokenCreateTest() throws Exception {
        Stage stage = stageService.createStage(201702, new StageDto()
                                                               .setTitle("2017 02")
                                                               .setStart(Timestamp.valueOf("2017-02-01 20:00:00"))
                                                               .setEnd(Timestamp.valueOf("2017-04-01 20:00:00")));
        Set<Team> teamSet = new HashSet<>();
        teamSet.add(team1);
        teamSet.add(team2);

        Token t = tokenService.createToken(tutor, stage, teamSet);
        Assert.assertTrue(tokenService.loadAndValidateToken(t.getTokenValue()).getFirst().equals(tutor));
        Assert.assertEquals(2, tokenService.loadAndValidateToken(t.getTokenValue()).getSecond().getTeams().size());
        Assert.assertEquals("2017 02", t.getStage().getTitle());

        tokenService.revokeToken(t);

        exception.expect(BadCredentialsException.class);
        tokenService.loadAndValidateToken(t.getTokenValue());
    }
}
