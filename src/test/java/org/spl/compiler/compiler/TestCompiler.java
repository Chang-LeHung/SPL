package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.SPLCompiler;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.tree.InsVisitor;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.interpreter.Evaluation;

import java.io.IOException;
import java.net.URL;

public class TestCompiler {

  public String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    assert resource != null;
    return resource.getPath();
  }

  @Test
  public void testCall() throws SPLSyntaxError, IOException, SPLInternalException {
    SPLCompiler compiler = new SPLCompiler(getResource("arithmetic/call.spl"));
    SPLCodeObject code = compiler.compile();
    DefaultEval defaultEval = new DefaultEval(code);
    ASTContext<Instruction> context = compiler.getContext();
    InsVisitor insVisitor = new InsVisitor(context.getConstantTable());
    context.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    defaultEval.evalFrame();
  }

  @Test
  public void testIf() throws SPLSyntaxError, IOException, SPLInternalException {
    SPLCompiler compiler = new SPLCompiler(getResource("controlflow/if.spl"));
    SPLCodeObject code = compiler.compile();
    ASTContext<Instruction> context = compiler.getContext();
    InsVisitor insVisitor = new InsVisitor(context.getConstantTable());
    context.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    System.out.println(context.getTopStackSize());
    System.out.println(code);
    Evaluation.init();
    DefaultEval defaultEval = new DefaultEval(code);
    defaultEval.evalFrame();
  }

  @Test
  public void testCondition() throws SPLSyntaxError, IOException, SPLInternalException {
    SPLCompiler compiler = new SPLCompiler(getResource("controlflow/condition.spl"));
    SPLCodeObject code = compiler.compile();
    ASTContext<Instruction> context = compiler.getContext();
    InsVisitor insVisitor = new InsVisitor(context.getConstantTable());
    context.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    System.out.println(context.getTopStackSize());
    System.out.println(code);
    DefaultEval defaultEval = new DefaultEval(code);
    defaultEval.evalFrame();
  }

  public void run(String filename) throws SPLSyntaxError, IOException, SPLInternalException {
    SPLCompiler compiler = new SPLCompiler(getResource(filename));
    SPLCodeObject code = compiler.compile();
    ASTContext<Instruction> context = compiler.getContext();
    InsVisitor insVisitor = new InsVisitor(context.getConstantTable());
    context.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    System.out.println(context.getTopStackSize());
    System.out.println(code);
    DefaultEval defaultEval = new DefaultEval(code);
    defaultEval.evalFrame();
  }

  @Test
  public void testMinus() throws SPLInternalException, SPLSyntaxError, IOException {
    run("arithmetic/minus.spl");
  }

  @Test
  public void testWhile() throws SPLInternalException, SPLSyntaxError, IOException {
    run("controlflow/while.spl");
  }

  @Test
  public void testDoWhile() throws SPLInternalException, SPLSyntaxError, IOException {
    run("controlflow/dowhile.spl");
  }
}
