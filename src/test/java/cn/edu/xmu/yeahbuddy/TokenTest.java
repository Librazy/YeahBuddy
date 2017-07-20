package cn.edu.xmu.yeahbuddy;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Stage;
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
        Assert.assertEquals(1, t.getReviews().size());
        Assert.assertTrue(t.getReviews().stream().findFirst().isPresent());
        Assert.assertTrue(t.getReviews().stream().findFirst().get().getTeam().equals(team1));
        Assert.assertEquals("2017 01", t.getReviews().stream().findFirst().map(Review::getStage).map(Stage::getTitle).orElseThrow(AssertionError::new));

        Collection<Token> tokens = tutorService.loadById(tutor1.getId()).getTokens();
        Assert.assertEquals(1, tokens.size());
    }

    @Test
    @Transactional
    @WithUserDetails(value = "some", userDetailsServiceBeanName = "administratorService")
    public void tokenCreateTest() throws Exception {
        Stage stage = stageService.createStage(201702, new StageDto()
                                                               .setTitle("2017 02")
                                                               .setStart(Timestamp.valueOf("2017-02-01 20:00:00"))
                                                               .setEnd(Timestamp.valueOf("2017-09-01 20:00:00")));
        Set<Review> reviewSet = new HashSet<>();

        Report report = reportService.createReport(team1, stage, "Report");

        Review review2 = reviewService.createReview(report, tutor1);

        reviewSet.add(review);
        reviewSet.add(review2);

        Token t = tokenService.createToken(tutor1, reviewSet, stage.getEnd());
        Assert.assertTrue(tokenService.loadAndValidateToken(t.getTokenValue()).getFirst().equals(tutor1));
        Assert.assertEquals(2, tokenService.loadAndValidateToken(t.getTokenValue()).getSecond().getReviews().size());

        tokenService.revokeToken(t);

        exception.expect(BadCredentialsException.class);
        tokenService.loadAndValidateToken(t.getTokenValue());
    }
}
