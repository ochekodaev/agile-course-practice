package ru.unn.agile.fraction.infrastructure;

import ru.unn.agile.fraction.viewmodel.ViewModel;
import ru.unn.agile.fraction.viewmodel.ViewModelTest;

public class ViewModelWithTxtLoggerTest extends ViewModelTest {
    @Override
    public void setUp() {
        TxtLogger realLogger = new TxtLogger("./ViewModelWithTxtLoggerTest.log");
        super.setViewModel(new ViewModel(realLogger));
    }
}
