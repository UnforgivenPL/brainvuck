package org.vaadin.miki.bv.client;

import java.util.Arrays;

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
public class MachineState implements State {

  private final char[] code;
  private final int position;
  private final int[] memory;
  private final int pointer;

  private final InputProvider provider;
  private final OutputConsumer consumer;

  public MachineState(char[] code, int codePosition, int[] memory, int memoryPointer, InputProvider input, OutputConsumer output) {
    super();
    this.provider = input;
    this.consumer = output;
    this.code = code;
    this.position = codePosition;
    this.memory = memory;
    this.pointer = memoryPointer;
  }

  public char[] getCode() {
    char[] copy = new char[this.code.length];
    for(int zmp1 = 0; zmp1<this.code.length; zmp1++) copy[zmp1] = this.code[zmp1];
    return copy;
    // Arrays.copyOf is not supported in GWT
//    return Arrays.copyOf(this.code, this.code.length);
  }

  public int getCodePosition() {
    return this.position;
  }

  public int getMemoryPointer() {
    return this.pointer;
  }

  public int[] getMemory() {
    int[] copy = new int[this.memory.length];
    for(int zmp1 = 0; zmp1<this.memory.length; zmp1++) copy[zmp1] = this.memory[zmp1];
    return copy;
    // Arrays.copyOf is not supported in GWT
//    return Arrays.copyOf(this.memory, this.memory.length);
  }

  public boolean equals(Object o) {
    if(o instanceof MachineState) {
      MachineState s = (MachineState)o;
      return s.pointer == this.pointer && s.position == this.position &&
             Arrays.equals(s.code, this.code) && Arrays.equals(s.memory, this.memory);
    }
    else return super.equals(o);
  }

  public InputProvider getInputProvider() {
    return this.provider;
  }

  public OutputConsumer getOutputConsumer() {
    return this.consumer;
  }

  public State derivate(int codePosition, char[] code, int memoryPointer,
                        int[] memory, InputProvider provider,
                        OutputConsumer consumer) {
    return new MachineState(code, codePosition, memory, memoryPointer, provider, consumer);
  }

}
