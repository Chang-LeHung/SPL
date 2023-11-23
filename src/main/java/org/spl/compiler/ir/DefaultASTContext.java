package org.spl.compiler.ir;

import org.spl.compiler.ir.controlflow.NameSpace;
import org.spl.compiler.tree.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultASTContext<E> implements Visitor<E>, ASTContext<E> {

  private final List<E> instructions;
  private final Map<String, Integer> labels;
  private final Map<Object, Integer> constantTable;

  private final NameSpace<String> nameSpace;
  private int stackSize;
  private int topStackSize;

  public DefaultASTContext() {
    stackSize = 0;
    topStackSize = 0;
    instructions = new ArrayList<>();
    labels = new HashMap<>();
    constantTable = new HashMap<>();
    nameSpace = new NameSpace<>();
  }

  public DefaultASTContext(List<E> instructions) {
    this.instructions = instructions;
    labels = new HashMap<>();
    constantTable = new HashMap<>();
    nameSpace = new NameSpace<>();
    stackSize = 0;
    topStackSize = 0;
  }

  @Override
  public int getTopStackSize() {
    return topStackSize;
  }

  public List<E> getInstructions() {
    return instructions;
  }

  @Override
  public void addInstruction(E instruction, int lineNo, int columnNo, int len) {
    instructions.add(instruction);
  }

  @Override
  public void add(E instruction, int lineNo, int columnNo, int len) {
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
  public void visit(IRNode<E> node) {
    node.doVisit(this);
  }
}

