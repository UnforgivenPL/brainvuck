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
public interface Syntax {

  public static final int NOOP = 0;

  public int commandFor(char c);
  public boolean opensSection(char c);
  public boolean closesSection(char c);
  public char matching(char c);

}
