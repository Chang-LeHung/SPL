package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.types.SPLStringType;

import java.util.Objects;

public class SPLStringObject extends SPLObject {
  private final String msg;

  public SPLStringObject(String msg) {
    super(SPLStringType.getInstance());
    this.msg = msg;
  }

  public String getVal() {
    return msg;
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLStringObject s) {
      return new SPLStringObject(msg + s.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '+' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject mul(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return new SPLStringObject(msg.repeat((int) l.getVal()));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '*' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLStringObject s) {
      return msg.equals(s.getVal()) ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '==' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLStringObject s) {
      return !msg.equals(s.getVal()) ? SPLBoolObject.getFalse() : SPLBoolObject.getTrue();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '!=' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __lt__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLStringObject s) {
      return msg.compareTo(s.getVal()) < 0 ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __gt__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLStringObject s) {
      return msg.compareTo(s.getVal()) > 0 ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __le__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLStringObject s) {
      return msg.compareTo(s.getVal()) <= 0 ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<=' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __ge__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLStringObject s) {
      return msg.compareTo(s.getVal()) >= 0 ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>=' on str and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __str__() {
    return this;
  }

  @Override
  public int hashCode() {
    return msg.hashCode();
  }

  @Override
  public String toString() {
    return msg;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SPLStringObject that)) return false;
    return Objects.equals(msg, that.msg);
  }
}
