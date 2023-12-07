package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class ForStmt extends AbstractIR<Instruction> {

  private final IRNode<Instruction> initializer;
  private final IRNode<Instruction> condition;
  private final IRNode<Instruction> increment;
  private final IRNode<Instruction> body;

  public ForStmt(IRNode<Instruction> initializer, IRNode<Instruction> condition, IRNode<Instruction> increment, IRNode<Instruction> body) {
    this.initializer = initializer;
    this.condition = condition;
    this.increment = increment;
    this.body = body;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    initializer.accept(context);
    int start = context.getCodeSize();

    JumpContext auxContex = new JumpContext(context);
    body.accept(auxContex);
    int bdSize = auxContex.getCodeSize();

    auxContex = new JumpContext(context);
    increment.accept(auxContex);
    int incrementSize = auxContex.getCodeSize();

    auxContex = new JumpContext(context);
    condition.accept(auxContex);
    int conditionSize = auxContex.getCodeSize();

    int diff = 2;
    int falseTarget = bdSize + incrementSize + diff;
    if (falseTarget >= 255) {
      diff += 2;
    }
    BreakVisitor breakVisitor = new BreakVisitor(context,
        start + conditionSize + diff + bdSize + incrementSize + 4);
    ContinueVisitor continueVisitor = new ContinueVisitor(context,
        start + conditionSize + diff + bdSize);
    body.accept(breakVisitor);
    body.accept(continueVisitor);
    condition.accept(context);
    context.addInstruction(new Instruction(OpCode.JUMP_FALSE, falseTarget), condition.getLineNo(), condition.getColumnNo(), condition.getLen());
    body.accept(context);
    increment.accept(context);
    context.addInstruction(new Instruction(OpCode.JUMP_ABSOLUTE, start), getLineNo(), getColumnNo(), getLen());
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
