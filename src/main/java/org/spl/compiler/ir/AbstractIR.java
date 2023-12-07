package org.spl.compiler.ir;

public abstract class AbstractIR<E> implements IRNode<E> {

  private int lineNo;
  private int columnNo;
  private int len;

  public AbstractIR(int lineNo, int columnNo, int len) {
    this.lineNo = lineNo;
    this.columnNo = columnNo;
    this.len = len;
  }

  public AbstractIR() {
  }

  @Override
  public int getLen() {
    return len;
  }

  @Override
  public void setLen(int len) {
    this.len = len;
  }

  @Override
  public int getLineNo() {
    return lineNo;
  }

  @Override
  public void setLineNo(int lineNo) {
    this.lineNo = lineNo;
  }

  @Override
  public int getColumnNo() {
    return columnNo;
  }

  @Override
  public void setColumnNo(int columnNo) {
    this.columnNo = columnNo;
  }

  public boolean isStatement() {
    return false;
  }
}
