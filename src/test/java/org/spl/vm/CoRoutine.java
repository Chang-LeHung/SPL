package org.spl.vm;

import org.junit.jupiter.api.Test;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.interpreter.SPL;

import java.io.IOException;

public class CoRoutine {


  @Test
  public void testCo01() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("routines/coroutine01.spl");
    spl.run();
  }

  @Test
  public void testImport() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("routines/import.spl");
    spl.run();
  }
}
