package org.spl.compiler.ir;

import java.util.Map;

public interface ASTContext<E> {

  void increaseStackSize();
  void increaseStackSize(int n);
  void decreaseStackSize();
  void decreaseStackSize(int n);
  void addInstruction(E instruction, int lineNo, int columnNo, int len);
  void add(E instruction, int lineNo, int columnNo, int len);
  int addConstant(Object o);
  int getConstantIndex(Object o);
  boolean containSymbol(String name);
  int getSymbolIndex(String name);
  Map<Object, Integer> getConstantTable();
  int getTopStackSize();
  int getStackSize();
  E getInstruction(int index);
  void visit(IRNode<E> node);

  void addSymbol(String name);

}