package org.hyperskill.hstest.v6.dynamic.input;

import org.hyperskill.hstest.v6.dynamic.output.SystemOutHandler;
import org.hyperskill.hstest.v6.exception.TestPassedException;
import org.hyperskill.hstest.v6.exception.WrongAnswerException;
import org.hyperskill.hstest.v6.stage.BaseStageTest;
import org.hyperskill.hstest.v6.testcase.CheckResult;
import org.hyperskill.hstest.v6.testcase.TestRun;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.hyperskill.hstest.v6.common.Utils.normalizeLineEndings;

public class SystemInMock extends InputStream {
    private StringReader currentReader;
    private List<String> inputLines = new LinkedList<>();
    private List<DynamicInputFunction> inputTextFuncs = new LinkedList<>();

    private int END_OF_LINE = 10; // represents \n

    void provideText(String text) {
        List<DynamicInputFunction> texts = new LinkedList<>();
        texts.add(new DynamicInputFunction(1, out -> text));
        setTexts(texts);
    }

    void setTexts(List<DynamicInputFunction> texts) {
        currentReader = new StringReader("");
        inputLines.clear();
        inputTextFuncs = texts;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                if (c == END_OF_LINE) {
                    break;
                }
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte)c;
            }
        } catch (IOException ignored) {
        }
        return i;
    }

    @Override
    public int read() throws IOException {

        // provide no more input if there was an exception
        TestRun testRun = BaseStageTest.getCurrTestRun();
        if (testRun != null && testRun.getErrorInTest() != null) {
            return -1;
        }

        int character = currentReader.read();
        if (character == -1) {
            ejectNextLine();
            character = currentReader.read();
            if (character == -1) {
                return -1;
            }
        }

        return character;
    }

    private void ejectNextLine() {
        if (inputLines.isEmpty()) {
            ejectNextInput();
            if (inputLines.isEmpty()) {
                return;
            }
        }
        String nextLine = inputLines.remove(0) + "\n";
        currentReader = new StringReader(nextLine);
        SystemOutHandler.injectInput(">" + nextLine);
    }

    private void ejectNextInput() {
        if (inputTextFuncs.isEmpty()) {
            return;
        }

        DynamicInputFunction inputFunction = inputTextFuncs.get(0);
        int triggerCount = inputFunction.getTriggerCount();
        if (triggerCount > 0) {
            inputFunction.trigger();
        }

        String currOutput = SystemOutHandler.getDynamicOutput();
        Function<String, Object> nextFunc = inputFunction.getInputFunction();

        String newInput;
        try {
            Object obj = nextFunc.apply(currOutput);
            if (obj instanceof String) {
                newInput = (String) obj;
            } else if (obj instanceof CheckResult) {
                CheckResult result = (CheckResult) obj;
                if (result.isCorrect()) {
                    throw new TestPassedException();
                } else {
                    String errorText = result.getFeedback();
                    throw new WrongAnswerException(errorText);
                }
            } else {
                throw new Exception("Dynamic input should return " +
                    "String or CheckResult objects only. Found: " + obj.getClass());
            }
        } catch (Throwable throwable) {
            BaseStageTest.getCurrTestRun().setErrorInTest(throwable);
            return;
        }

        if (inputFunction.getTriggerCount() == 0) {
            inputTextFuncs.remove(0);
        }

        newInput = normalizeLineEndings(newInput);
        inputLines.addAll(Arrays.asList(newInput.split("\n")));
    }

}
