package org.hyperskill.hstest.v7.outcomes;

public class WrongAnswerOutcome extends Outcome {

    public WrongAnswerOutcome(int testNum, String feedback) {
        testNumber = testNum;
        errorText = feedback;
        stackTrace = "";
    }

    @Override
    protected String getType() {
        return "Wrong answer";
    }
}
