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
public class StringProvider implements InputProvider {
  private final String data;
  private int position = 0;

  public StringProvider(String data) {
    this.data = data;
  }

  /**
   * getNextInput
   *
   * @return char
   * @todo Implement this org.vaadin.miki.bfi.InputProvider method
   */
  public char getNextInput() {
    if(this.position<this.data.length()) return this.data.charAt(this.position++);
    else return '\0';
  }
}
