package outcomes.kotlin_input;

import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hyperskill.hstest.dynamic.input.KotlinInput.readLine;

class TestInputHandlerKotlinStyle5Main {
    public static void main(String[] args) throws IOException {
        if (!"qwerty 123".equals(readLine())) {
            throw new RuntimeException();
        }
    }
}

public class TestInputHandlerKotlinStyle5 extends StageTest {
    public TestInputHandlerKotlinStyle5() {
        super(TestInputHandlerKotlinStyle5Main.class);
    }

    @Override
    public List<TestCase> generate() {
        return Arrays.asList(
            new TestCase<String>()
                .setInput("qwerty 123")
        );
    }

    @Override
    public CheckResult check(String reply, Object attach) {
        return CheckResult.correct();
    }
}
