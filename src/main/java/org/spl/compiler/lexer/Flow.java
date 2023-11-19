package org.spl.compiler.lexer;

public interface Flow<T> {

	T next();

	T lookAhead();

	T lookAhead(int n);

	int getOff();

	T peek();
	T peek(int n);
}
