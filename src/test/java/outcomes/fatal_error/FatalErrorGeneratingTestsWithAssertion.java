package outcomes.fatal_error;

import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

class FatalErrorGeneratingTestsWithAssertionMain {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}

public class FatalErrorGeneratingTestsWithAssertion extends StageTest {

    public FatalErrorGeneratingTestsWithAssertion() {
        super(FatalErrorGeneratingTestsWithAssertionMain.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Fatal error during testing, please send the report to support@hyperskill.org");
    }

    @Override
    public List<TestCase> generate() {
        assert false;
        return Arrays.asList(
                new TestCase()
        );
    }

    @Override
    public CheckResult check(String reply, Object attach) {
        return CheckResult.correct();
    }
}
