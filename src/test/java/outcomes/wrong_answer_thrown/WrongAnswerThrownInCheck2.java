package outcomes.wrong_answer_thrown;

import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

class WrongAnswerThrownInCheck2Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}

public class WrongAnswerThrownInCheck2 extends StageTest<Boolean> {

    public WrongAnswerThrownInCheck2() {
        super(WrongAnswerThrownInCheck2Main.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Wrong answer in test #1\n\n" +
            "Wrong answer from check attach true");
        exception.expectMessage(not(containsString("Fatal error")));
    }

    @Override
    public List<TestCase<Boolean>> generate() {
        return Arrays.asList(
            new TestCase<Boolean>().setAttach(true),
            new TestCase<Boolean>().setAttach(false)
        );
    }

    @Override
    public CheckResult check(String reply, Boolean attach) {
        if (attach) {
            throw new WrongAnswer("Wrong answer from check attach true");
        }
        return new CheckResult(true, "");
    }
}
