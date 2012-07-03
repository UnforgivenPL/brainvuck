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
public interface State {

  public static interface Initialiser {
    public State getInitialState(char[] code, int memorySize, InputProvider provider, OutputConsumer consumer) throws ErrorState;
  }

  public static class ErrorState extends Exception implements State {
    private static final long serialVersionUID = 20120630L;
    private final char[] code;
    private final int position;
    private final String message;
    public ErrorState(String message, char[] code, int position) {
      super(message); this.code = code; this.position = position; this.message = message;
    }
    public String getMessage() {return this.message;}
    public char[] getCode() {return this.code;}
    public int getCodePosition() {return this.position;}
    public int getMemoryPointer() {return -1;}
    public int[] getMemory() {return null;}
    public InputProvider getInputProvider() {return null;}
    public OutputConsumer getOutputConsumer() {return null;}
    public State derivate(int codePosition, char[] code, int memoryPointer,
                          int[] memory, InputProvider provider,
                          OutputConsumer consumer) {
      return this;
    }
  }

  public static final State INVALID = new State(){
    public char[] getCode() {return null;}
    public int getCodePosition() {return -1;}
    public int getMemoryPointer() {return -1;}
    public int[] getMemory() {return null;}
    public InputProvider getInputProvider() {return null;}
    public OutputConsumer getOutputConsumer() {return null;}
    public State derivate(int codePosition, char[] code, int memoryPointer,
                          int[] memory, InputProvider provider,
                          OutputConsumer consumer) {
      return this;
    }
  };
  public static final State END = new State() {
    public char[] getCode() {return new char[0];}
    public int getCodePosition() {return 0;}
    public int getMemoryPointer() {return 0;}
    public int[] getMemory() {return new int[0];}
    public InputProvider getInputProvider() {return null;}
    public OutputConsumer getOutputConsumer() {return null;}

    public State derivate(int codePosition, char[] code, int memoryPointer,
                          int[] memory, InputProvider provider,
                          OutputConsumer consumer) {
      return this;
    }
  };

  public char[] getCode();

  public int getCodePosition();

  public int getMemoryPointer();

  public int[] getMemory();

  public InputProvider getInputProvider();

  public OutputConsumer getOutputConsumer();

  public State derivate(int codePosition, char[] code, int memoryPointer, int[] memory, InputProvider provider, OutputConsumer consumer);

}
