package org.spl.vm.objects;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.types.SPLCommonType;

import java.util.Map;

public interface SPLInterface {


  SPLCommonType getType();

  SPLObject __add__(SPLObject rhs) throws SPLInternalException;
  default SPLObject __inplaceAdd__(SPLObject rhs) throws SPLInternalException {
    return __add__(rhs);
  }

  SPLObject __sub__(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceSub(SPLObject rhs) throws SPLInternalException {
    return __sub__(rhs);
  }

  SPLObject mul(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceMul__(SPLObject rhs) throws SPLInternalException {
    return mul(rhs);
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

  SPLObject xor(SPLObject rhs) throws SPLInternalException;

  default SPLObject __inplaceXor__(SPLObject rhs) throws SPLInternalException {
    return xor(rhs);
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

  SPLObject __call__(SPLObject... args) throws SPLInternalException;

  SPLObject __str__();

  default Map<SPLObject, SPLObject> getGlobals() {
    return null;
  }

  default void setGlobals(Map<SPLObject, SPLObject> globals) {
    return;
  }
}
