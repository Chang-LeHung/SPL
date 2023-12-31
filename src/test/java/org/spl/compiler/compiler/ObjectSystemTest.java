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
  public void testInheritance() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("object/demo02.spl");
    spl.dis();
    spl.run();
  }

  @Test
  public void testStatic() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("object/static.spl");
    spl.dis();
    spl.run();
  }

  @Test
  public void testStaticAttr() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("object/classAttr.spl");
    spl.dis();
    spl.run();
  }

  @Test
  public void testMultiInheritance() throws IOException {
    try {
      SPL spl = new SPL("object/single.spl");
      spl.dis();
      spl.run();
    } catch (SPLSyntaxError error) {
      System.err.println(error.getMessage());
    }
  }

  @Test
  public void testMagicFunction() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("object/add.spl");
    spl.dis();
    spl.run();
  }

  @Test
  public void testDisAddIns() throws SPLSyntaxError, IOException {
    SPL spl = new SPL("object/insadd.spl");
    spl.dis();
    spl.run();
  }
}
