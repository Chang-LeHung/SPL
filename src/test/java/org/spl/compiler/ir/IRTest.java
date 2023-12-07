package org.spl.compiler.ir;

import org.junit.jupiter.api.Test;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.parser.ArithmeticParser;
import org.spl.compiler.tree.InsVisitor;
import org.spl.compiler.exceptions.SPLSyntaxError;

import java.io.IOException;
import java.net.URL;

public class IRTest {

  public String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    assert resource != null;
    return resource.getPath();
  }

  @Test
  public void test01() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/test01.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    ir.accept(context);
    ir.codeGen(context);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x->x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
  }

  @Test
  public void testBool() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/bool.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    try {
      IRNode<Instruction> ir = arithmeticParser.buildAST();
    }catch (SPLSyntaxError error) {
      System.err.println(error.getMessage());
    }
  }

  @Test
  public void testASSign() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/assign.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    try {
      IRNode<Instruction> ir = arithmeticParser.buildAST();
    }catch (SPLSyntaxError error) {
      System.err.println(error.getMessage());
    }
  }

  @Test
  public void testAdd() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/add.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    ir.accept(context);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x->x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
    System.out.println(context.getTopStackSize());
  }

  @Test
  public void testCall() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/call.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    ir.accept(context);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x->x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
    System.out.println(ir);
    System.out.println(context.getTopStackSize());
  }

  @Test
  public void testAssign() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/assignop.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    System.out.println(arithmeticParser.getTokenFlow());
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    ir.accept(context);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x->x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
    System.out.println(ir);
    System.out.println(context.getTopStackSize());
  }

  @Test
  public void testComplex() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/complexdemo.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    System.out.println(arithmeticParser.getTokenFlow());
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    ir.accept(context);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x->x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
    System.out.println(ir);
    System.out.println(context.getTopStackSize());
  }
}
