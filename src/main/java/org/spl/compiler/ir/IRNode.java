package org.spl.compiler.ir;


public interface IRNode<E> {

  void codeGen(ASTContext<E> context);

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


  default Op getOperator() {
    return null;
  }

}
