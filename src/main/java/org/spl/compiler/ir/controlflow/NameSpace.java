package org.spl.compiler.ir.controlflow;


import java.util.HashSet;
import java.util.Set;

public class NameSpace<T> {

  private NameSpace<T> prev;
  private NameSpace<T> next;
  private final Set<T> symbols;

  public NameSpace() {
    symbols = new HashSet<>();
  }

  public boolean addSymbol(T symbol) {
    return symbols.add(symbol);
  }

  public boolean removeSymbol(T symbol) {
    return symbols.remove(symbol);
  }

  public boolean contain(T symbol) {
    NameSpace<T> cur = this;
    while (cur != null) {
      if (symbols.contains(symbol)) {
        return true;
      }
      cur = cur.prev;
    }
    return false;
  }

  public void setNext(NameSpace<T> next) {
    this.next = next;
  }

  public void setPrev(NameSpace<T> prev) {
    this.prev = prev;
  }

  public NameSpace<T> getPrev() {
    return prev;
  }

  public NameSpace<T> getNext() {
    return next;
  }
}
