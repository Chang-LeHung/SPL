package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLOutOfBoundException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.types.SPLStringType;

import java.util.ArrayList;
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
  public SPLObject __mul__(SPLObject rhs) throws SPLInternalException {
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

  @Override
  public SPLCommonIterator __getIterator__() throws SPLInternalException {
    ArrayList<SPLStringObject> subs = new ArrayList<>();
    for (int i = 0; i < msg.length(); i++) {
      subs.add(new SPLStringObject(msg.substring(i, i + 1)));
    }
    return new SPLCommonIterator(subs);
  }

  @Override
  public SPLObject __subscribe__(SPLObject args) throws SPLInternalException {
    if (args instanceof SPLLongObject l) {
      if (l.getVal() < msg.length())
        return new SPLStringObject(msg.substring((int) l.getVal(), (int) l.getVal() + 1));
      else
        return SPLErrorUtils.splErrorFormat(new SPLOutOfBoundException("index out of range"));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '[]' on str and " + args.getType().getName()));
  }

  public SPLObject slice(SPLObject... args) throws SPLInternalException {
    if (args.length == 2) {
      SPLObject L = args[0];
      SPLObject R = args[1];
      if (L instanceof SPLLongObject l && R instanceof SPLLongObject r) {
        if (l.getVal() < msg.length() && r.getVal() < msg.length()) {
          return new SPLStringObject(msg.substring((int) l.getVal(), (int) r.getVal() + 1));
        }
      }
    } else if (args.length == 3) {
      SPLObject L = args[0];
      SPLObject R = args[1];
      SPLObject S = args[2];
      if (L instanceof SPLLongObject l && R instanceof SPLLongObject r && S instanceof SPLLongObject s) {
        StringBuilder builder = new StringBuilder();
        for (long i = l.getVal(); i < r.getVal(); i += s.getVal()) {
          builder.append(msg.charAt((int) i));
        }
        return new SPLStringObject(builder.toString());
      }
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Invalid arguments for method 'slice'"));
  }
}
