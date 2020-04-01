package org.hyperskill.hstest.v7.stage;

import org.hyperskill.hstest.v7.dynamic.DynamicClassLoader;
import org.hyperskill.hstest.v7.dynamic.SystemHandler;
import org.hyperskill.hstest.v7.dynamic.input.SystemInHandler;
import org.hyperskill.hstest.v7.dynamic.output.SystemOutHandler;
import org.hyperskill.hstest.v7.exception.outcomes.ExceptionWithFeedback;
import org.hyperskill.hstest.v7.exception.outcomes.FatalErrorException;
import org.hyperskill.hstest.v7.exception.outcomes.TestPassed;
import org.hyperskill.hstest.v7.exception.outcomes.TimeLimitException;
import org.hyperskill.hstest.v7.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.v7.outcomes.Outcome;
import org.hyperskill.hstest.v7.testcase.CheckResult;
import org.hyperskill.hstest.v7.testcase.TestCase;
import org.hyperskill.hstest.v7.testcase.TestRun;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.contrib.java.lang.system.internal.CheckExitCalled;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.hyperskill.hstest.v7.common.FileUtils.createFiles;
import static org.hyperskill.hstest.v7.common.FileUtils.deleteFiles;
import static org.hyperskill.hstest.v7.common.ProcessUtils.startThreads;
import static org.hyperskill.hstest.v7.common.ProcessUtils.stopThreads;
import static org.hyperskill.hstest.v7.common.ReflectionUtils.getMainMethod;
import static org.hyperskill.hstest.v7.dynamic.output.ColoredOutput.RED_BOLD;
import static org.hyperskill.hstest.v7.dynamic.output.ColoredOutput.RESET;
import static org.hyperskill.hstest.v7.exception.FailureHandler.getUserException;
import static org.hyperskill.hstest.v7.testcase.CheckResult.correct;
import static org.hyperskill.hstest.v7.testcase.CheckResult.wrong;
import static org.junit.Assert.fail;


public abstract class StageTest<AttachType> {

    private final Class<?> testedClass;

    private final Object testedObject;
    private Method mainMethod;

    private final List<TestCase<AttachType>> testCases = new ArrayList<>();

    protected boolean needReloadClass = true;

    private static TestRun currTestRun;

    public static TestRun getCurrTestRun() {
        return currTestRun;
    }

    public StageTest(Class<?> testedClass) {
        this(testedClass, null);
    }

    public StageTest(Class<?> testedClass, Object testedObject) {
        this.testedClass = testedClass;
        this.testedObject = testedObject;
    }

    @BeforeClass
    public static void setUp() {
        Locale.setDefault(Locale.US);
        System.setProperty("line.separator", "\n");
    }

    private void initTests() throws Exception {

        mainMethod = getMainMethod(testedClass);

        String myName = StageTest.class.getName();

        String generateOwner = getClass()
            .getMethod("generate")
            .getDeclaringClass()
            .getName();

        String checkOwner = getClass()
            .getMethod("check", String.class, Object.class)
            .getDeclaringClass()
            .getName();

        boolean overrodeGenerate = !myName.equals(generateOwner);
        boolean overrodeCheck = !myName.equals(checkOwner);

        if (overrodeGenerate) {
            testCases.addAll(generate());
            if (testCases.size() == 0) {
                throw new FatalErrorException(
                    "No tests provided by \"generate\" method");
            }
        } else {
            throw new FatalErrorException(
                "Can't create tests: override \"generate\" method");
        }

        for (TestCase<AttachType> testCase : testCases) {
            if (testCase.getCheckFunc() == null) {
                if (!overrodeCheck) {
                    throw new FatalErrorException(
                        "Can't check result: override \"check\" method");
                }
                testCase.setCheckFunc(this::check);
            }
        }
    }

