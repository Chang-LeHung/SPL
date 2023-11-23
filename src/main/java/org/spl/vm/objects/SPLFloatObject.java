package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
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
  public SPLObject add(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(val + f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(val + l.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '+' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject sub(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(val - f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(val - l.getVal());
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '-' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject mul(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(val * f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(val * l.getVal());
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '*' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject div(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(val / f.getVal());
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(val / l.getVal());
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '/' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject mod(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject((int) (val % f.getVal()));
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject((int) (val % l.getVal()));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '%' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject pow(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return new SPLFloatObject(Math.pow(val, f.getVal()));
    } else if (rhs instanceof SPLLongObject l) {
      return new SPLFloatObject(Math.pow(val, l.getVal()));
    }
    return SPLErrorUtils
        .splErrorFormat(new SPLTypeError("can not apply operator '^' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject lshift(SPLObject rhs) throws SPLInternalException {
    return super.lshift(rhs);
  }

  @Override
  public SPLObject rshift(SPLObject rhs) throws SPLInternalException {
    return super.rshift(rhs);
  }

  @Override
  public SPLObject and(SPLObject rhs) throws SPLInternalException {
    return super.and(rhs);
  }

  @Override
  public SPLObject or(SPLObject rhs) throws SPLInternalException {
    return super.or(rhs);
  }

  @Override
  public SPLObject xor(SPLObject rhs) throws SPLInternalException {
    return super.xor(rhs);
  }

  @Override
  public SPLObject not() throws SPLInternalException {
    return super.not();
  }

  @Override
  public SPLObject neg() throws SPLInternalException {
    return new SPLFloatObject(-val);
  }

  @Override
  public SPLObject eq(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val == f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val == l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '==' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject ne(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val != f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val != l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '!=' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject lt(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val < f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val < l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject gt(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val > f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val > l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject le(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val <= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val <= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '<=' on float and " + rhs.getType().getName()));
  }

  @Override
  public SPLObject ge(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLFloatObject f) {
      return val >= f.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    } else if (rhs instanceof SPLLongObject l) {
      return val >= l.getVal() ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("can not apply operator '>=' on float and " + rhs.getType().getName()));
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
  public SPLObject invert() throws SPLInternalException {
    return super.invert();
  }

  @Override
  public SPLObject call(SPLObject... args) throws SPLInternalException {
    return super.call(args);
  }

  @Override
  public SPLObject str() {
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
