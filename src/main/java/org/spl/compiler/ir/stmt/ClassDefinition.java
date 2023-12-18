package org.spl.compiler.ir.stmt;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.AbstractIR;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.context.ASTContext;

import java.util.List;

public class ClassDefinition extends AbstractIR<Instruction> {

  private final int classDefIdx;
  private final int classNameIdx;
  private final int superClassNameIdx;

  public ClassDefinition(int classDefIdx, int classNameIdx, int superClassNameIdx) {
    this.classDefIdx = classDefIdx;
    this.classNameIdx = classNameIdx;
    this.superClassNameIdx = superClassNameIdx;
  }

  @Override
  public void codeGen(ASTContext<Instruction> context) throws SPLSyntaxError {
    context.addInstruction(new Instruction(OpCode.LOAD_CONST, classDefIdx), getLineNo(), getColumnNo(), getLen());
    context.increaseStackSize();
    if (superClassNameIdx != -1) {
      context.addInstruction(new Instruction(OpCode.LOAD_NAME, superClassNameIdx), getLineNo(), getColumnNo(), getLen());
    } else {
      context.addVarName("object");
      int idx = context.getVarNameIndex("object");
      context.addInstruction(new Instruction(OpCode.LOAD_NAME, idx), getLineNo(), getColumnNo(), getLen());
    }
    context.increaseStackSize();
    context.addInstruction(new Instruction(OpCode.BUILD_CLASS), getLineNo(), getColumnNo(), getLen());
    context.decreaseStackSize(2);
    context.increaseStackSize();
    context.addInstruction(new Instruction(OpCode.STORE_LOCAL, classNameIdx), getLineNo(), getColumnNo(), getLen());
    context.decreaseStackSize();
  }

  @Override
  public boolean isStatement() {
    return true;
  }

  @Override
  public List<IRNode<Instruction>> getChildren() {
    return List.of();
  }
}
