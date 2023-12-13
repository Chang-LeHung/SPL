package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.interpreter.SPL;
import org.spl.vm.objects.SPLObject;

import java.io.IOException;

public class TestErrorTrace {

  @Test
  public void test01() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("error/error01.spl");
    spl.dis();
    SPLObject run = spl.run();
    assert run == null;
  }

  @Test
  public void testAdd() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("error/adderror.spl");
    spl.dis();
    SPLObject run = spl.run();
    assert run == null;
  }

  @Test
  public void testCall() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("error/call.spl");
    SPLObject run = spl.run();
    assert run == null;
  }

  @Test
  public void testStackOverflow() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("error/stack.spl");
    SPLObject run = spl.run();
    assert run == null;
  }
}
