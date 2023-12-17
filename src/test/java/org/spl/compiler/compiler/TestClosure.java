package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.interpreter.SPL;
import org.spl.vm.objects.SPLObject;

import java.io.IOException;
import java.util.Arrays;

public class TestClosure {

  @Test
  public void testClosure() throws SPLSyntaxError, IOException {
    System.out.println("test closure");
    SPL spl = new SPL("closure/demo01.spl");
    SPLObject[] constants = spl.getCode().getConstants();
    System.out.println(Arrays.toString(constants));
    if (constants[0] instanceof SPLFuncObject f) {
      f.dis();
      if (f.getCodeObject().getConstants()[0] instanceof SPLFuncObject g) {
        g.dis();
      }
    }
    spl.run();
  }
}
