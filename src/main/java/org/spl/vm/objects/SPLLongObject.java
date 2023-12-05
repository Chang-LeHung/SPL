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
  public SPLObject add(SPLObject rhs) throws SPLInternalException {
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
  public SPLObject sub(SPLObject rhs) throws SPLInternalException {
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
  public SPLObject div(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val / l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(getVal() / f.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '/' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject mod(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val % l.val);
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(getVal() % f.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '%' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject pow(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create((long) Math.pow(getVal(), l.getVal()));
    } else if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(Math.pow(getVal(), f.getVal()));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '**' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject lshift(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val << l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<<' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject rshift(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val >> l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>>' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject URshift(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val >>> l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>>>' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject and(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return create(val & l.val);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '&' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject or(SPLObject rhs) throws SPLInternalException {
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
  public SPLObject neg() {
    return create(-getVal());
  }

  @Override
  public SPLObject invert() {
    return create(~getVal());
  }

  @Override
  public SPLObject str() {
    return new SPLStringObject(String.valueOf(getVal()));
  }

  @Override
  public SPLObject not() throws SPLInternalException {
    return super.not();
  }

  @Override
  public SPLObject eq(SPLObject rhs) throws SPLInternalException {
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
  public SPLObject ne(SPLObject rhs) throws SPLInternalException {
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
  public SPLObject lt(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() < l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() < f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject gt(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() > l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() > f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject le(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() <= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() <= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<=' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject ge(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLLongObject l) {
      return getVal() >= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLFloatObject f) {
      return getVal() >= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '>=' on long and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject conditionalAnd(SPLObject rhs) throws SPLInternalException {
    return super.conditionalAnd(rhs);
  }

  @Override
  public SPLObject conditionalOr(SPLObject rhs) throws SPLInternalException {
    return super.conditionalOr(rhs);
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
  public SPLObject trueDiv(SPLObject rhs) throws SPLInternalException {
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
