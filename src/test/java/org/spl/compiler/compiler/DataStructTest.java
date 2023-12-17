package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.interpreter.SPL;

import java.io.IOException;

public class DataStructTest {

  @Test
  public void testDict() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("datastruct/dict02.spl");
    spl.dis();
    System.out.println(spl.getCode().getMaxStackSize());
    spl.run();
  }

  @Test
  public void testDict03() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("datastruct/dict03.spl");
    spl.dis();
    System.out.println(spl.getCode().getMaxStackSize());
    spl.run();
  }
}
