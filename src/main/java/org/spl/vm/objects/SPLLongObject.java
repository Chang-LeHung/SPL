package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.types.SPLObjectType;

public class SPLLongObject extends SPLObject {
  public static SPLLongObject[] pool;

  static {
    pool = new SPLLongObject[301];
    for (int i = -5; i < 256; i++) {
      pool[i + 5] = new SPLLongObject(i);
    }
  }

  private final long val;

  private SPLLongObject(long val) {
    super(SPLObjectType.getInstance());
    this.val = val;
  }

  public static SPLLongObject create(long val) {
    if (val >= -5 && val < 256) {
      return pool[(int) val + 5];
    }
    return new SPLLongObject(val);
  }

  public long getVal() {
    return val;
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val + l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      double res = getVal() + f.getVal();
      return new SPLFloatObject(res);
    } else if (rhs instanceof SPLStringObject s) {
      return new SPLStringObject(String.valueOf(val) + s.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '+' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __sub__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val - l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(getVal() - f.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '-' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject mul(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val * l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(getVal() * f.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '*' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __div__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val / l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(getVal() / f.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '/' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __mod__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val % l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(getVal() % f.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '%' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __pow__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create((long) Math.pow(getVal(), l.getVal()));
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(Math.pow(getVal(), f.getVal()));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '**' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __lshift__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val << l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<<' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __rshift__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val >> l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>>' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __URshift__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val >>> l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>>>' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __and__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val & l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '&' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __or__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val | l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '|' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject xor(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val ^ l.val);

    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '^' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __neg__() {
    return create(-getVal());
  }

  @Override
  public SPLObject __invert__() {
    return create(~getVal());
  }

  @Override
  public SPLObject __str__() {
    return new SPLStringObject(String.valueOf(getVal()));
  }

  @Override
  public SPLObject __not__() throws SPLInternalException {
    return super.__not__();
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      if (l.getVal() == getVal()) {
        return SPLBoolObject.getTrue();
      } else {
        return SPLBoolObject.getFalse();
      }
    } else if (rhs instanceof SPLFloatObject f) {
      return f.getVal() == getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '==' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      if (l.getVal() != getVal()) {
        return SPLBoolObject.getTrue();
      } else {
        return SPLBoolObject.getFalse();
      }
    } else if (rhs instanceof SPLFloatObject f) {
      return f.getVal() != getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '!=' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __lt__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() < l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() < f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __gt__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() > l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() > f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __le__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() <= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() <= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<=' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject __ge__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() >= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() >= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '>=' on long and " + rhs.getType().getName()));
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
  public int hashCode() {
    return Long.hashCode(val);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SPLLongObject that)) return false;
    return val == that.val;
  }

  @Override
  public SPLObject __trueDiv__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create((int) val / l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject((int) getVal() / f.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '/' on long and " + rhs.getType().getName()));
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }
}
