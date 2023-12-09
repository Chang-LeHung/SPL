package org.spl.vm.interpreter;

import org.spl.compiler.SPLCompiler;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.shell.InteractiveShell;

import java.io.IOException;

public class SPLMain {

  public static void main(String[] args) throws SPLInternalException, SPLSyntaxError, IOException {
    if (args.length == 1) {
      try {
        SPLCompiler compiler = new SPLCompiler(args[0]);
        SPLCodeObject code = compiler.compile();
        DefaultEval defaultEval = new DefaultEval(code);
        defaultEval.evalFrame();
      } catch (SPLSyntaxError | SPLInternalException | IOException e) {
        // output red font words
        System.err.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        Runtime.getRuntime().exit(-1);
      } finally {
        Runtime.getRuntime().exit(0);
      }
    } else if (args.length == 0) {
      InteractiveShell.main(null);
    }
    System.err.println("Usage: SPLMain <file>");
  }
}
