package org.spl.compiler.ir.context;

import org.spl.compiler.bytecode.ByteCode;
import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.NameSpace;
import org.spl.compiler.tree.Visitor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultASTContext<E extends Instruction> implements Visitor<E>, ASTContext<E> {

  private String filename;
  private final List<E> instructions;
  private final Map<Object, Integer> constantTable;
  private int firstLineNo;
  private final ByteArrayOutputStream code;
  private final ByteArrayOutputStream debugInfo;
  private final ByteArrayOutputStream lenColumn;
  private int insOfLine;
  private int currentLineNo;
  private int lastLineNo;

  private final NameSpace<String> nameSpace;
  private int stackSize;
  private int topStackSize;
  private int args;

  public DefaultASTContext(String filename) {
    this.filename = filename;
    stackSize = 0;
    topStackSize = 0;
    instructions = new ArrayList<>();
    constantTable = new HashMap<>();
    nameSpace = new NameSpace<>();
    firstLineNo = -1;
    insOfLine = 0;
    code = new ByteArrayOutputStream();
    debugInfo = new ByteArrayOutputStream();
    currentLineNo = 0;
    lastLineNo = 0;
    lenColumn = new ByteArrayOutputStream();
  }


  @Override
  public int getTopStackSize() {
    return topStackSize;
  }

  @Override
  public List<E> getInstructions() {
    return instructions;
  }

  @Override
  public int getCodeSize() {
    return code.size();
  }

  @Override
  public void addInstruction(E instruction, int lineNo, int columnNo, int len) throws SPLSyntaxError {
    instructions.add(instruction);
    if (firstLineNo == -1) {
      firstLineNo = lineNo;
      currentLineNo = lineNo;
      lastLineNo = lineNo;
    }
    if (currentLineNo != lineNo) {
      write(insOfLine, debugInfo);
      int rest = currentLineNo - lastLineNo;
      write(rest, debugInfo);
      lastLineNo = currentLineNo;
      currentLineNo = lineNo;
      insOfLine = 1;
    } else {
      insOfLine++;
    }
    // write instruction info
    write(instruction.getOpCode(), code);
    if (instruction.getCode() == OpCode.JUMP_ABSOLUTE) {
      int arg = instruction.getOparg();
      code.write(arg >> 16);
      code.write(arg >> 8);
      code.write(arg);
    } else {
      write(instruction.getOparg(), code);
    }
    // write debug info
    write(len, lenColumn);
    write(columnNo, lenColumn);
  }

  public void write(int v, ByteArrayOutputStream out) throws SPLSyntaxError {
    if (v > 254) {
      if (v > 0xffff) {
        throw new SPLSyntaxError("Too constants in file " + filename + "or" + "too many arguments in a function");
      }
      out.write(0xff);
      // BIG ENDIAN two bytes are enough
      out.write((v >> 8) & 0xff);
      out.write((v) & 0xff);
    } else {
      out.write(v);
    }
  }

  @Override
  public void add(E instruction, int lineNo, int columnNo, int len) throws SPLSyntaxError {
    addInstruction(instruction, lineNo, columnNo, len);
  }

  @Override
  public E getInstruction(int index) {
    return instructions.get(index);
  }

  @Override
  public int getConstantIndex(Object o) {
    if (constantTable.containsKey(o)) {
      return constantTable.get(o);
    }
    throw new RuntimeException("Constant not found");
  }

  @Override
  public int addConstant(Object o) {
    if (constantTable.containsKey(o))
      return constantTable.get(o);
    constantTable.put(o, constantTable.size());
    return constantTable.size() - 1;
  }

  @Override
  public void addSymbol(String name) {
    nameSpace.addSymbol(name);
  }

  @Override
  public boolean containSymbol(String name) {
    return nameSpace.contain(name);
  }

  @Override
  public int getSymbolIndex(String name) {
    return constantTable.get(name);
  }

  public Map<Object, Integer> getConstantTable() {
    return constantTable;
  }

  @Override
  public void increaseStackSize() {
    increaseStackSize(1);
  }

  @Override
  public void increaseStackSize(int size) {
    stackSize += size;
    if (stackSize > topStackSize)
      topStackSize = stackSize;
  }

  @Override
  public void decreaseStackSize() {
    stackSize--;
  }

  @Override
  public int getStackSize() {
    return stackSize;
  }

  @Override
  public void decreaseStackSize(int size) {
    stackSize -= size;
  }

  @Override
  public String getFileName() {
    return filename;
  }

  public String setFileName(String filename) {
    return this.filename = filename;
  }


  @Override
  public void visit(IRNode<E> node) throws SPLSyntaxError {
    node.doVisit(this);
  }

  @Override
  public int getFirstLineNo() {
    return firstLineNo;
  }

  @Override
  public void setFirstLineNo(int firstLineNo) {
    this.firstLineNo = firstLineNo;
  }

  @Override
  public byte[] getCode() {
    return code.toByteArray();
  }

  @Override
  public byte[] getDebugInfo() {
    return debugInfo.toByteArray();
  }

  @Override
  public byte[] getLenColumn() {
    return lenColumn.toByteArray();
  }

  public int getArgs() {
    return args;
  }

  public void setArgs(int args) {
    this.args = args;
  }

  @Override
  public int getNumberOfArgs() {
    return args;
  }

  @Override
  public void generateByteCodes(IRNode<E> node) throws SPLSyntaxError {
    node.accept(this);
    completeVisiting();
  }

  private void completeVisiting() throws SPLSyntaxError {
    write(insOfLine, debugInfo);
    int rest = currentLineNo - lastLineNo;
    write(rest, debugInfo);
    lastLineNo = 0;
    currentLineNo = 0;
    insOfLine = 0;
  }
}

