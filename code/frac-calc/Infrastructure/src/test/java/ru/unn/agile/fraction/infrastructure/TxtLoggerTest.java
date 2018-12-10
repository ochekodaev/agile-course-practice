package ru.unn.agile.fraction.infrastructure;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class TxtLoggerTest {
    private static final String FILE_NAME = "./TxtLoggerTest.log";
    private static final String DATE_TIME_LOG_LINE =
            "^\\d{2}.\\d{2}.\\d{4} \\d{2}:\\d{2}:\\d{2} -> ";
    private static final String TEST_MESSAGE = "Test message";

    private TxtLogger txtLogger;

    @Before
    public void setUp() {
        txtLogger = new TxtLogger(FILE_NAME);
    }

    @After
    public void tearDown() {
        txtLogger = null;
    }

    @Test
    public void canCreateLoggerWithCorrectFileName() {
        assertNotNull(txtLogger);
    }

    @Test
    public void canCreateLogFileWithCorrectFileName() {
        File f = new File(FILE_NAME);

        assertTrue(f.exists());
    }

    @Test
    public void canWriteLogMessageToLogger() {
        txtLogger.log(TEST_MESSAGE);

        List<String> logList = txtLogger.getLog();
        assertTrue(logList.get(0).matches(DATE_TIME_LOG_LINE + TEST_MESSAGE));
    }

    @Test
    public void canWriteSeveralLogMessagesToLogger() {
        String[] messages = {TEST_MESSAGE + " 0", TEST_MESSAGE + " 1"};

        for (String message : messages) {
            txtLogger.log(message);
        }

        List<String> logList = txtLogger.getLog();
        for (int i = 0; i < logList.size(); i++) {
            assertTrue(logList.get(i).matches(DATE_TIME_LOG_LINE + messages[i]));
        }
    }

    @Test(expected = Test.None.class)
    public void noThrowWhenCreateTxtLoggerWithIncorrectFileNameAndCreatedEmptyLogger() {
        TxtLogger emptyLogger = new TxtLogger("");

        assertEquals(0, emptyLogger.getLog().size());
    }

    @Test(expected = Test.None.class)
    public void noThrowWhenWriteToTxtLoggerWithIncorrectFileName() {
        TxtLogger emptyLogger = new TxtLogger("");

        emptyLogger.log(TEST_MESSAGE);

        assertEquals(0, emptyLogger.getLog().size());
    }

    @Test(expected = Test.None.class)
    public void noThrowWhenReadFromTxtLoggerWithIncorrectFileName() {
        TxtLogger emptyLogger = new TxtLogger("");

        List<String> logList = emptyLogger.getLog();

        assertEquals(0, logList.size());
    }
}
