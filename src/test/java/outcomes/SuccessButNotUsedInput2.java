package outcomes;

import org.hyperskill.hstest.v7.stage.StageTest;
import org.hyperskill.hstest.v7.testcase.CheckResult;
import org.hyperskill.hstest.v7.testcase.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


class SuccessButNotUsedInput2Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("HELLO");
        System.out.println(scanner.nextLine());
        System.out.println(scanner.nextLine());
    }
}

public class SuccessButNotUsedInput2 extends StageTest<String> {

    public SuccessButNotUsedInput2() {
        super(SuccessButNotUsedInput2Main.class);
    }

    @Override
    public List<TestCase<String>> generate() {
        return Arrays.asList(
            new TestCase<String>()
                .addInput(out -> {
                    if (out.equals("HELLO\n")) {
                        return CheckResult.correct();
                    }
                    return "";
                })
                .setAttach("NO")
        );
    }

    @Override
    public CheckResult check(String reply, String attach) {
        return new CheckResult(reply.equals(attach), "");
    }
}
