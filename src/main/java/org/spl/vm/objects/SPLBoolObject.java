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
  public SPLObject __not__() throws SPLInternalException {
    if (this == getTrue())
      return getFalse();
    return getTrue();
  }

  @Override
  public SPLObject __neg__() throws SPLInternalException {
    if (this == getTrue())
      return getFalse();
    return getTrue();
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    return this == rhs ? getTrue() : getFalse();
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    return this == rhs ? getFalse() : getTrue();
  }

  @Override
  public SPLObject __conditionalAnd__(SPLObject rhs) throws SPLInternalException {
    if (this == getFalse() || rhs == getFalse())
      return getFalse();
    return getTrue();
  }

  @Override
  public SPLObject __conditionalOr__(SPLObject rhs) throws SPLInternalException {
    if (this == getTrue() || rhs == getTrue())
      return getTrue();
    return getFalse();
  }

  @Override
  public SPLObject __str__() {
    return getTrue() == this ? SelfHolder.True : SelfHolder.False;
  }

  public static class SelfHolder {
    public static SPLBoolObject TRUE = new SPLBoolObject(true);
    public static SPLBoolObject FALSE = new SPLBoolObject(false);
    public static SPLBoolObject True = TRUE;
    public static SPLBoolObject False = FALSE;
  }
}
