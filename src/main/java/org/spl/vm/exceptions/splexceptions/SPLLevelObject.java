package org.spl.vm.exceptions.splexceptions;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.objects.SPLCommonIterator;
import org.spl.vm.objects.SPLObject;
import org.spl.vm.types.SPLCommonType;

public class SPLLevelObject extends SPLObject {
  public SPLLevelObject(SPLCommonType type) {
    super(type);
  }


  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__add__);
    if (func == null) {
      return super.__add__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __sub__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__sub__);
    if (func == null) {
      return super.__sub__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __mul__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__mul__);
    if (func == null) {
      return super.__mul__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __div__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__div__);
    if (func == null) {
      return super.__div__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __trueDiv__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__trueDiv__);
    if (func == null) {
      return super.__trueDiv__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __mod__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__mod__);
    if (func == null) {
      return super.__mod__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __pow__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__pow__);
    if (func == null) {
      return super.__pow__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __and__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__and__);
    if (func == null) {
      return super.__and__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __lt__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__lt__);
    if (func == null) {
      return super.__lt__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __call__(SPLObject... args) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__call__);
    if (func == null) {
      return super.__call__(args);
    }
    return func.__call__(args);
  }

  @Override
  public SPLObject __xor__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__xor__);
    if (func == null) {
      return super.__xor__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __lshift__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__lshift__);
    if (func == null) {
      return super.__lshift__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __URshift__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__URshift__);
    if (func == null) {
      return super.__URshift__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __rshift__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__rshift__);
    if (func == null) {
      return super.__rshift__(rhs);
    }
    return func.__call__(this, rhs);
  }


  @Override
  public SPLObject __not__() throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__not__);
    if (func == null) {
      return super.__not__();
    }
    return func.__call__(this);
  }

  @Override
  public SPLObject __invert__() throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__invert__);
    if (func == null) {
      return super.__invert__();
    }
    return func.__call__(this);
  }

  @Override
  public SPLObject __neg__() throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__neg__);
    if (func == null) {
      return super.__neg__();
    }
    return func.__call__(this);
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__eq__);
    if (func == null) {
      return super.__eq__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__ne__);
    if (func == null) {
      return super.__ne__(rhs);
    }
    return func.__call__(this, rhs);
  }


  @Override
  public SPLObject __gt__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__gt__);
    if (func == null) {
      return super.__gt__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __or__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__or__);
    if (func == null) {
      return super.__or__(rhs);
    }
    SPLObject[] args = {this, rhs};
    return func.__call__(args);
  }


  @Override
  public SPLObject __le__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__le__);
    if (func == null) {
      return super.__le__(rhs);
    }
    SPLObject[] args = {this, rhs};
    return func.__call__(args);
  }

  @Override
  public SPLObject __ge__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__ge__);
    if (func == null) {
      return super.__ge__(rhs);
    }
    SPLObject[] args = {this, rhs};
    return func.__call__(args);
  }

  @Override
  public SPLObject __conditionalAnd__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__conditionalAnd__);
    if (func == null) {
      return super.__conditionalAnd__(rhs);
    }
    SPLObject[] args = {this, rhs};
    return func.__call__(args);
  }

  @Override
  public SPLObject __conditionalOr__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__conditionalOr__);
    if (func == null) {
      return super.__conditionalOr__(rhs);
    }
    SPLObject[] args = {this, rhs};
    return func.__call__(args);
  }

  @Override
  public SPLObject __str__() throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__str__);
    if (func == null) {
      return super.__str__();
    }
    return func.__call__(this);
  }

  @Override
  public SPLObject __setAttr__(SPLObject name, SPLObject value) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(name);
    if (func == null) {
      return super.__setAttr__(name, value);
    }
    return func.__call__(this, name, value);
  }

  @Override
  public SPLObject __subscribe__(SPLObject args) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__subscribe__);
    if (func != null) {
      return func.__call__(args);
    }
    return super.__subscribe__(args);
  }

  @Override
  public SPLObject __getAttr__(SPLObject name) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__getAttr__);
    if (func != null) {
      return func.__call__(name);
    }
    return super.__getAttr__(name);
  }

  @Override
  public SPLCommonIterator __getIterator__() throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__getIterator__);
    if (func == null) {
      return super.__getIterator__();
    }
    return (SPLCommonIterator) func.__call__(this);
  }

  @Override
  public SPLObject __inplaceAdd__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceAdd__);
    if (func == null) {
      return super.__inplaceAdd__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceSub__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceSub__);
    if (func == null) {
      return super.__inplaceSub__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceMul__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceMul__);
    if (func == null) {
      return super.__inplaceMul__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceDiv__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceDiv__);
    if (func == null) {
      return super.__inplaceDiv__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceTrueDiv__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceTrueDiv__);
    if (func == null) {
      return super.__inplaceTrueDiv__(rhs);
    }
    return func.__call__(this, rhs);
  }


  @Override
  public SPLObject __inplaceMod__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceMod__);
    if (func == null) {
      return super.__inplaceMod__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplacePow__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplacePow__);
    if (func == null) {
      return super.__inplacePow__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceLshift__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceLshift__);
    if (func == null) {
      return super.__inplaceLshift__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceRshift__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceRshift__);
    if (func == null) {
      return super.__inplaceRshift__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceURshift__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceURshift__);
    if (func == null) {
      return super.__inplaceURshift__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceAnd__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceAnd__);
    if (func == null) {
      return super.__inplaceAnd__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceOr__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceOr__);
    if (func == null) {
      return super.__inplaceOr__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceXor__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceXor__);
    if (func == null) {
      return super.__inplaceXor__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceConditionalAnd__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceConditionalAnd__);
    if (func == null) {
      return super.__inplaceConditionalAnd__(rhs);
    }
    return func.__call__(this, rhs);
  }

  @Override
  public SPLObject __inplaceConditionalOr__(SPLObject rhs) throws SPLInternalException {
    SPLObject func = type.getAttrFromAttrs(__inplaceConditionalOr__);
    if (func == null) {
      return super.__inplaceConditionalOr__(rhs);
    }
    return func.__call__(this, rhs);
  }
}
