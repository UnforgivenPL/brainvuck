package org.vaadin.miki.bv.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BrainVuck implements EntryPoint {

  private Interpreter interpreter;
  private boolean debugMode = false;
  private boolean codeChanged = true;
  
  private int getMemorySize() {
    try {
      return Integer.parseInt(Window.Location.getParameter("memorySize"));
    }
    catch(Exception ex) {
      return 500;
    }
  }
  
  private int getPreviewSize() {
    return 15;
  }
  
  private void paintPreviewInGrid(Grid grid, State state) {
    // paint memory
    int start = state.getMemoryPointer()<this.getPreviewSize()/2 ? 0 : state.getMemoryPointer()-this.getPreviewSize()/2;
    int end = start+this.getPreviewSize();
    for(int zmp1=start; zmp1<end; zmp1++) {
      if(zmp1 == state.getMemoryPointer()) {
        grid.setHTML(1, zmp1-start+1, "<span class=\"vfCurrentMemory\">"+String.valueOf(zmp1)+"</span>");
        grid.setHTML(2, zmp1-start+1, "<span class=\"vfCurrentMemory\">"+String.valueOf(state.getMemory()[zmp1])+"</span>");        
      }
      else if(zmp1<state.getMemory().length){
        grid.setText(1, zmp1-start+1, String.valueOf(zmp1));
        grid.setText(2, zmp1-start+1, String.valueOf(state.getMemory()[zmp1]));
      }
      else {
        grid.setText(1, zmp1-start+1, "-");
        grid.setText(2, zmp1-start+1, "-");        
      }
    }
    // paint source code
    start = state.getCodePosition()<this.getPreviewSize()/2 ? 0 : state.getCodePosition()-this.getPreviewSize()/2;
    end = start+this.getPreviewSize();
    for(int zmp1=start; zmp1<end; zmp1++)
      if(zmp1<state.getCode().length) grid.setHTML(0, zmp1-start+1, zmp1 == state.getCodePosition() ? "<span class=\"vfNextCode\">"+SafeHtmlUtils.htmlEscape(state.getCode()[zmp1])+"</span>" : SafeHtmlUtils.htmlEscape(state.getCode()[zmp1]));
      else grid.setText(0, zmp1-start+1, "");
  }
  
  private void setupInterpreter(final TextBox input, final Label output, final Label status, final TextArea code, final Grid memory, final Button execute) {
    Brainfuck bf = new Brainfuck();
    InputProvider ip = new InputProvider() {
          public char getNextInput() {
            if(input == null || input.getText()==null || input.getText().isEmpty()) return 0;
            char data = input.getText().charAt(0);
            input.setText(input.getText().substring(1));
            return data;
          }          
        };
    this.interpreter = new Interpreter(this.getMemorySize(), bf, bf, ip, new OutputConsumer() {
      public void consumeOutput(int c) {
        output.setText(output.getText()+(char)c);
      }
    }, bf );
    this.interpreter.addInterpreterListener(new InterpreterListener() {
      
      public void initialised() {
        status.setText("Initialised interpreter. Code length: "+interpreter.getState().getCode().length);
        if(debugMode) paintPreviewInGrid(memory, interpreter.getState()); 
      }
      
      public void error(Exception e) {        
        if(e instanceof State.ErrorState) {
          status.setText("Error! "+e.getMessage());
          code.setCursorPos(((State.ErrorState)e).getCodePosition()+1);
          code.setFocus(true);
        }
        else status.setText("Fatal error (not enough memory)! "+e.getMessage());
        execute.setEnabled(true);
      }
      
      public void done() {
        status.setText("Done execution in "+(interpreter.getSteps()>9000 ? "over 9000" : interpreter.getSteps())+" steps.");
        
        paintPreviewInGrid(memory, interpreter.getState());
        execute.setEnabled(true);
        // this will trigger code execution again on the next click, if the debug mode is on
        codeChanged = true;
      }
      
      public void commandExecuted(int command) {
        status.setText("Step "+interpreter.getSteps()+": executed command "+commandToString(command)+".");
        if(debugMode) paintPreviewInGrid(memory, interpreter.getState());
      }
    });
  }
  
  private String commandToString(int command) {
    switch(command) {
      case Syntax.NOOP: return "NOOP";
      case Brainfuck.BACK: return "STEP_BACK";
      case Brainfuck.FORWARD: return "STEP_FORWARD";
      case Brainfuck.DECREASE: return "MEM_DECREASE";
      case Brainfuck.INCREASE: return "MEM_INCREASE";
      case Brainfuck.INPUT: return "DATA_INPUT";
      case Brainfuck.OUTPUT: return "DATA_OUTPUT";
      case Brainfuck.START_IF: return "START_IF";
      case Brainfuck.END_IF: return "END_IF";
      default: return "unknown code "+command;
    }
  }
  
  private void resetGrid(Grid grid) {
    grid.clear();
    grid.setHTML(0, 0, "<strong>Source code</strong>");
    grid.setHTML(1, 0, "<strong>Cell number</strong>");
    grid.setHTML(2, 0, "<strong>Cell value</strong>");    
  }
  
  private void addCodeSnippet(TextArea code, String snippet) {
    code.setText(code.getText().substring(0, code.getCursorPos())+snippet+code.getText().substring(code.getCursorPos()));    
  }
  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    final Label status = new Label("");
    status.addStyleName("vfStatus");
    
    
    final TextArea code = new TextArea();
    code.setText("++++[>++                +++<\n-]>[-<+>                ]<[>\n+++++>++                +++>\n        ++++                +>++\n        +++>                ++++\n        +>++                +++>\n        >+++                    ++<<\n        <<<<                    <<-]\n        ++++                    +>--\n            --------                ----\n            >--->---                ><<<\n            <[>>>>>+                >++>\n        >+++                    ++<<\n        <<<<                    <<-]\n        ++++                    ++++\n        [>>>                >>>>\n        ++++                >>++\n        ++++                ++<<\n<<<<<<<-                ]>.>\n.>.>.>.>                .>.>\n.>--.FTW                Miki");
    //code.setText("++++[>+++++<-]>[-<+>]<[>+++++>+++++>+++++>+++++>+++++>+++++>>+++++<<<<<<<<-]+++++>-------------->--->---><<<<[>>>>>+>++>>+++++<<<<<<<<-]++++++++[>>>>>>>++++>>++++++++<<<<<<<<<-]>.>.>.>.>.>.>.>.>--.");
    //code.setText("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.");
    code.addStyleName("vfCode");
    code.addKeyPressHandler(new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        codeChanged = true;
      }
    });
    
    MenuBar snippets = new MenuBar();
    snippets.setStyleName("menu");
    String[] codes = new String[]{"Clear cell", "[-]", "Move/Add value to next", "[->+<]", "Move/Add value from next", ">[-<+>]<", "Copy value to next", "[->+>+<<]>>[-<<+>>]<<", "Go to next empty cell", "[>]", "Read all input", ",[>,]", "Write to output", ".[>.]"};
    for(int zmp1=0; zmp1<codes.length; zmp1+=2) {
      final String snip = codes[zmp1+1];
      snippets.addItem(codes[zmp1], new Scheduler.ScheduledCommand() {
        public void execute() {addCodeSnippet(code, snip);}      
      });
    }

    final Label output = new Label();
    output.addStyleName("vfOutput");

    Label outputCaption = new Label("Output: ");
    outputCaption.addStyleName("vfLabel");
    outputCaption.setTitle("Click this label to flush the output.");
    outputCaption.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        output.setText("");
      }
    });
    
    Label memoryCaption = new Label("Memory preview:");
    memoryCaption.addStyleName("vfLabel");

    final Grid memory = new Grid(3, this.getPreviewSize()+1);
    memory.addStyleName("vfMemory");
    this.resetGrid(memory);
    
    final Button execute = new Button("Execute!");
    execute.addStyleName("vfExecute");
    execute.addStyleName("vfButton");
    execute.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        // clear output and memory preview if clicking outside debug mode or in debug mode, but with changed code
        if(!debugMode || codeChanged) {
          output.setText("");
          resetGrid(memory);
        }
        // if not debug mode, run
        if(!debugMode) {
          status.setText("Now executing. If you wrote bad code, it will freeze your browser.");
          execute.setEnabled(false);
          Scheduler.get().scheduleEntry(new Scheduler.ScheduledCommand() {            
            public void execute() {
              interpreter.run(code.getText().toCharArray());
            }
          });          
        }
          // if code changed, set it up
        else if(codeChanged) {
          if(interpreter.setCode(code.getText().toCharArray())) {
            codeChanged = false;
          }
        }
        // if code not changed
        else interpreter.step();        
      }
    });
    
    final Label inputCaption = new Label("Input: ");
    inputCaption.addStyleName("vfLabel");
    
    final TextBox input = new TextBox();
    input.addStyleName("vfInput");

    Button clear = new Button("Clear");
    clear.addStyleName("vfClear");
    clear.addStyleName("vfButton");
    clear.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        code.setText("");
        codeChanged = true;
        output.setText("");
        resetGrid(memory);
      }
    });

    final CheckBox memprev = new CheckBox("Show memory preview");
    memprev.addStyleName("vfDebug");
    memprev.addClickHandler(new ClickHandler() {      
      public void onClick(ClickEvent event) {
        RootPanel.get("memoryContainer").setVisible(memprev.getValue());
      }
    });
    
    final CheckBox debug = new CheckBox("Debug mode (step-by-step execution)");
    debug.addStyleName("vfDebug");
    debug.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        debugMode = debug.getValue();
        memprev.setValue(debugMode);
        memprev.fireEvent(event);
      }
    });
    
        
    // Add the nameField and sendButton to the RootPanel
    // Use RootPanel.get() to get the entire body element
    RootPanel.get("codeContainer").add(snippets);
    RootPanel.get("codeContainer").add(code);    
    RootPanel.get("executeContainer").add(clear);
    RootPanel.get("executeContainer").add(execute);
    RootPanel.get("debugContainer").add(debug);
    RootPanel.get("debugContainer").add(memprev);
    RootPanel.get("debugContainer").add(status);
    RootPanel.get("inputContainer").add(inputCaption);
    RootPanel.get("inputContainer").add(input);
    RootPanel.get("outputContainer").add(outputCaption);
    RootPanel.get("outputContainer").add(output);
    RootPanel.get("memoryContainer").setVisible(false);
    RootPanel.get("memoryContainer").add(memoryCaption);
    RootPanel.get("memoryContainer").add(memory);

    // Focus the cursor on the name field when the app loads
    code.setFocus(true);

    this.setupInterpreter(input, output, status, code, memory, execute);
    
  }
}
