package org.spl.vm.objects;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.types.SPLCommonType;

import java.util.Map;

public interface SPLInterface {

  SPLStringObject __init__ = new SPLStringObject("__init__");
  SPLStringObject __add__ = new SPLStringObject("__add__");
  SPLStringObject __sub__ = new SPLStringObject("__sub__");
  SPLStringObject __mul__ = new SPLStringObject("__mul__");
  SPLStringObject __div__ = new SPLStringObject("__div__");
  SPLStringObject __pow__ = new SPLStringObject("__pow__");
  SPLStringObject __mod__ = new SPLStringObject("__mod__");
  SPLStringObject __trueDiv__ = new SPLStringObject("__floorDiv__");
  SPLStringObject __and__ = new SPLStringObject("__and__");
  SPLStringObject __or__ = new SPLStringObject("__or__");
  SPLStringObject __not__ = new SPLStringObject("__not__");
  SPLStringObject __xor__ = new SPLStringObject("__xor__");
  SPLStringObject __conditionalAnd__ = new SPLStringObject("__conditionalAnd__");
  SPLStringObject __conditionalOr__ = new SPLStringObject("__conditionalOr__");
  SPLStringObject __str__ = new SPLStringObject("__str__");
  SPLStringObject __setAttr__ = new SPLStringObject("__setAttr__");
  SPLStringObject __getAttr__ = new SPLStringObject("__getAttr__");
  SPLStringObject __getIterator__ = new SPLStringObject("__getIterator__");
  SPLStringObject __subscribe__ = new SPLStringObject("__subscribe__");
  SPLStringObject __gt__ = new SPLStringObject("__gt__");
  SPLStringObject __lt__ = new SPLStringObject("__lt__");
  SPLStringObject __call__ = new SPLStringObject("__call__");
  SPLStringObject __ge__ = new SPLStringObject("__ge__");
  SPLStringObject __le__ = new SPLStringObject("__le__");
  SPLStringObject __eq__ = new SPLStringObject("__eq__");
  SPLStringObject __ne__ = new SPLStringObject("__ne__");
  SPLStringObject __neg__ = new SPLStringObject("__neg__");
  SPLStringObject __pos__ = new SPLStringObject("__pos__");
  SPLStringObject __invert__ = new SPLStringObject("__invert__");
  SPLStringObject __inplaceAdd__ = new SPLStringObject("__inplaceAdd__");
  SPLStringObject __inplaceConditionalAnd__ = new SPLStringObject("__inplaceConditionalAnd__");
  SPLStringObject __inplaceConditionalOr__ = new SPLStringObject("__inplaceConditionalOr__");
  SPLStringObject __inplaceSub__ = new SPLStringObject("__inplaceSub__");
  SPLStringObject __inplaceMul__ = new SPLStringObject("__inplaceMul__");
  SPLStringObject __inplaceDiv__ = new SPLStringObject("__inplaceDiv__");
  SPLStringObject __inplaceTrueDiv__ = new SPLStringObject("__inplaceTrueDiv__");
  SPLStringObject __inplaceMod__ = new SPLStringObject("__inplaceMod__");
  SPLStringObject __inplacePow__ = new SPLStringObject("__inplacePow__");
  SPLStringObject __inplaceLshift__ = new SPLStringObject("__inplaceLshift__");
  SPLStringObject __inplaceRshift__ = new SPLStringObject("__inplaceRshift__");
  SPLStringObject __inplaceURshift__ = new SPLStringObject("__inplaceURshift__");
  SPLStringObject __inplaceFloorDiv__ = new SPLStringObject("__inplaceFloorDiv__");
  SPLStringObject __inplaceAnd__ = new SPLStringObject("__inplaceAnd__");
  SPLStringObject __inplaceOr__ = new SPLStringObject("__inplaceOr__");
  SPLStringObject __inplaceXor__ = new SPLStringObject("__inplaceXor__");
  SPLStringObject __inplaceLShift__ = new SPLStringObject("__inplaceLShift__");
  SPLStringObject __inplaceRShift__ = new SPLStringObject("__inplaceRShift__");
  SPLStringObject __lshift__ = new SPLStringObject("__lshift__");
  SPLStringObject __rshift__ = new SPLStringObject("__rshift__");
  SPLStringObject __URshift__ = new SPLStringObject("__URshift__");


