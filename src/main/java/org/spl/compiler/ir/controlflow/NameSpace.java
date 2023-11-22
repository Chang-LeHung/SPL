package org.spl.compiler.ir.controlflow;


import java.util.HashSet;
import java.util.Set;

public class NameSpace<T> {

  private final Set<T> symbols;
  private NameSpace<T> prev;
  private NameSpace<T> next;

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

  public NameSpace<T> newNameSpaceAndFetch() {
    NameSpace<T> ns = new NameSpace<>();
    ns.setPrev(this);
    setNext(ns);
    return ns;
  }

  public NameSpace<T> getPrev() {
    assert prev != null;
    return prev;
  }

  public void setPrev(NameSpace<T> prev) {
    this.prev = prev;
  }

  public NameSpace<T> getNext() {
    assert next != null;
    return next;
  }

  public void setNext(NameSpace<T> next) {
    this.next = next;
  }
}
