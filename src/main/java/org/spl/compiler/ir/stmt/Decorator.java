package org.spl.compiler.ir.stmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.compiler.ir.stmt.func.FuncDef;

import java.util.List;

public class Decorator extends AbstractIR<Instruction> {

  private final FuncDef funcDef;
  private final IRNode<Instruction> expr;
  private List<IRNode<Instruction>> children;


  public Decorator(FuncDef funcDef, IRNode<Instruction> expr) {
    this.funcDef = funcDef;
    this.expr = expr;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    int idxInVarNames = funcDef.getIdxInVarNames();
    context.addInstruction(new Instruction(OpCode.LOAD_LOCAL, idxInVarNames), funcDef.getLineNo(), funcDef.getColumnNo(), funcDef.getLen());
    context.increaseStackSize();
    expr.accept(context);
    context.addInstruction(new Instruction(OpCode.CALL, 1), getLineNo(), getColumnNo(), getLen());
    context.decreaseStackSize(2);
    context.increaseStackSize();
    context.addInstruction(new Instruction(OpCode.STORE_LOCAL, idxInVarNames), funcDef.getLineNo(), funcDef.getColumnNo(), funcDef.getLen());
    context.decreaseStackSize();
  }

  @Override
  public boolean isStatement() {
    return true;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(funcDef);
    }
    return children;
  }
}
