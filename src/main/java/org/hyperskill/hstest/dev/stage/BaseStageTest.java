package org.hyperskill.hstest.dev.stage;

import org.hyperskill.hstest.dev.exception.FailureHandler;
import org.hyperskill.hstest.dev.statics.StaticFieldsManager;
import org.hyperskill.hstest.dev.testcase.CheckResult;
import org.hyperskill.hstest.dev.testcase.TestCase;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import static org.hyperskill.hstest.dev.common.Utils.*;
import static org.junit.Assert.*;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public abstract class BaseStageTest<AttachType> {

    private final Class<?> testedClass;
    private Class userClass;

    private final Object testedObject;
    private Method mainMethod;

    private boolean overrodeGenerate;
    private boolean overrodeCheck;

    private final List<TestCase<AttachType>> testCases = new ArrayList<>();

    public BaseStageTest(Class testedClass) {
        this(testedClass, null);
    }

    public BaseStageTest(Class testedClass, Object testedObject) {
        this.testedClass = testedClass;
        this.testedObject = testedObject;
    }

    @Rule
    public SystemOutRule systemOut = new SystemOutRule().enableLog();

    @Rule
    public TextFromStandardInputStream systemIn = emptyStandardInputStream();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @BeforeClass
    public static void setUp() {
        Locale.setDefault(Locale.US);
        System.setProperty("line.separator", "\n");
    }

    private void initTests() throws Exception {

        try {
            mainMethod = testedClass.getMethod("main", String[].class);
        } catch (NoSuchMethodException ex) {
            throw new Exception("No main method found");
        }

        boolean isMethodStatic = Modifier.isStatic(mainMethod.getModifiers());

        if (!isMethodStatic) {
            throw new IllegalArgumentException("Main method is not static");
        }

        if (testedObject != null) {
            userClass = testedObject.getClass();
        } else {
            userClass = mainMethod.getDeclaringClass();
        }

        // TODO below is a custom polymorphic behaviour implemented that may be skipped in future

        String myName = BaseStageTest.class.getName();

        String generateOwner = getClass()
            .getMethod("generate")
            .getDeclaringClass()
            .getName();

        String checkOwner = getClass()
            .getMethod("check", String.class, Object.class)
            .getDeclaringClass()
            .getName();

        overrodeGenerate = !myName.equals(generateOwner);
        overrodeCheck = !myName.equals(checkOwner);

        if (overrodeGenerate) {
            testCases.addAll(generate());
            if (testCases.size() == 0) {
                throw new Exception("No tests provided by \"generate\" method");
            }
        } else {
            throw new Exception("Can't create tests: override \"generate\" method");
        }

        if (!overrodeCheck) {
            throw new Exception("Can't check result: override \"check\" method");
        }
    }

    @Test
    public void start() {
        int currTest = 0;
        try {
            initTests();
            String savingPackage;
            if (userClass.getPackage() != null) {
                savingPackage = userClass.getPackage().getName();
            } else {
                savingPackage = StaticFieldsManager.getTopPackage(userClass);
            }
            StaticFieldsManager.saveStaticFields(savingPackage);

            if (overrodeGenerate) {
                for (TestCase<AttachType> test : testCases) {
                    currTest++;
                    System.err.println("Start test " + currTest);

                    createFiles(test.getFiles());
                    ExecutorService pool = startThreads(test.getProcesses());
                    String output = run(test);
                    CheckResult result = checkSolution(test, output);

                    stopThreads(test.getProcesses(), pool);
                    deleteFiles(test.getFiles());
                    StaticFieldsManager.resetStaticFields();

                    String errorMessage = "Wrong answer in test #" + currTest
                        + "\n\n" + result.getFeedback().trim();

                    if (FailureHandler.detectStaticCloneFails()) {
                        errorMessage += "\n\n" + FailureHandler.avoidStaticsMsg;
                    }

                    assertTrue(errorMessage, result.isCorrect());
                }
            }
        } catch (Throwable t) {
            fail(FailureHandler.getFeedback(t, currTest));
        }
    }

    private String run(TestCase<?> test) throws Exception {
        systemIn.provideLines(normalizeLineEndings(test.getInput()).trim());
        systemOut.clearLog();
        if (test.getArgs().size() == 0) {
            test.addArgument(new String[]{});
        }
        mainMethod.invoke(testedObject, test.getArgs().toArray());
        return normalizeLineEndings(systemOut.getLog());
    }

    private CheckResult checkSolution(TestCase<AttachType> test, String output) {
        CheckResult byChecking = new CheckResult(true);
        CheckResult bySolving = new CheckResult(true);

        if (overrodeCheck) {
            byChecking = check(output, test.getAttach());
        }

        boolean isCorrect = byChecking.isCorrect() && bySolving.isCorrect();
        String resultFeedback = (byChecking.getFeedback() + "\n" + bySolving.getFeedback()).trim();

        return new CheckResult(isCorrect, resultFeedback);
    }

    public List<TestCase<AttachType>> generate() {
        return new ArrayList<>();
    }

    /*public List<PredefinedIOTestCase> generatePredefinedInputOutput() {
        return new ArrayList<>();
    }*/

    public CheckResult check(String reply, AttachType clue) {
        return CheckResult.FALSE;
    }

    /*public String solve(String input) {
        return "";
    }

    public CheckResult checkSolved(String reply, String clue) {
        boolean isCorrect = reply.trim().equals(clue.trim());
        return new CheckResult(isCorrect);
    }*/
}
