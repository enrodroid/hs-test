package outcomes;

import org.hyperskill.hstest.v7.stage.StageTest;
import org.hyperskill.hstest.v7.testcase.CheckResult;
import org.hyperskill.hstest.v7.testcase.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

class WrongAnswerInTest1Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}

public class WrongAnswerInTest1 extends StageTest {

    public WrongAnswerInTest1() {
        super(WrongAnswerInTest1Main.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Wrong answer in test #1");
        exception.expectMessage(not(containsString("Fatal error")));
    }

    @Override
    public List<TestCase> generate() {
        return Arrays.asList(
            new TestCase()
        );
    }

    @Override
    public CheckResult check(String reply, Object attach) {
        return CheckResult.wrong("");
    }
}
