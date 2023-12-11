package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.exceptions.splexceptions.SPLZeroDivisionError;
import org.spl.vm.types.SPlFloatType;

public class SPLFloatObject extends SPLObject {

  private final double val;

  public SPLFloatObject(double val) {
    super(SPlFloatType.getInstance());
    this.val = val;
  }

  public double getVal() {
    return val;
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(val + f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(val + l.getVal());
    } else if (rhs instanceof SPLStringObject s) {
      return new SPLStringObject(val + s.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '+' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __sub__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(val - f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(val - l.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '-' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __mul__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(val * f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(val * l.getVal());
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '*' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __div__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      if (f.getVal() == 0)
        return SPLErrorUtils.splErrorFormat(new SPLZeroDivisionError("division by zero"));
      return new SPLFloatObject(val / f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      if (l.getVal() == 0)
        return SPLErrorUtils.splErrorFormat(new SPLZeroDivisionError("division by zero"));
      return new SPLFloatObject(val / l.getVal());
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '/' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __mod__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject((int) (val % f.getVal()));
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject((int) (val % l.getVal()));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '%' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __pow__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(Math.pow(val, f.getVal()));
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(Math.pow(val, l.getVal()));
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '^' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __lshift__(SPLObject rhs) throws SPLInternalException {
    return super.__lshift__(rhs);
  }

  @Override
  public SPLObject __rshift__(SPLObject rhs) throws SPLInternalException {
    return super.__rshift__(rhs);
  }

  @Override
  public SPLObject __and__(SPLObject rhs) throws SPLInternalException {
    return super.__and__(rhs);
  }

  @Override
  public SPLObject __or__(SPLObject rhs) throws SPLInternalException {
    return super.__or__(rhs);
  }

  @Override
  public SPLObject __xor__(SPLObject rhs) throws SPLInternalException {
    return super.__xor__(rhs);
  }

  @Override
  public SPLObject __not__() throws SPLInternalException {
    return super.__not__();
  }

  @Override
  public SPLObject __neg__() throws SPLInternalException {
    return new SPLFloatObject(-val);
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val == f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val == l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '==' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val != f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val != l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '!=' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __lt__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val < f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val < l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __gt__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val > f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val > l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __le__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val <= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val <= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<=' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __ge__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val >= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val >= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>=' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __conditionalAnd__(SPLObject rhs) throws SPLInternalException {
    return super.__conditionalAnd__(rhs);
  }

  @Override
  public SPLObject __conditionalOr__(SPLObject rhs) throws SPLInternalException {
    return super.__conditionalOr__(rhs);
  }

  @Override
  public SPLObject __invert__() throws SPLInternalException {
    return super.__invert__();
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    return super.__call__(args);
  }

  @Override
  public SPLObject __str__() {
    return new SPLStringObject(String.valueOf(val));
  }

  @Override
  public int hashCode() {
    return Double.hashCode(val);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SPLFloatObject that)) return false;
    return Double.compare(that.val, val) == 0;
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }
}
