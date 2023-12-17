package org.spl.compiler.ir.context;

import org.spl.compiler.bytecode.Instruction;
import org.spl.compiler.bytecode.OpCode;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.IRNode;
import org.spl.compiler.ir.NameSpace;
import org.spl.compiler.ir.block.ProgramBlock;
import org.spl.compiler.tree.Visitor;
import org.spl.vm.objects.SPLObject;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class DefaultASTContext<E extends Instruction> implements Visitor<E>, ASTContext<E> {

  private final List<E> instructions;
  private final List<JumpTableEntry> jumpTable;
  private final Map<Object, Integer> varnames;
  private final Map<String, Integer> closures;
  private final Map<SPLObject, Integer> constants;
  private final ByteArrayOutputStream code;
  private final ByteArrayOutputStream debugInfo;
  private final ByteArrayOutputStream lenColumn;
  private final Set<String> globals;
  private final NameSpace<String> nameSpace;
  private final List<String> sourceCode;
  private String coName;
  private String filename;
  private boolean started;
  private int firstLineNo;
  private int insOfLine;
  private int currentLineNo;
  private int lastLineNo;
  private int stackSize;
  private int topStackSize;
  private int args;
  private ProgramBlock pb;
  private boolean inTry = false;
  private boolean inFunc = false;
  private DefaultASTContext<E> prev;

  public DefaultASTContext(String filename, List<String> sourceCode) {
    this.filename = filename;
    coName = "<module>";
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
    this.sourceCode = sourceCode;
    lenColumn = new ByteArrayOutputStream();
    globals = new HashSet<>();
    constants = new HashMap<>();
    jumpTable = new ArrayList<>();
    started = false;
    closures = new HashMap<>();
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
  public void addJumpTableEntry(JumpTableEntry entry) {
    jumpTable.add(entry);
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
    return jumpTable;
  }

  @Override
  public List<String> getSourceCode() {
    return sourceCode;
  }

  @Override
  public void addInstruction(E instruction, int lineNo, int columnNo, int len) throws SPLSyntaxError {
    instructions.add(instruction);
    if (!started) {
      started = true;
      currentLineNo = lineNo;
      lastLineNo = firstLineNo;
      insOfLine++;
    } else {
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
    }

    // write instruction info
    write(instruction.getOpCode(), code);
    OpCode opcode = instruction.getCode();
    if (opcode == OpCode.JUMP_ABSOLUTE || opcode == OpCode.LONG_JUMP) {
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
    Integer integer = varnames.get(name);
    if (integer != null)
      return integer;
    return -1;
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
    currentLineNo = 0;
    insOfLine = 0;
  }

  public void addGlobal(String name) {
    globals.add(name);
  }

  @Override
  public String getCoName() {
    return coName;
  }

  @Override
  public void setCoName(String coName) {
    this.coName = coName;
  }

  @Override
  public boolean isInFunction() {
    return inFunc;
  }

  @Override
  public ASTContext<E> getPreviousContext() {
    return prev;
  }

  @Override
  public boolean requestClosure(String var) {
    if (inFunc) {
      if (varnames.containsKey(var))
        return true;
      else {
        if (prev != null) {
          if (prev.requestClosure(var)) {
            closures.put(var, closures.size());
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public Map<String, Integer> getClosureMap() {
    return closures;
  }

  public int loadClosureVar(String var) {
    if (inFunc) {
      if (closures.containsKey(var)) {
        return closures.get(var);
      } else {
        if (prev != null && prev.requestClosure(var)) {
          closures.put(var, closures.size());
          return closures.get(var);
        }
      }
    }
    // not found
    return -1;
  }

  public boolean isInFunc() {
    return inFunc;
  }

  public void setInFunc(boolean inFunc) {
    this.inFunc = inFunc;
  }

  public DefaultASTContext<E> getPrev() {
    return prev;
  }

  public void setPrev(DefaultASTContext<E> prev) {
    this.prev = prev;
  }
}

