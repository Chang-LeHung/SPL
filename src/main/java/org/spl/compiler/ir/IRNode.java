package org.spl.compiler.ir;


import java.util.List;

public interface Node<E> {

  void codeGen(List<E> container);

  default boolean isLiteral() {
    return false;
  }

  default boolean isIntLiteral() {
    return false;
  }

  default boolean isFloatLiteral() {
    return false;
  }

  default boolean isStringLiteral() {
    return false;
  }

  default boolean isBooleanLiteral() {
    return false;
  }

  default boolean isVariable() {
    return false;
  }


  Op getOperator();

}
