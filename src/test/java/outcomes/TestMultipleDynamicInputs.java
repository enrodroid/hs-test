package outcomes;

import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class TestMultipleDynamicInputsMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 5; i++) {
            System.out.println(scanner.nextInt());
        }
    }
}

public class TestMultipleDynamicInputs extends StageTest<String> {

    public TestMultipleDynamicInputs() {
        super(TestMultipleDynamicInputsMain.class);
    }

    @Override
    public List<TestCase<String>> generate() {
        return Arrays.asList(
            new TestCase<String>()
                .addInput(o -> "1")
                .addInput(o -> "2")
                .addInput(o -> "3")
                .addInput(o -> "4")
                .addInput(o -> "5")
                .setAttach("1\n2\n3\n4\n5\n"),

            new TestCase<String>()
                .addInput(o -> "1")
                .addInput(o -> "2")
                .addInput(o -> "3")
                .addInput(2, o -> "4")
                .setAttach("1\n2\n3\n4\n4\n"),

            new TestCase<String>()
                .addInput(o -> "1")
                .addInput(o -> "2")
                .addInput(2, o -> "3")
                .addInput(o -> "4")
                .setAttach("1\n2\n3\n3\n4\n"),

            new TestCase<String>()
                .addInput(o -> "1")
                .addInput(3, o -> "2")
                .addInput(o -> "3")
                .addInput(o -> "4")
                .addInput(o -> "5")
                .setAttach("1\n2\n2\n2\n3\n"),

            new TestCase<String>()
                .addInput(o -> "1")
                .addInput(10, o -> "2")
                .addInput(o -> "3")
                .addInput(o -> "4")
                .addInput(o -> "5")
                .setAttach("1\n2\n2\n2\n2\n"),

            new TestCase<String>()
                .addInput(2, o -> "1\n2")
                .addInput(o -> "5")
                .addInput(o -> "6")
                .addInput(o -> "7")
                .addInput(o -> "8")
                .setAttach("1\n2\n1\n2\n5\n"),

            new TestCase<String>()
                .addInput(-1, o -> "1\n2")
                .addInput(o -> "5")
                .addInput(o -> "6")
                .addInput(o -> "7")
                .addInput(o -> "8")
                .setAttach("1\n2\n1\n2\n1\n"),

            new TestCase<String>()
                .addInfInput(o -> "1\n2")
                .addInput(o -> "5")
                .addInput(o -> "6")
                .addInput(o -> "7")
                .addInput(o -> "8")
                .setAttach("1\n2\n1\n2\n1\n"),

            new TestCase<String>()
                .addInfInput(o -> "1")
                .addInput(o -> "2")
                .addInput(o -> "3")
                .addInput(o -> "4")
                .addInput(o -> "5")
                .setAttach("1\n1\n1\n1\n1\n"),

            new TestCase<String>()
                .addInput(o -> "1")
                .addInput(o -> "2")
                .addInfInput(o -> "3")
                .addInput(o -> "4")
                .addInput(o -> "5")
                .setAttach("1\n2\n3\n3\n3\n")
        );
    }

    @Override
    public CheckResult check(String reply, String attach) {
        return new CheckResult(reply.equals(attach), "");
    }
}
