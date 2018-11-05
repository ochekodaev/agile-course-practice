package ru.unn.agile.numerical_integration.ViewModel.legacy;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;

public class ViewModel {
    public static final String FLOAT_NUMBER_REGEX =
        "[+-]?((\\d+\\.?\\d*)|(\\d*\\.\\d+))";

    public static final String ERROR_WRONG_LEFT_BORDER =
        "Wrong left border value";
    public static final String ERROR_WRONG_RIGHT_BORDER =
        "Wrong right border value";
    public static final String ERROR_WRONG_SPLITS_NUMBER =
        "Wrong splits number value";
    public static final String ERROR_WRONG_FUNCTION_TEXT =
        "Wrong function";

    private String functionText;
    private String leftBorderText;
    private String rightBorderText;
    private String splitsNumberText;
    private String outputMessage;
    private AbstractMap<ErrorKind, String> errorsList;


    public ViewModel() {
        functionText = "x";
        leftBorderText = "0.0";
        rightBorderText = "1.0";
        splitsNumberText = "1";
        outputMessage = "";
        errorsList = new HashMap<>();
        checkErrors();
    }

    public String getFunctionText() {
        return functionText;
    }

    public String getLeftBorderValue() {
        return leftBorderText;
    }

    public String getRightBorderValue() {
        return rightBorderText;
    }

    public String getSplitsNumber() {
        return splitsNumberText;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public void setLeftBorderValue(final String value) {
        leftBorderText = value;
        checkErrors();
    }

    public void setRightBorderValue(final String value) {
        rightBorderText = value;
        checkErrors();
    }

    public void setSplitsNumber(final String value) {
        splitsNumberText = value;
        checkErrors();
    }

    public void setFunction(final String value) {
        functionText = value;
        checkErrors();
    }

    public boolean canComputeFunction() {
        return errorsList.isEmpty();
    }

    private void checkErrors() {
        checkFunctionText();
        checkLeftBorderValue();
        checkRightBorderValue();
        checkSplitsNumber();

        outputMessage = createErrorMessage();
    }

    private String createErrorMessage() {
        final int variableToMakeLinterHappy = 100;
        StringBuilder builder = new StringBuilder(variableToMakeLinterHappy);

        Iterator it = errorsList.entrySet().iterator();

        if (it.hasNext()) {
            builder.append("Unable to compute due to the following errors:\n");
        }

        while (it.hasNext()) {
            final HashMap.Entry pair = (HashMap.Entry) it.next();
            final ErrorKind errorKind = (ErrorKind) pair.getKey();
            final String errorMessage = (String) pair.getValue();
            builder.append(getErrorMessageBase(errorKind)).append(": ");
            builder.append(errorMessage).append(";");
            if (it.hasNext()) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    private static String getErrorMessageBase(final ErrorKind errorKind) {
        switch (errorKind) {
            case LeftBorderValue:
                return ERROR_WRONG_LEFT_BORDER;
            case RightBorderValue:
                return ERROR_WRONG_RIGHT_BORDER;
            case SplitsNumber:
                return ERROR_WRONG_SPLITS_NUMBER;
            case Function:
                return ERROR_WRONG_FUNCTION_TEXT;
            default:
                throw new RuntimeException("Unexpected error kind");
        }
    }

    private void checkFunctionText() {
        final MathParser parser = new MathParser(functionText);
        final boolean success = parser.eval(0);
        if (!success) {
            addError(ErrorKind.Function, parser.getError());
            return;
        }
        removeError(ErrorKind.Function);
    }

    private void checkLeftBorderValue() {
        if (leftBorderText.matches(FLOAT_NUMBER_REGEX)) {
            removeError(ErrorKind.LeftBorderValue);
        } else {
            addError(ErrorKind.LeftBorderValue, "Expected float number");
        }
    }

    private void checkRightBorderValue() {
        if (rightBorderText.matches(FLOAT_NUMBER_REGEX)) {
            removeError(ErrorKind.RightBorderValue);
        } else {
            addError(ErrorKind.RightBorderValue, "Expected float number");
        }
    }

    private void checkSplitsNumber() {
        if (!splitsNumberText.matches("[+-]?\\d+")) {
            addError(ErrorKind.SplitsNumber, "Expected integer number");
            return;
        }

        if (Integer.parseInt(splitsNumberText) < 1) {
            addError(ErrorKind.SplitsNumber, "Expected positive number");
            return;
        }

        removeError(ErrorKind.SplitsNumber);
    }

    private void addError(final ErrorKind kind, final String message) {
        errorsList.put(kind, message);
    }

    private void removeError(final ErrorKind kind) {
        errorsList.remove(kind);
    }
}
