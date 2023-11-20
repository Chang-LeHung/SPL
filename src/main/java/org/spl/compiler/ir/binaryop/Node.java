package org.spl.compiler.ir;


import java.util.List;

public interface Node<E> {

  void codeGen(List<E> container);

}
