package dynamic;

import org.hyperskill.hstest.v7.dynamic.SystemHandler;
import org.hyperskill.hstest.v7.dynamic.input.SystemInHandler;
import org.hyperskill.hstest.v7.dynamic.output.SystemOutHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class TestDynamicOutput {

    Scanner scanner;

    @Before
    public void setUp() throws Exception {
        SystemHandler.setUpSystem();
        scanner = new Scanner(System.in);
    }

    @After
    public void tearDown() {
        SystemHandler.tearDownSystem();
    }

    @Test
    public void testNormalOutputHandler() {
        System.out.print("123");
        assertEquals("123", SystemOutHandler.getPartialOutput());
        System.out.print("456");
        assertEquals("456", SystemOutHandler.getPartialOutput());
    }

    @Test
    public void testNormalOutputHandlerWithNewLines() {
        System.out.println("123");
        assertEquals("123\n", SystemOutHandler.getPartialOutput());
        System.out.println("456");
        assertEquals("456\n", SystemOutHandler.getPartialOutput());
    }

    @Test
    public void testResetOutputHandler() {
        System.out.print("123");
        SystemOutHandler.resetOutput();
        assertEquals("", SystemOutHandler.getPartialOutput());
        System.out.print("456");
        assertEquals("456", SystemOutHandler.getPartialOutput());
    }
}
