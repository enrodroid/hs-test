package outcomes.dynamic_testing;

import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;
import org.hyperskill.hstest.testing.TestedProgram;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

class TestDynamicTestingProgramNotFinishedAfterTestButExceptionHappenedServer {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Server started!");
            System.out.println("S1: " + scanner.nextLine());
            Thread.sleep(1000);
            System.out.println("S2: " + scanner.nextLine());
        } catch (Throwable th) {
            System.out.println("Server stopped!");
            System.out.println(th.toString());
        }
    }
}

class TestDynamicTestingProgramNotFinishedAfterTestButExceptionHappenedClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Client started!");
        System.out.println("C1: " + scanner.nextLine());
        System.out.println("C2: " + scanner.nextLine());
    }
}

public class TestDynamicTestingProgramNotFinishedAfterTestButExceptionHappened extends StageTest<String> {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        exception.expect(AssertionError.class);
        exception.expectMessage(
            "Exception in test #1\n" +
                "\n" +
                "Probably your program run out of input (Scanner tried to read more than expected).\n" +
                "\n" +
                "java.util.NoSuchElementException: No line found"
        );
        exception.expectMessage(not(containsString("Fatal error")));
    }

    @Override
    public List<TestCase<String>> generate() {
        return Arrays.asList(
            new TestCase<String>().setDynamicTesting(() -> {

                TestedProgram server = new TestedProgram(
                    TestDynamicTestingProgramNotFinishedAfterTestButExceptionHappenedServer.class);

                TestedProgram client = new TestedProgram(
                    TestDynamicTestingProgramNotFinishedAfterTestButExceptionHappenedClient.class);

                String out1 = server.start();
                String out2 = client.start();
                if (!out1.equals("Server started!\n")
                    || !out2.equals("Client started!\n")) {
                    return CheckResult.wrong("");
                }

                return null;
            })
        );
    }

    @Override
    public CheckResult check(String reply, String attach) {
        if (reply.contains("Server stopped!\n" +
            "java.util.NoSuchElementException: No line found")
            && reply.contains("Client stopped!")) {
            return CheckResult.correct();
        }
        return CheckResult.wrong("");
    }
}
