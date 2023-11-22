package org.spl.compiler.ir;

public abstract class AbstractIR<E> implements IRNode<E> {

  private int lineNo;
  private int columnNo;

  public AbstractIR(int lineNo, int columnNo) {
    this.lineNo = lineNo;
    this.columnNo = columnNo;
  }

  public AbstractIR() {
  }

  public int getLineNo() {
    return lineNo;
  }

  public void setLineNo(int lineNo) {
    this.lineNo = lineNo;
  }

  public int getColumnNo() {
    return columnNo;
  }

  public void setColumnNo(int columnNo) {
    this.columnNo = columnNo;
  }
}