    @Test
    public final void start() {
        int currTest = 0;
        try {
            SystemHandler.setUpSystem();
            initTests();

            for (TestCase<AttachType> test : testCases) {
                currTest++;
                SystemOutHandler.getRealOut().println(
                    RED_BOLD + "\nStart test " + currTest + RESET
                );

                currTestRun = new TestRun(currTest, test);

                createFiles(test.getFiles());
                ExecutorService pool = startThreads(test.getProcesses());

                String output = runTest(test);
                CheckResult result = checkSolution(test, output);

                stopThreads(test.getProcesses(), pool);
                deleteFiles(test.getFiles());

                if (!result.isCorrect()) {
                    throw new WrongAnswer(result.getFeedback());
                }
            }
        } catch (Throwable t) {
            Outcome outcome = Outcome.getOutcome(t, currTest);
            String failText = outcome.toString();
            fail(failText);
        } finally {
            currTestRun = null;
            SystemHandler.tearDownSystem();
        }
    }

    private String runTest(TestCase<AttachType> test) throws Throwable {
        SystemInHandler.setInputFuncs(test.getInputFuncs());
        SystemOutHandler.resetOutput();
        currTestRun.setErrorInTest(null);

        runMain(test.getArgs(), test.getTimeLimit());
        checkErrors(test);

        return SystemOutHandler.getOutput();
    }

    private void runMain(List<String> args, int timeLimit) {
        ExecutorService executorService = newSingleThreadExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });

        Future<?> future = executorService.submit(() -> {
            invokeMain(args);
            return null;
        });

        try {
            if (timeLimit <= 0) {
                future.get();
            } else {
                future.get(timeLimit, TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException ex) {
            currTestRun.setErrorInTest(new TimeLimitException(timeLimit));
        } catch (Throwable ex) {
            currTestRun.setErrorInTest(ex);
        } finally {
            executorService.shutdownNow();
        }
    }

    private void invokeMain(List<String> args) {
        try {
            Method methodToInvoke = mainMethod;

            if (needReloadClass) {
                ClassLoader dcl = new DynamicClassLoader(testedClass);
                try {
                    Class<?> reloaded = dcl.loadClass(testedClass.getName());
                    methodToInvoke = getMainMethod(reloaded);
                } catch (Exception ex) {
                    currTestRun.setErrorInTest(ex);
                }
            }

            methodToInvoke.invoke(testedObject, new Object[] {
                args.toArray(new String[0])
            });
        } catch (InvocationTargetException ex) {
            if (currTestRun.getErrorInTest() == null) {
                // CheckExitCalled is thrown in case of System.exit()
                // consider System.exit() like normal exit
                if (!(ex.getCause() instanceof CheckExitCalled)) {
                    currTestRun.setErrorInTest(
                        new ExceptionWithFeedback("", getUserException(ex)));
                }
            }
        } catch (IllegalAccessException ex) {
            currTestRun.setErrorInTest(ex);
        }
    }

    private void checkErrors(TestCase<AttachType> test) throws Throwable {
        if (currTestRun.getErrorInTest() == null) {
            return;
        }

        Throwable errorInTest = currTestRun.getErrorInTest();

        if (errorInTest instanceof TestPassed) {
            return;
        }

        if (errorInTest instanceof ExceptionWithFeedback) {
            Throwable userException =
                ((ExceptionWithFeedback) errorInTest).getRealException();

            Map<Class<? extends Throwable>, String> feedbackOnExceptions =
                test.getFeedbackOnExceptions();

            for (Class<? extends Throwable> exClass : feedbackOnExceptions.keySet()) {
                String feedback = feedbackOnExceptions.get(exClass);
                if (userException != null && exClass.isAssignableFrom(userException.getClass())) {
                    throw new ExceptionWithFeedback(feedback, userException);
                }
            }
        }

        throw errorInTest;
    }

    private CheckResult checkSolution(TestCase<AttachType> test, String output) {
        if (currTestRun.getErrorInTest() != null
            && currTestRun.getErrorInTest() instanceof TestPassed) {
            return correct();
        }
        try {
            return test.getCheckFunc().apply(output, test.getAttach());
        } catch (WrongAnswer ex) {
            return wrong(ex.getMessage());
        } catch (TestPassed ex) {
            return correct();
        }
    }

    public List<TestCase<AttachType>> generate() {
        return new ArrayList<>();
    }

    public CheckResult check(String reply, AttachType attach) {
        return wrong("");
    }
}
