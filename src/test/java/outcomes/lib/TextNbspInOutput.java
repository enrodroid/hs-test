package outcomes.lib;

import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.util.Arrays;
import java.util.List;

class TextNbspInOutputMain {
    public static void main(String[] args) {
        System.out.print("1\u00a02 3");
    }
}

public class TextNbspInOutput extends StageTest {

    public TextNbspInOutput() {
        super(TextNbspInOutputMain.class);
    }

    @Override
    public List<TestCase> generate() {
        return Arrays.asList(
            new TestCase()
        );
    }

    @Override
    public CheckResult check(String reply, Object attach) {
        return new CheckResult(reply.equals("1\u00202\u00203"), "");
    }
}
