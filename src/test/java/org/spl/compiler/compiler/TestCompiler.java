package org.spl.compiler.compiler;

import org.junit.jupiter.api.Test;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.lexer.Lexer;
import org.spl.compiler.parser.SPLParser;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.utils.Dissembler;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.interpreter.Evaluation;
import org.spl.vm.interpreter.SPL;

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
    run("arithmetic/call.spl");
  }

  @Test
  public void testIf() throws SPLSyntaxError, IOException, SPLInternalException {
    run("controlflow/if.spl");
  }

  @Test
  public void testCondition() throws SPLSyntaxError, IOException, SPLInternalException {
    run("controlflow/condition.spl");
  }

  public DefaultEval run(String filename) throws SPLSyntaxError, IOException, SPLInternalException {
    filename = getResource(filename);
    SPL spl = new SPL(filename);
    spl.run();
    return null;
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
    run("function/basic.spl");
  }

  @Test
  public void testFunctionDefaultArg() throws SPLInternalException, SPLSyntaxError, IOException {
    run("function/defaultArg.spl");
  }

  @Test
  public void testFunctionDefaultArg02() throws SPLInternalException, SPLSyntaxError, IOException {
    run("function/defaultArg02.spl");
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
    run("function/fib.spl");
  }

  @Test
  public void testDefault03() throws SPLInternalException, SPLSyntaxError, IOException {
    run("function/default03.spl");
  }

  @Test
  public void testAnonymous01() throws SPLInternalException, SPLSyntaxError, IOException {
    run("function/anonymous01.spl");
  }

  @Test
  public void testAnonymous02() throws SPLInternalException, SPLSyntaxError, IOException {
    run("function/anonymous02.spl");
  }

  @Test
  public void testAttr01() throws SPLInternalException, SPLSyntaxError, IOException {
    Evaluation.init();
    SPLParser parser = new SPLParser(getResource("attr/demo01.spl"));
    IRNode<Instruction> ir = parser.buildAST();
    DefaultASTContext<Instruction> context = parser.getContext();
    context.generateByteCodes(ir);
    SPLCodeObject code = SPLCodeObjectBuilder.build(context);
    Dissembler dissembler = new Dissembler(code);
    dissembler.prettyPrint();
  }

  @Test
  public void testStoreAttr() throws SPLInternalException, SPLSyntaxError, IOException {
    run("attr/demo02.spl");
  }

  @Test
  public void testAssign() throws SPLInternalException, SPLSyntaxError, IOException {
    run("attr/demo03.spl");
  }

  @Test
  public void testTrueDiv() throws SPLInternalException, SPLSyntaxError, IOException {
    run("arithmetic/testTrueDiv.spl");
  }

  @Test
  public void testList() throws SPLInternalException, SPLSyntaxError, IOException {
    run("datastruct/list.spl");
  }

  @Test
  public void testSet() throws SPLInternalException, SPLSyntaxError, IOException {
    run("datastruct/set.spl");
  }

  @Test
  public void testDict() throws SPLInternalException, SPLSyntaxError, IOException {
    run("datastruct/dict.spl");
  }

  @Test
  public void testList02() throws SPLInternalException, SPLSyntaxError, IOException {
    run("datastruct/list02.spl");
  }

  @Test
  public void testList03() throws SPLInternalException, SPLSyntaxError, IOException {
    run("datastruct/list03.spl");
  }

  @Test
  public void testAttr() throws SPLInternalException, SPLSyntaxError, IOException {
    run("attr/load_store.spl");
  }

  @Test
  public void testSubscribe() throws SPLInternalException, SPLSyntaxError, IOException {
    run("attr/subs.spl");
  }

  @Test
  public void testDictSubscribe() throws SPLInternalException, SPLSyntaxError, IOException {
    run("attr/dict.spl");
  }

  @Test
  public void testConciseForCode() throws SPLInternalException, SPLSyntaxError, IOException {
    Evaluation.init();
    SPLParser parser = new SPLParser(getResource("controlflow/conciseFor.spl"));
    IRNode<Instruction> ir = parser.buildAST();
    DefaultASTContext<Instruction> context = parser.getContext();
    context.generateByteCodes(ir);
    SPLCodeObject code = SPLCodeObjectBuilder.build(context);
    Dissembler dissembler = new Dissembler(code);
    dissembler.prettyPrint();
    System.out.println(Arrays.toString(code.getConstants()));
    System.out.println(Arrays.toString(code.getVarnames()));
  }

  @Test
  public void testConciseFor() throws SPLInternalException, SPLSyntaxError, IOException {
    run("controlflow/conciseFor.spl");
  }

  @Test
  public void testIterSet() throws SPLInternalException, SPLSyntaxError, IOException {
    run("datastruct/iterset.spl");
  }

  @Test
  public void testIterDict() throws SPLInternalException, SPLSyntaxError, IOException {
    run("datastruct/iterdict.spl");
  }

  @Test
  public void testTry01Code() throws SPLInternalException, SPLSyntaxError, IOException {
    Evaluation.init();
    SPLParser parser = new SPLParser(getResource("try/try01.spl"));
    IRNode<Instruction> ir = parser.buildAST();
    DefaultASTContext<Instruction> context = parser.getContext();
    context.generateByteCodes(ir);
    SPLCodeObject code = SPLCodeObjectBuilder.build(context);
    Dissembler dissembler = new Dissembler(code);
    dissembler.prettyPrint();
    System.out.println(code);
  }

  @Test
  public void testTry01() throws SPLInternalException, SPLSyntaxError, IOException {
    run("try/try01.spl");
  }

  @Test
  public void testLambda() throws SPLInternalException, SPLSyntaxError, IOException {
    run("function/lambda.spl");
  }

  @Test
  public void testTryReturn() throws SPLInternalException, SPLSyntaxError, IOException {
    run("try/try02.spl");
  }

  @Test
  public void testTryReturn02() throws SPLInternalException, SPLSyntaxError, IOException {
    run("try/try03.spl");
  }

  @Test
  public void testTry04() throws SPLInternalException, SPLSyntaxError, IOException {
    run("try/try04.spl");
  }

  @Test
  public void testComment() throws SPLInternalException, SPLSyntaxError, IOException {
    run("comment/comment.spl");
  }

  @Test
  public void testDefaultArg() throws SPLInternalException, SPLSyntaxError, IOException {
    SPL spl = new SPL("function/defaultArg03.spl");
    spl.dis();
    spl.run();
  }
}

