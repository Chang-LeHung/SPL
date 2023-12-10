package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class ConciseForStmt extends AbstractIR<Instruction> {

  private final IRNode<Instruction> expression;
  private final String name;
  private final int idx;

  private final IRNode<Instruction> block;
  private List<IRNode<Instruction>> children;

  public ConciseForStmt(IRNode<Instruction> expression, String name, int idx, IRNode<Instruction> block) {
    this.expression = expression;
    this.name = name;
    this.idx = idx;
    this.block = block;
  }

  public IRNode<Instruction> getExpression() {
    return expression;
  }

  public String getName() {
    return name;
  }

  public int getIdx() {
    return idx;
  }

  public IRNode<Instruction> getBlock() {
    return block;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.GET_ITERATOR, 0), getLineNo(), getColumnNo(), getLen());
    int currentSize = context.getCodeSize();
    int size = 2 + 4; // STORE_LOCAL + ABSOLUTE_JUMP
    if (idx >= 255)
      size += 2;
    JumpContext auxContext = new JumpContext(context);
    block.accept(auxContext);
    int blockSize = auxContext.getCodeSize();
    size += blockSize;
    // absolute jump pos will occupy 3 bytes
    context.addInstruction(new Instruction(OpCode.NEXT, size), getLineNo(), getColumnNo(), getLen());
    context.addInstruction(new Instruction(OpCode.STORE_LOCAL, idx), getLineNo(), getColumnNo(), getLen());
    block.accept(context);
    context.addInstruction(new Instruction(OpCode.JUMP_ABSOLUTE, currentSize), getLineNo(), getColumnNo(), getLen());
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    if (children == null) {
      children = List.of(expression);
    }
    return children;
  }

  @Override
  public boolean isStatement() {
    return true;
  }
}
