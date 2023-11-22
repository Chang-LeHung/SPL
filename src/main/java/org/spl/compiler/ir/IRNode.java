package org.spl.compiler.ir;


import java.util.List;

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

  int getLen();

  int getColumnNo();

  int getLineNo();

  void setLineNo(int lineNo);

  void setColumnNo(int columnNo);


  void setLen(int len);

  default void preVisiting(ASTContext<E> context) {

  }

  default void postVisiting(ASTContext<E> context) {

  }


  default void accept(ASTContext<E> context) {
    context.visit(this);
  }

  default void doVisit(ASTContext<E> context) {
    preVisiting(context);
    List<AbstractIR<E>> children = getChildren();
    for (IRNode<E> child : children) {
      child.accept(context);
    }
    codeGen(context);
    postVisiting(context);
  }

  List<AbstractIR<E>> getChildren();

}
