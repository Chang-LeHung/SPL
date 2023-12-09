package org.spl.vm.internal.shell;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.parser.SPLParser;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

import java.io.IOException;
import java.util.HashMap;

public class InteractiveShell {
  public static String filename = "anonymous";

  public static SPLCodeObject compile(String content) throws SPLSyntaxError, IOException {
    SPLParser parser = new SPLParser(filename, content);
    IRNode<Instruction> ir = parser.buildAST();
    DefaultASTContext<Instruction> context = parser.getContext();
    context.generateByteCodes(ir);
    return SPLCodeObjectBuilder.build(context);
  }

  public static void main(String[] args) throws IOException, SPLSyntaxError, SPLInternalException {
    HashMap<SPLObject, SPLObject> locals = new HashMap<>();
    Terminal terminal = TerminalBuilder.terminal();
    terminal.echo(false);
    LineReader reader = LineReaderBuilder.builder()
        .terminal(terminal)
        .build();
    StringBuilder builder = new StringBuilder();
    while (true) {
      try {
        builder.delete(0, builder.length());
        while (true) {
          String line = reader.readLine(">>>");
          builder.append(line);
          if (!line.endsWith("$"))
            break;
          else {
            builder.deleteCharAt(builder.length() - 1);
          }
        }
        String line = builder.toString();
        if (line.length() == 0)
          continue;
        else if (locals.containsKey(new SPLStringObject(line))) {
          System.out.println(locals.get(new SPLStringObject(line)).__str__());
        }
        SPLCodeObject code = compile(line);
        DefaultEval defaultEval = new DefaultEval(filename, locals, locals, code);
        SPLObject res = defaultEval.evalFrame();
        if (res != SPLNoneObject.getInstance())
          System.out.println(res.__str__());
      } catch (Exception e) {
        System.err.println("\033[31m" + e.getMessage() + "\033[0m");
      }
    }
  }
}
