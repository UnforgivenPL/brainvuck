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
public class Parser {

  private final Syntax syntax;

  public Parser(Syntax syntax) {
    this.syntax = syntax;
  }

  public Syntax getSyntax() {
    return this.syntax;
  }

  public boolean parse(char[] code) throws Parser.ParsingException {
    for(int zmp1=0; zmp1<code.length; zmp1++)
      if(this.syntax.opensSection(code[zmp1]) && this.locateLoopEnd(code, zmp1) == -1) throw new Parser.MalformedLoopException(code, zmp1);
      else if(this.syntax.closesSection(code[zmp1]) && this.locateLoopStart(code, zmp1) == -1) throw new Parser.MalformedLoopException(code, zmp1);
    return true;
  }

  public char[] strip(char[] code) {
    StringBuilder builder = new StringBuilder();
    for(char c: code)
      if(this.syntax.commandFor(c) != Syntax.NOOP)
        builder.append(c);
    return builder.toString().toCharArray();
  }

  public int locateLoopEnd(char[] code, int where) {
    char closing = this.syntax.matching(code[where]);
    char opening = code[where];
    if(closing == opening) return -1;
    int subloops = 0;
    while(where<code.length) {
      if(code[where]==opening) subloops++;
      else if(code[where]==closing) subloops--;
      if(subloops == 0) return where;
      where++;
    }
    return -1;
  }

  public int locateLoopStart(char[] code, int where) {
    char closing = code[where];
    char opening = this.syntax.matching(code[where]);
    if(closing == opening) return -1;
    int subloops = 0;
    while(where>=0) {
      // opens section of the same type
      if(code[where]==opening) subloops++;
      else if(code[where]==closing) subloops--;
      if(subloops == 0) return where;
      where--;
    }
    return -1;
  }

  public abstract static class ParsingException extends Exception {
    private static final long serialVersionUID = 20120630L;
    private final char[] code;
    private final int position;
    public ParsingException(char[] code, int position) {
      super();
      this.code = code;
      this.position = position;
    }
    public char[] getCode() {return this.code;}
    public int getPosition() {return this.position;}
    public String getMessage() {return "Syntax error at position "+this.position;}
  }

  public static class NestedLoopException extends ParsingException {
    private static final long serialVersionUID = 20120630L;
    public NestedLoopException(char[] code, int position) {super(code, position);}
  }

  public static class MalformedLoopException extends ParsingException {
    private static final long serialVersionUID = 20120630L;
    public MalformedLoopException(char[] code, int position) {super(code, position);}
  }

}
