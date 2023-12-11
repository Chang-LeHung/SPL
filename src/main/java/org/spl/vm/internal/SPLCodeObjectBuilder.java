package org.spl.vm.internal;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.vm.internal.objs.SPLCodeObject;

public class SPLCodeObjectBuilder {

  public static SPLCodeObject build(ASTContext<Instruction> context) {
    byte[] code = context.getCode();
    byte[] debugInfo = context.getDebugInfo();
    String fileName = context.getFileName();
    byte[] lenColumn = context.getLenColumn();
    int firstLineNo = context.getFirstLineNo();
    int args = context.getNumberOfArgs();
    return new SPLCodeObject(args,
        context.getTopStackSize(),
        fileName,
        firstLineNo,
        code,
        lenColumn,
        debugInfo,
        context.getJumpTable(),
        context.getVarnames(),
        context.getConstants());
  }
}
