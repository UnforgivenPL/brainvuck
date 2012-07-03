package test.org.vaadin.miki.bfi;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.vaadin.miki.bv.client.Brainfuck;
import org.vaadin.miki.bv.client.Interpreter;
import org.vaadin.miki.bv.client.StringConsumer;
import org.vaadin.miki.bv.client.StringProvider;

public class InterpreterTest {

  private Interpreter interpreter = null;
  private StringConsumer consumer = new StringConsumer();
  private StringProvider provider = new StringProvider("34");

  @Before
  public void setUp() throws Exception {
    Brainfuck bf = new Brainfuck();
    interpreter = new Interpreter(200, bf, bf, this.provider, this.consumer, bf);
    interpreter.setCode("++[--]>".toCharArray());
  }

  @After
  public void tearDown() throws Exception {
    interpreter = null;
  }

  @Test
  public void testHelloWorld() {
    // code from wikipedia
    String code = "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.";
    String expected = "Hello World!\n";
    interpreter.setCode(code.toCharArray());
    interpreter.run();
    assertEquals("not hello world", expected, this.consumer.toString());
  }

  @Test
  public void testHelloWorldCommented() {
    // code from wikipedia
    String code = "+Ignored+++++++++[>+++++++>++++++++Or this++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.This should not matter anyway+++.------.--------.>+.>.";
    String expected = "Hello World!\n";
    interpreter.setCode(code.toCharArray());
    interpreter.run();
    assertEquals("not hello world", expected, this.consumer.toString());
  }

  
  @Test
  public void testSimpleAdding() {
    // code from wikipedia
    String code = ",>++++++[<-------->-],[<+>-]<.";
    String expected = "7";
    interpreter.setCode(code.toCharArray());
    interpreter.run();
    assertEquals("not adds", expected, this.consumer.toString());
    assertEquals("not adds mem", 55, interpreter.getState().getMemory()[0]);
  }
  
  @Test
  public void testInfinite() {
    String code = "+[]-";
    interpreter.setCode(code.toCharArray());
    interpreter.run();
  }

}
