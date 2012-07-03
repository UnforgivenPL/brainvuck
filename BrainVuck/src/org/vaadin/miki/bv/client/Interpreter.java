package org.vaadin.miki.bv.client;

import java.util.ArrayList;

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
public class Interpreter {
  private State state;
  private int memorySize;
  private final State.Initialiser initialiser;

  private long steps;

  private final ArrayList<InterpreterListener> listeners = new ArrayList<InterpreterListener>();
  private CommandExecutor[] executors;
  private final InputProvider provider;
  private final OutputConsumer consumer;

  private final Syntax syntax;

  public Interpreter(int memorySize, State.Initialiser initialiser, Syntax syntax, InputProvider provider, OutputConsumer consumer, CommandExecutor... executors) {
    this.memorySize = memorySize;
    this.syntax = syntax;
    this.initialiser = initialiser;
    this.executors = executors;
    this.provider = provider;
    this.consumer = consumer;
  }

  protected void init(char[] code) throws State.ErrorState {
    this.state = this.initialiser.getInitialState(code, this.memorySize, this.provider, this.consumer);
    this.steps = 0;
    this.doNotifyInitialised();
  }

  public boolean setCode(char[] code) {
    try {
      this.init(code);
      return true;
    }
    catch(State.ErrorState ses) {
      this.doNotifyError(ses);
      return false;
    }
  }

  public long run() {
    while(this.step()) ;
    return this.steps;
  }
  
  public long run(char[] code) {
    if(this.setCode(code)) {
     while(this.step()) ;
     return this.steps;
    }
    else return -1;
  }

  public boolean step() {
    try {
      if(this.state.getCodePosition()>= this.state.getCode().length || this.state == State.END) {
        this.doNotifyDone();
        return false;
      }
      char current = this.state.getCode()[this.state.getCodePosition()];
      int operation = this.syntax.commandFor(current);
      if(operation != Syntax.NOOP) {
        State result = null;
        // go through executors
        for(int zmp1 = 0; zmp1 < this.executors.length && result == null; zmp1++)
          result = this.executors[zmp1].execute(operation, this.state);
        // if the result is found AND is different than the current state, ok
        if(result != null && !this.state.equals(result))
          this.state = result;
        // null means the command was not executed - so, ignore it and move to next code fragment
        // this will likely be not called at all, it means that the syntax has been recognised, but there is no executor that does anything related to it
        else if(result == null)
          this.state = this.state.derivate(this.state.getCodePosition()+1, this.state.getCode(), this.state.getMemoryPointer(), this.state.getMemory(), this.state.getInputProvider(), this.state.getOutputConsumer());
        // if the state is exactly the same as the current state, stuck
        else if(this.state.equals(result)) {
          this.doNotifyError(new State.ErrorState("Stuck in an infinite loop at position "+result.getCodePosition()+".", result.getCode(), result.getCodePosition()));
          return false;
        }
      }
      // noop = go to next step
      else
        this.state = this.state.derivate(this.state.getCodePosition()+1, this.state.getCode(), this.state.getMemoryPointer(), this.state.getMemory(), this.state.getInputProvider(), this.state.getOutputConsumer());
      this.doNotifyOperationPerformed(operation);
      this.steps++;
      return true;
    }
    catch(Exception e) {
      this.doNotifyError(e);
      return false;
    }
  }

  public State getState() {
    return this.state;
  }

  protected void doNotifyOperationPerformed(int operation) {
    for(InterpreterListener il: this.listeners) il.commandExecuted(operation);
  }

  protected void doNotifyInitialised() {
    for(InterpreterListener il: this.listeners) il.initialised();
  }

  protected void doNotifyError(Exception e) {
    for(InterpreterListener il: this.listeners) il.error(e);
  }

  protected void doNotifyDone() {
    for(InterpreterListener il: this.listeners) il.done();
  }

  public void addInterpreterListener(InterpreterListener il) {
    this.listeners.add(il);
  }
  
  public void removeInterpreterListener(InterpreterListener il) {
    this.listeners.remove(il);
  }
  
  public long getSteps() {
    return this.steps;
  }
  
}
