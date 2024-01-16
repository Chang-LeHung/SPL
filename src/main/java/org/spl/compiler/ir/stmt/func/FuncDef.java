package org.spl.compiler.ir.stmt.func;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncDef extends AbstractIR<Instruction> {

  private final String funcName;
  private final int idxInConstants;
  private final int idxInVarNames;
  private final List<IRNode<Instruction>> defaults;
  private final List<IRNode<Instruction>> closures;
  private final IRNode<Instruction> body;
  private final List<String> args;
  private List<IRNode<Instruction>> children;

  public FuncDef(List<IRNode<Instruction>> closures,
                 IRNode<Instruction> body,
                 String funcName,
                 List<String> args,
                 int idxInConstants,
                 int idxInVarNames,
                 List<IRNode<Instruction>> defaults) {
    this.body = body;
    this.args = args;
    this.closures = closures;
    this.funcName = funcName;
    this.idxInConstants = idxInConstants;
    this.idxInVarNames = idxInVarNames;
    this.defaults = defaults;
  }

  /**
   * lambda expression
   * @param idxInConstants idx of function in constant table
   */
  public FuncDef(int idxInConstants, List<String> args, String name, IRNode<Instruction> body) {
    this.body = body;
    this.args = args;
    this.funcName = name;
    this.closures = List.of();
    this.idxInConstants = idxInConstants;
    this.idxInVarNames = -1;
    this.defaults = List.of();
    this.children = defaults;
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
    context.addInstruction(new Instruction(OpCode.MAKE_FUNCTION, defaults.size()), getLineNo(), getColumnNo(), getLen());
    if (idxInVarNames != -1)
      context.addInstruction(new Instruction(OpCode.STORE_LOCAL, idxInVarNames), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public void postVisiting(ASTContext<Instruction> context) {
    context.increaseStackSize();
    context.decreaseStackSize();
    context.decreaseStackSize(defaults.size());
    context.decreaseStackSize(closures.size());
    context.increaseStackSize();
    if (idxInVarNames != -1)
      context.decreaseStackSize();
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      Collections.reverse(defaults);
      Collections.reverse(closures);
      children = new ArrayList<>(defaults);
      children.addAll(closures);
    }
    return children;
  }

  public IRNode<Instruction> getBody() {
    return body;
  }

  public List<IRNode<Instruction>> getDefaults() {
    return defaults;
  }

  public List<IRNode<Instruction>> getClosures() {
    return closures;
  }

  public List<String> getArgs() {
    return args;
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
