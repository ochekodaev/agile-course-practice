package ru.unn.agile.fraction.viewmodel;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import ru.unn.agile.fraction.model.Fraction;
import ru.unn.agile.fraction.model.Fraction.Operation;

public class ViewModel {
    private StringProperty firstNumerator = new SimpleStringProperty();
    private StringProperty firstDenominator = new SimpleStringProperty();
    private StringProperty secondNumerator = new SimpleStringProperty();
    private StringProperty secondDenominator = new SimpleStringProperty();

    private ObjectProperty<ObservableList<Operation>> operations =
            new SimpleObjectProperty<>(FXCollections.observableArrayList(Operation.values()));
    private ObjectProperty<Operation> operation = new SimpleObjectProperty<>();
    private BooleanProperty calculationDisabled = new SimpleBooleanProperty();

    private StringProperty resultNumerator = new SimpleStringProperty();
    private StringProperty resultDenominator = new SimpleStringProperty();

    private final StringProperty status = new SimpleStringProperty();
    private final List<InputValueChanger> valueChangedListeners = new ArrayList<>();

    private final StringProperty log = new SimpleStringProperty();

    private ILogger logger;

    public ViewModel() {
        initDefaultFields();
    }

    public ViewModel(final ILogger logger) {
        initDefaultFields();
        setLogger(logger);
    }

    private void initDefaultFields() {
        firstNumerator.set("");
        firstDenominator.set("");
        secondNumerator.set("");
        secondDenominator.set("");
        operation.set(Operation.ADD);
        resultNumerator.set("");
        resultDenominator.set("");
        status.set(Status.WAITING.toString());
        log.set("");

        BooleanBinding couldCalculate = new BooleanBinding() {
            {
                super.bind(firstNumerator, firstDenominator,
                        secondNumerator, secondDenominator);
            }

            @Override
            protected boolean computeValue() {
                return getInputStatus() == Status.READY;
            }
        };
        calculationDisabled.bind(couldCalculate.not());

        final List<StringProperty> fields = new ArrayList<StringProperty>() {
            {
                add(firstNumerator);
                add(firstDenominator);
                add(secondNumerator);
                add(secondDenominator);
            }
        };

        for (StringProperty field : fields) {
            final InputValueChanger listener = new InputValueChanger();
            field.addListener(listener);
            valueChangedListeners.add(listener);
        }

        final InputValueChanger listener = new InputValueChanger();
        operation.addListener(listener);
        valueChangedListeners.add(listener);
    }

    public StringProperty firstNumeratorProperty() {
        return firstNumerator;
    }

    public StringProperty firstDenominatorProperty() {
        return firstDenominator;
    }

    public StringProperty secondNumeratorProperty() {
        return secondNumerator;
    }

    public StringProperty secondDenominatorProperty() {
        return secondDenominator;
    }

    public StringProperty resultNumeratorProperty() {
        return resultNumerator;
    }

    public StringProperty resultDenominatorProperty() {
        return resultDenominator;
    }

    public final ObservableList<Operation> getOperations() {
        return operations.get();
    }

    public ObjectProperty<Operation> operationProperty() {
        return operation;
    }

    public BooleanProperty calculationDisabledProperty() {
        return calculationDisabled;
    }

    public final boolean isCalculationDisabled() {
        return calculationDisabled.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public final String getStatus() {
        return status.get();
    }

    public StringProperty logProperty() {
        return log;
    }

    public final String getLog() {
        return log.get();
    }

    public List<String> getLogList() {
        return logger.getLog();
    }

    public final void setLogger(final ILogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger can't be null");
        }
        this.logger = logger;
    }

    private class InputValueChanger implements ChangeListener<Object> {
        @Override
        public void changed(final ObservableValue<?> obs,
                            final Object prevVal,
                            final Object nextVal) {
            status.set(getInputStatus().toString());
            if (obs == operation) {
                writeToLog(operationStateLogMessage());
            } else {
                writeToLog(fractionsStateLogMessage());
            }
        }
    }

