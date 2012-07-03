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
public interface CommandExecutor {

  /**
   * Executes command in a given context.
   * @param command Command to execute.
   * @param state Starting state.
   * @return State after the execution, or <b>State.INVALID</b>, or <b>State.END</b>.
   */
  public State execute(int command, State state);

}
