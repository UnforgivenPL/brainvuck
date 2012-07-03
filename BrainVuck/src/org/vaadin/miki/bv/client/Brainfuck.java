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
public class Brainfuck implements Syntax, CommandExecutor, State.Initialiser {

  public static final int FORWARD = 1;
  public static final int BACK = 2;
  public static final int INCREASE = 4;
  public static final int DECREASE = 8;
  public static final int START_IF = 16;
  public static final int END_IF = 32;
  public static final int INPUT = 64;
  public static final int OUTPUT = 128;

  private Parser parser = new Parser(this);

  public int commandFor(char c) {
    switch(c) {
      case '>': return FORWARD;
      case '<': return BACK;
      case '+': return INCREASE;
      case '-': return DECREASE;
      case '[': return START_IF;
      case ']': return END_IF;
      case ',': return INPUT;
      case '.': return OUTPUT;
      default:  return NOOP;
    }
  }
  public boolean opensSection(char c) {return c=='[';}
  public boolean closesSection(char c) {return c==']';}
  public char matching(char c) {if(c=='[') return ']'; else if(c==']') return '['; else return c;}

  public State execute(int command, State state) {
    State result = null;
    int[] mem = state.getMemory();
    int pos = state.getCodePosition()+1;
    switch(command) {
      case BACK:
        result = state.derivate(pos, state.getCode(), state.getMemoryPointer()-1, mem, state.getInputProvider(), state.getOutputConsumer());
        break;
      case FORWARD:
        result = state.derivate(pos, state.getCode(), state.getMemoryPointer()+1, mem, state.getInputProvider(), state.getOutputConsumer());
        break;
      case INCREASE:
        mem[state.getMemoryPointer()]++;
        result = state.derivate(pos, state.getCode(), state.getMemoryPointer(), mem, state.getInputProvider(), state.getOutputConsumer());
        break;
      case DECREASE:
        mem[state.getMemoryPointer()]--;
        result = state.derivate(pos, state.getCode(), state.getMemoryPointer(), mem, state.getInputProvider(), state.getOutputConsumer());
        break;
      case INPUT:
        mem[state.getMemoryPointer()] = state.getInputProvider().getNextInput();
        result = state.derivate(pos, state.getCode(), state.getMemoryPointer(), mem, state.getInputProvider(), state.getOutputConsumer());
        break;
      case OUTPUT:
        state.getOutputConsumer().consumeOutput(state.getMemory()[state.getMemoryPointer()]);
        result = state.derivate(pos, state.getCode(), state.getMemoryPointer(), state.getMemory(), state.getInputProvider(), state.getOutputConsumer());
        break;
      case START_IF:
        result = state.derivate(state.getMemory()[state.getMemoryPointer()] == 0 ? this.parser.locateLoopEnd(state.getCode(), state.getCodePosition())+1 : state.getCodePosition()+1, state.getCode(), state.getMemoryPointer(), mem, state.getInputProvider(), state.getOutputConsumer());
        break;
      case END_IF:
        result = state.derivate(state.getMemory()[state.getMemoryPointer()] != 0 ? this.parser.locateLoopStart(state.getCode(), state.getCodePosition())+1 : state.getCodePosition()+1, state.getCode(), state.getMemoryPointer(), mem, state.getInputProvider(), state.getOutputConsumer());
        break;
    }
    return result;
  }

  public State getInitialState(char[] code, int memorySize, InputProvider provider, OutputConsumer consumer) throws State.ErrorState {
    try {
      if(this.parser.parse(code)) return new MachineState(code, 0, new int[memorySize], 0, provider, consumer);
    }
    catch(Parser.ParsingException ppe) {
      throw new State.ErrorState(ppe.getMessage(), ppe.getCode(), ppe.getPosition());
    }
    throw new State.ErrorState("Cannot parse", code, 0);
  }
};
