package org.spl.compiler.lexer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TokenFlow<T> implements Flow<T>, List<T> {

  private final List<T> tokens;
  private int off;

  public TokenFlow(List<T> tokens) {
    this.tokens = tokens;
    off = -1;
  }

  @Override
  public void next() {
    if (off > tokens.size())
      throw new IndexOutOfBoundsException();
    off++;
  }

  @Override
  public void back() {
    off--;
  }

  public int getOff() {
    return off;
  }

  public void setOff(int off) {
    this.off = off;
  }

  @Override
  public int size() {
    return tokens.size();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  @Override
  public Iterator<T> iterator() {
    return tokens.iterator();
  }

  @Override
  public Object[] toArray() {
    return tokens.toArray();
  }

  @Override
  public <E> E[] toArray(E[] a) {
    return tokens.toArray(a);
  }

  @Override
  public boolean add(T token) {
    return tokens.add(token);
  }

  @Override
  public boolean remove(Object o) {
    return tokens.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return tokens.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return tokens.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    return tokens.addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return tokens.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return tokens.retainAll(c);
  }

  @Override
  public void clear() {
    tokens.clear();
  }

  @Override
  public T get(int index) {
    return tokens.get(index);
  }

  @Override
  public T set(int index, T element) {
    return tokens.set(index, element);
  }

  @Override
  public void add(int index, T element) {
    tokens.add(index, element);
  }

  @Override
  public T remove(int index) {
    return tokens.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return tokens.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return tokens.lastIndexOf(o);
  }

  @Override
  public ListIterator<T> listIterator() {
    return tokens.listIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return tokens.listIterator(index);
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    return tokens.subList(fromIndex, toIndex);
  }

  @Override
  public T peek() {
    return current();
  }

  @Override
  public T peek(int n) {
    if (off + n >= tokens.size())
      throw new IndexOutOfBoundsException();
    return tokens.get(off + n);
  }

  @Override
  public T current() {
    if (off < 0)
      return null;
    else
      return tokens.get(off);
  }

  @Override
  public T lookAhead() {
    return lookAhead(1);
  }

  @Override
  public T lookBack() {
    return lookBack(1);
  }

  @Override
  public T lookBack(int n) {
    return tokens.get(off - n);
  }

  @Override
  public T lookAhead(int n) {
    return tokens.get(off + n);
  }

  @Override
  public int getCursor() {
    return getOff();
  }

  @Override
  public void setCursor(int cursor) {
    setOff(cursor);
  }

  @Override
  public String toString() {
    return tokens.toString();
  }
}
