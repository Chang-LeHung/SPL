package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.interpreter.SPL;

import java.io.IOException;

public class BuiltinTest {

  @Test
  public void testRange() throws SPLInternalException, SPLSyntaxError, IOException {
    SPL spl = new SPL("builtin/range.spl");
    spl.dis();
    spl.run();
  }
}