  SPLCommonType getType();

  SPLObject __add__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceAdd__(SPLObject rhs) throws SPLInternalException {
    return __add__(rhs);
  }

  SPLObject __sub__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceSub__(SPLObject rhs) throws SPLInternalException {
    return __sub__(rhs);
  }

  SPLObject __mul__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceMul__(SPLObject rhs) throws SPLInternalException {
    return __mul__(rhs);
  }

  SPLObject __div__(SPLObject rhs) throws SPLInternalException;

  SPLObject __trueDiv__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceDiv__(SPLObject rhs) throws SPLInternalException {
    return __div__(rhs);
  }

  default SPLObject __inplaceTrueDiv__(SPLObject rhs) throws SPLInternalException {
    return __trueDiv__(rhs);
  }

  SPLObject __mod__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceMod__(SPLObject rhs) throws SPLInternalException {
    return __mod__(rhs);
  }

  SPLObject __pow__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplacePow__(SPLObject rhs) throws SPLInternalException {
    return __pow__(rhs);
  }

  SPLObject __lshift__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceLshift__(SPLObject rhs) throws SPLInternalException {
    return __lshift__(rhs);
  }

  SPLObject __rshift__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceRshift__(SPLObject rhs) throws SPLInternalException {
    return __rshift__(rhs);
  }

  SPLObject __URshift__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceURshift__(SPLObject rhs) throws SPLInternalException {
    return __URshift__(rhs);
  }

  SPLObject __and__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceAnd__(SPLObject rhs) throws SPLInternalException {
    return __and__(rhs);
  }

  SPLObject __or__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceOr__(SPLObject rhs) throws SPLInternalException {
    return __or__(rhs);
  }

  SPLObject __xor__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceXor__(SPLObject rhs) throws SPLInternalException {
    return __xor__(rhs);
  }

  SPLObject __not__() throws SPLInternalException;

  SPLObject __neg__() throws SPLInternalException;

  SPLObject __eq__(SPLObject rhs) throws SPLInternalException;

  SPLObject __ne__(SPLObject rhs) throws SPLInternalException;

  SPLObject __lt__(SPLObject rhs) throws SPLInternalException;

  SPLObject __gt__(SPLObject rhs) throws SPLInternalException;

  SPLObject __le__(SPLObject rhs) throws SPLInternalException;

  SPLObject __ge__(SPLObject rhs) throws SPLInternalException;

  SPLObject __conditionalAnd__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceConditionalAnd__(SPLObject rhs) throws SPLInternalException {
    return __conditionalAnd__(rhs);
  }

  SPLObject __conditionalOr__(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceConditionalOr__(SPLObject rhs) throws SPLInternalException {
    return __conditionalOr__(rhs);
  }

  SPLObject __invert__() throws SPLInternalException;

  @SPLExportMethod
  SPLObject __call__(SPLObject... args) throws SPLInternalException;

  SPLObject __str__() throws SPLInternalException;

  default Map<SPLObject, SPLObject> getGlobals() {
    return null;
  }

  default void setGlobals(Map<SPLObject, SPLObject> globals) {
  }

  SPLObject __getAttr__(SPLObject name) throws SPLInternalException;

  SPLObject __getMethod__(SPLObject name) throws SPLInternalException, NoSuchMethodException;

  SPLObject __setAttr__(SPLObject name, SPLObject value) throws SPLInternalException;

  SPLObject __subscribe__(SPLObject args) throws SPLInternalException;

  SPLCommonIterator __getIterator__() throws SPLInternalException;
}
