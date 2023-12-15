package org.spl.vm.interpreter;

import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.shell.InteractiveShell;
import org.spl.vm.objects.SPLObject;

import java.io.IOException;

public class SPLMain {

  public static void main(String[] args) throws SPLInternalException, SPLSyntaxError, IOException {
    if (args.length == 1) {
      try {
        SPL spl = new SPL(args[0]);
        SPLObject run = spl.run();
        if (run == null) {
          Runtime.getRuntime().exit(-1);
        }
      } catch (SPLSyntaxError | IOException e) {
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
