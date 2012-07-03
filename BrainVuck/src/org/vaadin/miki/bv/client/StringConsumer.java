package org.vaadin.miki.bv.client;

/**
 * <p>Title: Brainfuck Interpreter</p>
 *
 * <p>Description: Simple, minimalistic BF interpreter, with lots of events to
 * be hooked into.</p>
 *
 * <p>Copyright: (c) 2012</p>
 *
 * <p>Company: Vaadin, Ltd.</p>
 *
 * @author Miki
 * @version 0.1
 */
public class StringConsumer implements OutputConsumer {

  private StringBuilder builder = new StringBuilder();

  /**
   * consumeOutput
   *
   * @param c char
   * @todo Implement this org.vaadin.miki.bfi.OutputConsumer method
   */
  public void consumeOutput(int c) {
    this.builder.append((char)c);
  }

  public String toString() {
    return this.builder.toString();
  }
}
