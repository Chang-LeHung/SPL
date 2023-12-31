package org.spl.compiler;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.parser.SPLParser;
import org.spl.vm.internal.SPLCodeObjectBuilder;
import org.spl.vm.internal.objs.SPLCodeObject;

import java.io.IOException;
import java.util.List;

public class SPLCompiler {

  private final String filename;
  private ASTContext<Instruction> context;
  private IRNode<?> ir;
  private SPLParser parser;

  public SPLCompiler(String filename) {
    this.filename = filename;
  }

  public SPLCodeObject compile() throws SPLSyntaxError, IOException {
    parser = new SPLParser(filename);
    IRNode<Instruction> ir = parser.buildAST();
    this.ir = ir;
    parser.getContext().generateByteCodes(ir);
    context = parser.getContext();
    return SPLCodeObjectBuilder.build(parser.getContext());
  }

  public ASTContext<Instruction> getContext() {
    return context;
  }

  public List<String> getSourceCode() {
    return parser.getSourceCode();
  }

  public IRNode<?> getIr() {
    return ir;
  }
}
