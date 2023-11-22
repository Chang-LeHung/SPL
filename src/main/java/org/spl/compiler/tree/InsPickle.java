package org.spl.compiler.tree;

public interface InsPickle {

  <T extends Visitor> void accept(T t);
}
