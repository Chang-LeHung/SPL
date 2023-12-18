package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.interpreter.SPL;

import java.io.IOException;

public class ObjectSystemTest {

  @Test
  public void testTypeAttr() throws SPLInternalException, SPLSyntaxError, IOException {
    SPL spl = new SPL("objsys/type.spl");
    spl.dis();
    spl.run();
  }

  @Test
  public void testClass01() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("object/demo01.spl");
    spl.dis();
    spl.run();
  }

  @Test
  public void testInheritance() throws SPLInternalException, SPLSyntaxError, IOException {
    SPL spl = new SPL("object/demo02.spl");
    spl.dis();
    spl.run();
  }
}
