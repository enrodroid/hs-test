package org.hyperskill.hstest.v7.exception.outcomes;

public class FatalError extends Error {
    public FatalError(String errorText) {
        super(errorText);
    }
}