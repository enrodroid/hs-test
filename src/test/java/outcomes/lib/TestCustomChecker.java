package outcomes.lib;

import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.util.Arrays;
import java.util.List;

class TestCustomCheckerMain {
    public static void main(String[] args) {
        System.out.println(args.length);
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}

public class TestCustomChecker extends StageTest<String> {

    public TestCustomChecker() {
        super(TestCustomCheckerMain.class);
    }

    @Override
    public List<TestCase<String>> generate() {
        return Arrays.asList(
            new TestCase<String>()
                .addArguments("-in", "123", "-out", "234")
                .setAttach("4\n-in\n123\n-out\n234\n")
                .setCheckFunc(this::customCheck),

            new TestCase<String>()
                .addArguments("-in", "435")
                .addArguments("-out", "567", "789")
                .setAttach("5\n-in\n435\n-out\n567\n789\n")
                .setCheckFunc(this::customCheck)
        );
    }

    private CheckResult customCheck(String reply, String attach) {
        return new CheckResult(reply.equals(attach), "");
    }
}
