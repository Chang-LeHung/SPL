package org.spl.compiler.ir.context;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.NameSpace;
import org.spl.compiler.tree.Visitor;
import org.spl.vm.objects.SPLObject;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class DefaultASTContext<E extends Instruction> implements Visitor<E>, ASTContext<E> {

  private final List<E> instructions;
  private final Map<Object, Integer> varnames;
  private final Map<SPLObject, Integer> constants;
  private final ByteArrayOutputStream code;
  private final ByteArrayOutputStream debugInfo;
  private final ByteArrayOutputStream lenColumn;
  private final Set<String> globals;
  private final NameSpace<String> nameSpace;
  private String filename;
  private int firstLineNo;
  private int insOfLine;
  private int currentLineNo;
  private int lastLineNo;
  private int stackSize;
  private int topStackSize;
  private int args;

  public DefaultASTContext(String filename) {
    this.filename = filename;
    stackSize = 0;
    topStackSize = 0;
    instructions = new ArrayList<>();
    varnames = new HashMap<>();
    nameSpace = new NameSpace<>();
    firstLineNo = -1;
    insOfLine = 0;
    code = new ByteArrayOutputStream();
    debugInfo = new ByteArrayOutputStream();
    currentLineNo = 0;
    lastLineNo = 0;
    lenColumn = new ByteArrayOutputStream();
    globals = new HashSet<>();
    constants = new HashMap<>();
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
  public boolean isGlobal(String name) {
    return globals.contains(name);
  }

  @Override
  public SPLObject[] getConstants() {
    SPLObject[] constants = new SPLObject[this.constants.size()];
    this.constants.forEach((x, y) -> {
      constants[y] = x;
    });
    return constants;
  }

  @Override
  public Map<SPLObject, Integer> getConstantMap() {
    return constants;
  }

  @Override
  public void addConstantObject(SPLObject object) {
    if (constants.containsKey(object))
      return;
    constants.put(object, constants.size());
  }

  @Override
  public int getConstantObjectIndex(SPLObject o) {
    if (!constants.containsKey(o))
      throw new RuntimeException("not found \"" + o + "\"");
    return constants.get(o);
  }

  @Override
  public int getConstantsSize() {
    return constants.size();
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
  public int getVarNameIndex(Object o) {
    if (varnames.containsKey(o)) {
      return varnames.get(o);
    }
    throw new RuntimeException("Constant not found");
  }

  @Override
  public int addVarName(Object o) {
    if (varnames.containsKey(o))
      return varnames.get(o);
    varnames.put(o, varnames.size());
    return varnames.size() - 1;
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
    return varnames.get(name);
  }

  public Map<Object, Integer> getVarnames() {
    return varnames;
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

  public void addGlobal(String name) {
    globals.add(name);
  }

}

