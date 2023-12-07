package org.spl.compiler.ir.stmt.func;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class FuncDef extends AbstractIR<Instruction> {

  private final String funcName;
  private final int idxInConstants;
  private final int idxInVarNames;

  public FuncDef(String funcName, int idxInConstants, int idxInVarNames) {
    this.funcName = funcName;
    this.idxInConstants = idxInConstants;
    this.idxInVarNames = idxInVarNames;
  }

  public String getFuncName() {
    return funcName;
  }

  public int getIdxInConstants() {
    return idxInConstants;
  }

  public int getIdxInVarNames() {
    return idxInVarNames;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.LOAD_CONST, idxInConstants), getLineNo(), getColumnNo(), getLen());
    context.addInstruction(new Instruction(OpCode.STORE_LOCAL, idxInVarNames), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
