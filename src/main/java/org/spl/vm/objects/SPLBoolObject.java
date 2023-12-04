package org.spl.vm.objects;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.types.SPLBoolType;

public class SPLBoolObject extends SPLObject {

  private final boolean value;

  private SPLBoolObject(boolean value) {
    super(SPLBoolType.getInstance());
    this.value = value;
  }

  public static SPLBoolObject getTrue() {
    return SelfHolder.TRUE;
  }

  public static SPLBoolObject getFalse() {
    return SelfHolder.FALSE;
  }

  public boolean isTrue() {
    return value;
  }

  public static class SelfHolder {
    public static SPLBoolObject TRUE = new SPLBoolObject(true);
    public static SPLBoolObject FALSE = new SPLBoolObject(false);
    public static SPLBoolObject True = TRUE;
    public static SPLBoolObject False = FALSE;
  }

  @Override
  public String toString() {
    return Boolean.toString(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SPLBoolObject that)) return false;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(value);
  }

  @Override
  public SPLObject not() throws SPLInternalException {
    if (this == getTrue())
      return getFalse();
    return getTrue();
  }

  @Override
  public SPLObject neg() throws SPLInternalException {
    if (this == getTrue())
      return getFalse();
    return getTrue();
  }

  @Override
  public SPLObject eq(SPLObject rhs) throws SPLInternalException {
    return this == rhs ? getTrue() : getFalse();
  }

  @Override
  public SPLObject ne(SPLObject rhs) throws SPLInternalException {
    return this == rhs ? getFalse() : getTrue();
  }

  @Override
  public SPLObject conditionalAnd(SPLObject rhs) throws SPLInternalException {
    if (this == getFalse() || rhs == getFalse())
      return getFalse();
    return getTrue();
  }

  @Override
  public SPLObject conditionalOr(SPLObject rhs) throws SPLInternalException {
    if (this == getTrue() || rhs == getTrue())
      return getTrue();
    return getFalse();
  }

  @Override
  public SPLObject str() {
    return getTrue() == this? SelfHolder.True : SelfHolder.False;
  }
}
