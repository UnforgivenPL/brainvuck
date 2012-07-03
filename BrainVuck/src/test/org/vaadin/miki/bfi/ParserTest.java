package test.org.vaadin.miki.bfi;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;

import org.vaadin.miki.bv.client.Parser;
import org.vaadin.miki.bv.client.Brainfuck;

public class ParserTest {

  private Parser parser = null;

  @Before
  public void setUp() throws Exception {
    parser = new Parser(new Brainfuck());
  }

  @After
  public void tearDown() throws Exception {
    parser = null;
  }

  @Test
  public void testLocateLoopEnd() {
    for(int i: "Vaadin }>".toCharArray()) System.out.println(i);
    int expectedReturn = 5;
    int actualReturn = parser.locateLoopEnd("++[--]>".toCharArray(), 2);
    assertEquals("return value", expectedReturn, actualReturn);
    expectedReturn = 10;
    actualReturn = parser.locateLoopEnd("++[-[-<]+>]>".toCharArray(), 2);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  @Test
  public void testLocateLoopStart() {
    int expectedReturn = 2;
    int actualReturn = parser.locateLoopStart("++[--]>".toCharArray(), 5);
    assertEquals("return value", expectedReturn, actualReturn);
    expectedReturn = 2;
    actualReturn = parser.locateLoopStart("++[-[-<]+>]>".toCharArray(), 10);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  @Test
  public void testParse() throws Parser.ParsingException {
    char[] code = "+++----><<><>>----++=adkosadkapsodkaposkd++++-.,[--]".toCharArray();
    boolean expectedReturn = true;
    boolean actualReturn = parser.parse(code);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  @Test
  public void testStrip() {
    char[] code = "this++is--some+-code.".toCharArray();
    char[] expectedReturn = "++--+-.".toCharArray();
    char[] actualReturn = parser.strip(code);
    assertTrue("return value", Arrays.equals(expectedReturn, actualReturn));
  }

}
