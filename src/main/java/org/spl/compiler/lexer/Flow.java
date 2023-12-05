package org.spl.compiler.lexer;

public interface Flow<T> {

  void next();

  T lookAhead();

  T lookAhead(int n);

  int getCursor();

  void setCursor(int cursor);

  T peek();

  T peek(int n);

  T current();
}
