package org.spl.compiler.ir;

import org.spl.compiler.ir.controlflow.NameSpace;
import org.spl.compiler.tree.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTContext<E> implements Visitor<E> {

  private final List<E> instructions;
  private final Map<String, Integer> labels;
  private final Map<Object, Integer> constantTable;

  private final NameSpace<String> nameSpace;
  private int stackSize;
  private int topStackSize;

  public ASTContext() {
    stackSize = 0;
    topStackSize = 0;
    instructions = new ArrayList<>();
    labels = new HashMap<>();
    constantTable = new HashMap<>();
    nameSpace = new NameSpace<>();
  }

  public ASTContext(List<E> instructions) {
    this.instructions = instructions;
    labels = new HashMap<>();
    constantTable = new HashMap<>();
    nameSpace = new NameSpace<>();
    stackSize = 0;
    topStackSize = 0;
  }

  public int getTopStackSize() {
    return topStackSize;
  }

  public List<E> getInstructions() {
    return instructions;
  }

  public void addInstruction(E instruction) {
    instructions.add(instruction);
  }

  public void add(E instruction) {
    addInstruction(instruction);
  }

  public E getInstruction(int index) {
    return instructions.get(index);
  }

  public int getConstantIndex(Object o) {
    if (constantTable.containsKey(o)) {
      return constantTable.get(o);
    }
    throw new RuntimeException("Constant not found");
  }

  public int addConstant(Object o) {
    if (constantTable.containsKey(o))
      return constantTable.get(o);
    constantTable.put(o, constantTable.size());
    return constantTable.size() - 1;
  }

  public void addSymbol(String name) {
    nameSpace.addSymbol(name);
  }

  public boolean containSymbol(String name) {
    return nameSpace.contain(name);
  }

  public int getSymbolIndex(String name) {
    return constantTable.get(name);
  }

  public Map<Object, Integer> getConstantTable() {
    return constantTable;
  }

  public void increaseStackSize() {
    increaseStackSize(1);
  }

  public void increaseStackSize(int size) {
    stackSize += size;
    if (stackSize > topStackSize)
      topStackSize = stackSize;
  }

  public void decreaseStackSize() {
    stackSize--;
  }

  public int getStackSize() {
    return stackSize;
  }

  public void decreaseStackSize(int size) {
    stackSize -= size;
  }


  @Override
  public void visit(IRNode<E> node) {
    node.doVisit(this);
  }
}

