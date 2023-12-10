package org.spl.vm.internal.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrettyPrinter {

  private List<String> header;

  private List<Row> data;

  private int edgeSize = 2;

  public PrettyPrinter() {
    data = new ArrayList<>();
  }

  public static <K, V> void printMap(List<Map<K, V>> maps) {
    if (maps.size() == 0) return;
    PrettyPrinter printer = new PrettyPrinter();
    Map<K, V> map = maps.get(0);
    List<String> header = map.keySet().stream().map(Object::toString).collect(Collectors.toList());
    printer.setHeader(header);
    for (Map<K, V> t : maps) {
      List<String> collect = t.values().stream().map(Object::toString).collect(Collectors.toList());
      Row row = new Row(collect);
      printer.addRow(row);
    }
    printer.print();
  }

  public int getEdgeSize() {
    return edgeSize;
  }

  public void setEdgeSize(int edgeSize) {
    this.edgeSize = edgeSize;
  }

  public List<String> getHeader() {
    return header;
  }

  public void setHeader(List<String> header) {
    this.header = header;
  }

  public boolean addRow(Row row) {
    return data.add(row);
  }

  public boolean addRow(List<String> row) {
    return data.add(new Row(row));
  }

  public List<Row> getData() {
    return data;
  }

  public void setData(List<Row> data) {
    this.data = data;
  }

  public void print() {
    List<Integer> list = findLens();
    printLine(list);
    System.out.println();
    printData(header, list);
    System.out.println();
    printLine(list);
    System.out.println();
    for (Row row : data) {
      printData(row.getData(), list);
      System.out.println();
    }
    printLine(list);
    System.out.println();
  }

  private void printData(List<String> data, List<Integer> list) {
    System.out.print('|');
    for (int i = 0; i < list.size(); ++i) {
      String s = data.get(i);
      int left = list.get(i) - s.length();
      int L = left / 2;
      int R = left - L;
      printChar(L, ' ');
      System.out.print(s);
      printChar(R, ' ');
      System.out.print('|');
    }
  }

  private void printLine(List<Integer> list) {
    System.out.print('+');
    for (Integer v : list) {
      printChar(v, '-');
      System.out.print('+');
    }
  }

  private void printChar(int size, char c) {
    for (int i = 0; i < size; ++i) {
      System.out.print(c);
    }
  }

  private List<Integer> findLens() {
    List<Integer> arrayList = new ArrayList<>();
    for (String s : header) {
      arrayList.add(s.length() + edgeSize);
    }
    for (Row row : data) {
      List<String> t = row.getData();
      for (int i = 0; i < t.size(); ++i) {
        int size = t.get(i).length() + edgeSize;
        if (arrayList.get(i) < size)
          arrayList.set(i, size);
      }
    }
    return arrayList;
  }

  public static class Row {
    List<String> data;

    public Row(List<String> param) {
      data = param;
    }

    public List<String> getData() {
      return data;
    }

    public void setData(List<String> data) {
      this.data = data;
    }
  }
}