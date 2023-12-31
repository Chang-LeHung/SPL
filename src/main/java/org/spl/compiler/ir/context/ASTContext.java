package org.spl.compiler.ir.context;

import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.vm.objects.SPLObject;

import java.util.List;
import java.util.Map;

public interface ASTContext<T> {

  void increaseStackSize();

  void increaseStackSize(int n);

  void decreaseStackSize();

  void decreaseStackSize(int n);

  String getFileName();

  void addInstruction(T instruction, int lineNo, int columnNo, int len) throws SPLSyntaxError;

  void add(T instruction, int lineNo, int columnNo, int len) throws SPLSyntaxError;

  int addVarName(Object o);

  int getVarNameIndex(Object o);

  boolean containSymbol(String name);

  int getSymbolIndex(String name);

  Map<Object, Integer> getVarnames();

  int getTopStackSize();

  int getStackSize();

  T getInstruction(int index);

  void visit(IRNode<T> node) throws SPLSyntaxError;

  void addSymbol(String name);

  int getFirstLineNo();

  void setFirstLineNo(int firstLineNo);

  byte[] getCode();

  byte[] getDebugInfo();

  byte[] getLenColumn();

  int getNumberOfArgs();

  void generateByteCodes(IRNode<T> node) throws SPLSyntaxError;

  List<T> getInstructions();

  int getCodeSize();

  boolean isGlobal(String name);

  SPLObject[] getConstants();

  Map<SPLObject, Integer> getConstantMap();

  void addConstantObject(SPLObject o);

  int getConstantObjectIndex(SPLObject o);

  int getConstantsSize();

  void addJumpTableEntry(JumpTableEntry entry);

  void enableTryBlock();

  void disableTryBlock();

  boolean isTryBlockEnabled();

  ProgramBlock getFinallyBlock();

  void setFinallyBlock(ProgramBlock pb);

  List<JumpTableEntry> getJumpTable();

  default List<String> getSourceCode() {
    return null;
  }

  String getCoName();

  void setCoName(String coName);

  boolean isInFunction();

  ASTContext<T> getPreviousContext();

  boolean requestClosure(String var);

  Map<String, Integer> getClosureMap();

  record JumpTableEntry(int startPc, int endPc, int targetPc) {

    @Override
    public String toString() {
      return "JumpTableEntry{" +
          "startPC=" + startPc +
          ", endPC=" + endPc +
          ", targetPC=" + targetPc +
          '}';
    }

    public boolean isInRange(int pc) {
      return pc >= startPc && pc <= endPc;
    }
  }
}