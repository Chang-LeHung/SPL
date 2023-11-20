package org.spl.compiler.ir;

import java.util.*;

public class ASTContext<E> implements List<E> {

  List<E> byteCodes;
  Map<String, Integer> labels;
  Map<Object, Integer> constantTable;

  public ASTContext() {
    byteCodes = new ArrayList<>();
    labels = new HashMap<>();
    constantTable = new HashMap<>();
  }

  public ASTContext(List<E> byteCodes) {
    this.byteCodes = byteCodes;
    labels = new HashMap<>();
    constantTable = new HashMap<>();
  }


  @Override
  public int size() {
    return byteCodes.size();
  }

  @Override
  public boolean isEmpty() {
    return byteCodes.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return byteCodes.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return byteCodes.iterator();
  }

  @Override
  public Object[] toArray() {
    return byteCodes.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return byteCodes.toArray(a);
  }

  @Override
  public boolean add(E t) {
    return byteCodes.add(t);
  }

  @Override
  public boolean remove(Object o) {
    return byteCodes.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return byteCodes.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return byteCodes.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    return byteCodes.addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return byteCodes.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return byteCodes.retainAll(c);
  }

  @Override
  public void clear() {
    byteCodes.clear();
  }

  public E get(int index) {
    return byteCodes.get(index);
  }

  @Override
  public E set(int index, E element) {
    return byteCodes.set(index, element);
  }

  @Override
  public void add(int index, E element) {
    byteCodes.add(index, element);
  }

  @Override
  public E remove(int index) {
    return byteCodes.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return byteCodes.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return byteCodes.lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator() {
    return byteCodes.listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return byteCodes.listIterator(index);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return byteCodes.subList(fromIndex, toIndex);
  }
}
