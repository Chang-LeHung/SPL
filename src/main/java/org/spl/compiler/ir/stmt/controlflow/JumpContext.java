package org.spl.compiler.ir.stmt.controlflow;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.ir.context.ASTContext;
import org.spl.vm.objects.SPLObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JumpContext implements ASTContext<Instruction> {

  private final List<Ins> code;
  private final ASTContext<Instruction> context;
  private int nBytes;

  private boolean inTry;
  private ProgramBlock pb;

  public JumpContext(ASTContext<Instruction> context) {
    this.context = context;
    code = new ArrayList<>();
    nBytes = 0;
    TryStmt.tryStateCopy(context, this);
  }

  @Override
  public void increaseStackSize() {
    context.increaseStackSize();
  }

  @Override
  public void increaseStackSize(int n) {
    context.increaseStackSize(n);
  }

  @Override
  public void decreaseStackSize() {
    context.decreaseStackSize();
  }

  @Override
  public void decreaseStackSize(int n) {
    context.decreaseStackSize(n);
  }

  @Override
  public String getFileName() {
    return context.getFileName();
  }

  @Override
  public void addInstruction(Instruction instruction, int lineNo, int columnNo, int len) throws SPLSyntaxError {
    code.add(new Ins(instruction, lineNo, columnNo, len));
    OpCode opcode = instruction.getCode();
    if (opcode == OpCode.JUMP_ABSOLUTE || opcode == OpCode.LONG_JUMP) {
      nBytes += 4;
    } else {
      nBytes += 2;
      if (instruction.getOparg() >= 255)
        nBytes += 2;
    }
  }

  @Override
  public void add(Instruction instruction, int lineNo, int columnNo, int len) throws SPLSyntaxError {
    addInstruction(instruction, lineNo, columnNo, len);
  }

  @Override
  public int addVarName(Object o) {
    return context.addVarName(o);
  }

  @Override
  public int getVarNameIndex(Object o) {
    return context.getVarNameIndex(o);
  }

  @Override
  public boolean containSymbol(String name) {
    return context.containSymbol(name);
  }

  @Override
  public int getSymbolIndex(String name) {
    return context.getSymbolIndex(name);
  }

  @Override
  public Map<Object, Integer> getVarnames() {
    return context.getVarnames();
  }

  @Override
  public int getTopStackSize() {
    return context.getTopStackSize();
  }

  @Override
  public int getStackSize() {
    return context.getStackSize();
  }

  @Override
  public Instruction getInstruction(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visit(IRNode<Instruction> node) throws SPLSyntaxError {
    node.doVisit(this);
  }

  @Override
  public void addSymbol(String name) {
    context.addSymbol(name);
  }

  @Override
  public int getFirstLineNo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFirstLineNo(int firstLineNo) {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] getCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] getDebugInfo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] getLenColumn() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getNumberOfArgs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void generateByteCodes(IRNode<Instruction> node) throws SPLSyntaxError {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Instruction> getInstructions() {
    return null;
  }

  @Override
  public int getCodeSize() {
    return nBytes;
  }

  @Override
  public boolean isGlobal(String name) {
    return context.isGlobal(name);
  }

  @Override
  public SPLObject[] getConstants() {
    return context.getConstants();
  }

  @Override
  public Map<SPLObject, Integer> getConstantMap() {
    return context.getConstantMap();
  }

  @Override
  public void addConstantObject(SPLObject object) {
    context.addConstantObject(object);
  }

  @Override
  public int getConstantObjectIndex(SPLObject o) {
    return context.getConstantObjectIndex(o);
  }

  @Override
  public int getConstantsSize() {
    return context.getConstantsSize();
  }

  @Override
  public void addJumpTableEntry(JumpTableEntry entry) {

  }

  @Override
  public void enableTryBlock() {
    inTry = true;
  }

  @Override
  public void disableTryBlock() {
    inTry = false;
  }

  @Override
  public boolean isTryBlockEnabled() {
    return inTry;
  }

  @Override
  public ProgramBlock getFinallyBlock() {
    return pb;
  }

  @Override
  public void setFinallyBlock(ProgramBlock pb) {
    this.pb = pb;
  }

  @Override
  public List<JumpTableEntry> getJumpTable() {
    return null;
  }

  public int getNBytes() {
    return nBytes;
  }

  public List<Ins> getIns() {
    return code;
  }

  public static class Ins {
    Instruction ins;
    int lineNo;
    int columnNo;
    int len;

    Ins(Instruction ins, int lineNo, int columnNo, int len) {
      this.ins = ins;
      this.lineNo = lineNo;
      this.columnNo = columnNo;
      this.len = len;
    }
  }

}
