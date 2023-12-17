package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.interpreter.SPL;

import java.io.IOException;

public class TestDecorator {

  @Test
  public void testDecorator01() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("decorator/demo01.spl");
    spl.dis();
    spl.run();
  }
}
