package outcomes;

import org.hyperskill.hstest.dev.stage.BaseStageTest;
import org.hyperskill.hstest.dev.testcase.CheckResult;
import org.hyperskill.hstest.dev.testcase.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SuccessDynamicInput extends BaseStageTest<String> {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello");
        String line = scanner.nextLine();
        if (line.equals("1")) {
            System.out.println(scanner.nextLine());
        } else {
            System.out.println(line);
        }
    }

    public SuccessDynamicInput() {
        super(SuccessDynamicInput.class);
    }

    @Override
    public List<TestCase<String>> generate() {
        return Arrays.asList(
            new TestCase<String>()
                .addInput(out -> out)
                .setAttach("Hello\nHello\n"),

            new TestCase<String>()
                .addInput(o -> "Hi")
                .setAttach("Hello\nHi\n"),

            new TestCase<String>()
                .addInput(o -> "Hihi1")
                .addInput(o -> "Hihi2")
                .setAttach("Hello\nHihi1\n"),

            new TestCase<String>()
                .addInput(o -> "")
                .addInput(o -> "Hihi3")
                .setAttach("Hello\n\n"),

            new TestCase<String>()
                .addInput(o -> "1\n")
                .addInput(o -> "Hey")
                .setAttach("Hello\nHey\n"),

            new TestCase<String>()
                .addInput(o -> "2")
                .addInput(o -> "Hey")
                .setAttach("Hello\n2\n"),

            new TestCase<String>()
                .addInput(o -> "Hi before\nHi after")
                .addInput(o -> "Hey")
                .setAttach("Hello\nHi before\n")
        );
    }

    @Override
    public CheckResult check(String reply, String attach) {
        return new CheckResult(reply.equals(attach));
    }
}
