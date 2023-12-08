package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.SPLCompiler;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.lexer.Lexer;
import org.spl.compiler.tree.InsVisitor;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.internal.utils.Dissembler;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.interpreter.Evaluation;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

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
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    defaultEval.evalFrame();
  }

  @Test
  public void testIf() throws SPLSyntaxError, IOException, SPLInternalException {
    SPLCompiler compiler = new SPLCompiler(getResource("controlflow/if.spl"));
    SPLCodeObject code = compiler.compile();
    ASTContext<Instruction> context = compiler.getContext();
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
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
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    System.out.println(context.getTopStackSize());
    System.out.println(code);
    DefaultEval defaultEval = new DefaultEval(code);
    defaultEval.evalFrame();
  }

  public DefaultEval run(String filename) throws SPLSyntaxError, IOException, SPLInternalException {
    SPLCompiler compiler = new SPLCompiler(getResource(filename));
    SPLCodeObject code = compiler.compile();
    ASTContext<Instruction> context = compiler.getContext();
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(insVisitor::visit);
    System.out.println(insVisitor);
    System.out.println(context.getTopStackSize());
    System.out.println(code);
    DefaultEval defaultEval = new DefaultEval(code);
    defaultEval.evalFrame();
    return defaultEval;
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

  @Test
  public void testWhileBreakContinue() throws SPLInternalException, SPLSyntaxError, IOException {
    run("controlflow/break.spl");
  }

  @Test
  public void testDoWhileBreakContinue() throws SPLInternalException, SPLSyntaxError, IOException {
    run("controlflow/dowhilebreak.spl");
  }

  @Test
  public void testForBreakContinue() throws SPLInternalException, SPLSyntaxError, IOException {
    run("controlflow/for.spl");
  }

  @Test
  public void testFunctionDef() throws SPLInternalException, SPLSyntaxError, IOException {
    DefaultEval eval = run("function/basic.spl");
    System.out.println(Arrays.toString(eval.getConstants()));
    Dissembler dissembler = new Dissembler(((SPLFuncObject) eval.getConstants()[1]));
    dissembler.prettyPrint();
  }

  @Test
  public void testFunctionDefaultArg() throws SPLInternalException, SPLSyntaxError, IOException {
    DefaultEval eval = run("function/defaultArg.spl");
    System.out.println(Arrays.toString(eval.getConstants()));
    Dissembler dissembler = new Dissembler(((SPLFuncObject) eval.getConstants()[2]));
    dissembler.prettyPrint();
  }

  @Test
  public void testFunctionDefaultArg02() throws SPLInternalException, SPLSyntaxError, IOException {
    try {
      DefaultEval eval = run("function/defaultArg02.spl");
    }catch (SPLSyntaxError e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  @Test
  public void testTokens() throws IOException, SPLSyntaxError {
    String resource = getResource("function/defaultArg02.spl");
    Lexer lexer = new Lexer(resource);
    lexer.doParse();
    System.out.println(lexer.getTokens());
  }

  @Test
  public void testFunctionReturn() throws SPLInternalException, SPLSyntaxError, IOException {
    DefaultEval eval = run("function/fib.spl");
    System.out.println(Arrays.toString(eval.getConstants()));
    Dissembler dissembler = new Dissembler(((SPLFuncObject) eval.getConstants()[0]));
    dissembler.prettyPrint();
  }

  @Test
  public void testDefault03() throws SPLInternalException, SPLSyntaxError, IOException {
    DefaultEval eval = run("function/default03.spl");
    System.out.println(Arrays.toString(eval.getConstants()));
    Dissembler dissembler = new Dissembler(((SPLFuncObject) eval.getConstants()[2]));
    dissembler.prettyPrint();
  }

  @Test
  public void testAnonymous01() throws SPLInternalException, SPLSyntaxError, IOException {
    DefaultEval eval = run("function/anonymous01.spl");
    System.out.println(Arrays.toString(eval.getConstants()));
    Dissembler dissembler = new Dissembler(((SPLFuncObject) eval.getConstants()[0]));
    dissembler.prettyPrint();
  }
}
