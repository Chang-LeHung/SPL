package org.spl.vm;

import org.junit.jupiter.api.Test;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.DefaultASTContext;
import org.spl.compiler.parser.ArithmeticParser;
import org.spl.compiler.tree.InsVisitor;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.interpreter.DefaultEval;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.objects.SPLStringObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class CodeObjectTest {
  public String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    assert resource != null;
    return resource.getPath();
  }

  @Test
  public void testAdd() throws SPLSyntaxError, IOException {
    String resource = getResource("arithmetic/add.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    context.generateByteCodes(ir);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x -> x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
    SPLCodeObject build = SPLCodeObjectBuilder.build(context);
    System.out.println(build);
  }

  @Test
  public void testEval() throws SPLSyntaxError, IOException, SPLInternalException {
    String resource = getResource("arithmetic/print.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    context.generateByteCodes(ir);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x -> x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
    SPLCodeObject build = SPLCodeObjectBuilder.build(context);
    System.out.println(build);
    DefaultEval defaultEval = new DefaultEval(build);
    defaultEval.evalFrame();
  }

  @Test
  public void echoOpCode() {
    for (OpCode value : OpCode.values()) {
      System.out.println(value);
    }
  }

  @Test
  public void map() {
    HashMap<SPLObject, SPLObject> map = new HashMap<>();
    map.put(new SPLStringObject("key"), new SPLStringObject("value"));
    System.out.println(map.get(new SPLStringObject("key")));
    System.out.println(new SPLStringObject("key").hashCode());
    System.out.println(new SPLStringObject("key").hashCode());
  }

  @Test
  public void testComplete() throws SPLSyntaxError, IOException, SPLInternalException {
    String resource = getResource("arithmetic/complete.spl");
    ArithmeticParser arithmeticParser = new ArithmeticParser(resource);
    System.out.println(arithmeticParser.getTokenFlow());
    IRNode<Instruction> ir = arithmeticParser.buildAST();
    DefaultASTContext<Instruction> context = arithmeticParser.getContext();
    context.generateByteCodes(ir);
    InsVisitor insVisitor = new InsVisitor(context.getVarnames(), context.getConstantMap());
    context.getInstructions().forEach(x -> x.accept(insVisitor));
    System.out.println(insVisitor);
    System.out.println(context.getVarnames());
    SPLCodeObject build = SPLCodeObjectBuilder.build(context);
    System.out.println(build);
    DefaultEval defaultEval = new DefaultEval(build);
    System.out.println(context.getVarnames());
    defaultEval.evalFrame();
  }
}
