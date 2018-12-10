package ru.unn.agile.fraction.view;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import ru.unn.agile.fraction.model.Fraction.Operation;
import ru.unn.agile.fraction.viewmodel.ViewModel;
import ru.unn.agile.fraction.infrastructure.TxtLogger;

public class Fraction {
    @FXML
    private ViewModel viewModel;

    @FXML
    private TextField firstNumerator;
    @FXML
    private TextField firstDenominator;
    @FXML
    private TextField secondNumerator;
    @FXML
    private TextField secondDenominator;
    @FXML
    private TextField resultNumerator;
    @FXML
    private TextField resultDenominator;
    @FXML
    private ComboBox<Operation> cbOperation;
    @FXML
    private Button btnCalc;

    @FXML
    void initialize() {
        viewModel.setLogger(new TxtLogger("./FractionCalculator.log"));

        firstNumerator.textProperty().bindBidirectional(viewModel.firstNumeratorProperty());
        firstDenominator.textProperty().bindBidirectional(viewModel.firstDenominatorProperty());
        secondNumerator.textProperty().bindBidirectional(viewModel.secondNumeratorProperty());
        secondDenominator.textProperty().bindBidirectional(viewModel.secondDenominatorProperty());
        resultNumerator.textProperty().bindBidirectional(viewModel.resultNumeratorProperty());
        resultDenominator.textProperty().bindBidirectional(viewModel.resultDenominatorProperty());

        cbOperation.valueProperty().bindBidirectional(viewModel.operationProperty());

        btnCalc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                viewModel.calculate();
            }
        });
    }
}
