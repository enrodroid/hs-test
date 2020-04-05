package outcomes;

import org.hyperskill.hstest.v7.stage.StageTest;
import org.hyperskill.hstest.v7.testcase.CheckResult;
import org.hyperskill.hstest.v7.testcase.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

class TestDynamicOutputWithNumbersMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Print x and y: ");
        if (123 != scanner.nextInt()
            || 456 != scanner.nextInt()
            || 678 != scanner.nextInt()) {
            throw new RuntimeException();
        }
        System.out.println("Another num:");
        if (248 != scanner.nextInt()) {
            throw new RuntimeException();
        }
    }
}


public class TestDynamicOutputWithNumbers extends StageTest<String> {

    public TestDynamicOutputWithNumbers() {
        super(TestDynamicOutputWithNumbersMain.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Wrong answer in test #1\n\n" +
            "Please find below the output of your program during this failed test.\n" +
            "Note that the '>' character indicates the beginning of the input line.\n" +
            "\n---\n\n" +
            "Print x and y: > 123 456\n" +
            "> 678\n" +
            "Another num:\n" +
            "> 248"
        );
        exception.expectMessage(not(containsString("Fatal error")));
    }

    @Override
    public List<TestCase<String>> generate() {
        return Arrays.asList(
            new TestCase<String>().setInput("123 456\n678\n248")
        );
    }

    @Override
    public CheckResult check(String reply, String attach) {
        return CheckResult.wrong("");
    }
}
