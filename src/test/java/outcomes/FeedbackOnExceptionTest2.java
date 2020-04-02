package outcomes;

import org.hyperskill.hstest.v7.stage.StageTest;
import org.hyperskill.hstest.v7.testcase.CheckResult;
import org.hyperskill.hstest.v7.testcase.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class FeedbackOnExceptionTest2Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World");
        throw new IOException();
    }
}

public class FeedbackOnExceptionTest2 extends StageTest {

    public FeedbackOnExceptionTest2() {
        super(FeedbackOnExceptionTest2Main.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Exception in test #1");
        exception.expectMessage("IOEX thrown\n" +
            "\n" +
            "java.io.IOException");
    }

    @Override
    public List<TestCase> generate() {
        return Arrays.asList(
            new TestCase()
                .feedbackOnException(
                    ArithmeticException.class,
                    "Do not divide by zero!")
                .feedbackOnException(
                    IOException.class,
                    "IOEX thrown"
                )
        );
    }

    @Override
    public CheckResult check(String reply, Object attach) {
        return CheckResult.correct();
    }
}