    private Status getInputStatus() {
        Status inputStatus = Status.READY;
        boolean fn = firstNumerator.get().isEmpty();
        boolean fd = firstDenominator.get().isEmpty();
        boolean sn = secondNumerator.get().isEmpty();
        boolean sd = secondDenominator.get().isEmpty();

        if (fn || fd || sn || sd) {
            inputStatus = Status.WAITING;
        }

        try {
            if (!fn) {
                Integer.parseInt(firstNumerator.get());
            }
            if (!fd) {
                Integer.parseInt(firstDenominator.get());
            }
            if (!sn) {
                Integer.parseInt(secondNumerator.get());
            }
            if (!sd) {
                Integer.parseInt(secondDenominator.get());
            }
        } catch (NumberFormatException nfe) {
            inputStatus = Status.BAD_FORMAT;
        }

        return inputStatus;
    }

    private void writeToLog(final String message) {
        logger.log(message);
        StringBuilder logMessages = new StringBuilder();
        for (String line : getLogList()) {
            logMessages.append(line).append("\n");
        }
        log.set(logMessages.toString());
    }

    private String currentStateForAllFieldsLogMessage() {
        Operation currentOperation = operation.get();
        return String.format(LogMessages.CURRENT_STATE,
                firstNumerator.get(),
                firstDenominator.get(),
                secondNumerator.get(),
                secondDenominator.get(),
                resultNumerator.get(),
                resultDenominator.get(),
                currentOperation.toString(),
                status.get(),
                calculationDisabled.get(),
                getOperations().toString());
    }

    private String fractionsStateLogMessage() {
        return String.format(LogMessages.FRACTIONS_WERE_CHANGED,
                firstNumerator.get(),
                firstDenominator.get(),
                secondNumerator.get(),
                secondDenominator.get(),
                status.get(),
                calculationDisabled.get());
    }

    private String operationStateLogMessage() {
        Operation currentOperation = operation.get();
        return String.format(LogMessages.OPERATION_WAS_CHANGED,
                currentOperation.toString());
    }

    private String stateAfterCalculateLogMessage() {
        return LogMessages.CALCULATE_BUTTON_WAS_PRESSED + " "
                + currentStateForAllFieldsLogMessage();
    }

    public void calculate() {
        if (calculationDisabled.get()) {
            return;
        }

        Fraction f1 = new Fraction(Integer.parseInt(firstNumerator.get()),
                Integer.parseInt(firstDenominator.get()));
        Fraction f2 = new Fraction(Integer.parseInt(secondNumerator.get()),
                Integer.parseInt(secondDenominator.get()));
        Fraction res = operation.get().apply(f1, f2);

        resultNumerator.set(String.valueOf(res.getNumerator()));
        resultDenominator.set(String.valueOf(res.getDenominator()));
        status.set(Status.SUCCESS.toString());

        writeToLog(stateAfterCalculateLogMessage());
    }

    public static final class LogMessages {
        public static final String CURRENT_STATE =
                "Current state: "
                        + "First fraction (%s/%s), "
                        + "Second fraction (%s/%s), "
                        + "Result fraction (%s/%s), "
                        + "Operation (%s), "
                        + "Status (%s), "
                        + "Calculate disabled (%s), "
                        + "All operations (%s).";
        public static final String FRACTIONS_WERE_CHANGED =
                "Fractions were changed: "
                        + "First fraction (%s/%s), "
                        + "Second fraction (%s/%s), "
                        + "Status (%s), "
                        + "Calculate disabled (%s).";
        public static final String OPERATION_WAS_CHANGED =
                "Operation was changed: "
                        + "Operation (%s).";
        public static final String CALCULATE_BUTTON_WAS_PRESSED =
                "Calculate button was pressed.";
    }
    }

enum Status {
    WAITING("Please provide input data"),
    READY("Press '=' button"),
    BAD_FORMAT("Bad input data format"),
    SUCCESS("Success");

    private final String name;

    Status(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
