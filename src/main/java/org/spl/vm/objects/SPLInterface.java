package org.spl.vm.objects;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.types.SPLCommonType;

import java.util.Map;

public interface SPLInterface {


  SPLCommonType getType();

  SPLObject add(SPLObject rhs) throws SPLInternalException;
  default SPLObject inplaceAdd(SPLObject rhs) throws SPLInternalException {
    return add(rhs);
  }

  SPLObject sub(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceSub(SPLObject rhs) throws SPLInternalException {
    return sub(rhs);
  }

  SPLObject mul(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceMul(SPLObject rhs) throws SPLInternalException {
    return mul(rhs);
  }

  SPLObject div(SPLObject rhs) throws SPLInternalException;
  SPLObject trueDiv(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceDiv(SPLObject rhs) throws SPLInternalException {
    return div(rhs);
  }

  default SPLObject inplaceTrueDiv(SPLObject rhs) throws SPLInternalException {
    return trueDiv(rhs);
  }

  SPLObject mod(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceMod(SPLObject rhs) throws SPLInternalException {
    return mod(rhs);
  }

  SPLObject pow(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplacePow(SPLObject rhs) throws SPLInternalException {
    return pow(rhs);
  }

  SPLObject lshift(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceLshift(SPLObject rhs) throws SPLInternalException {
    return lshift(rhs);
  }

  SPLObject rshift(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceRshift(SPLObject rhs) throws SPLInternalException {
    return rshift(rhs);
  }

  SPLObject URshift(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceURshift(SPLObject rhs) throws SPLInternalException {
    return URshift(rhs);
  }

  SPLObject and(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceAnd(SPLObject rhs) throws SPLInternalException {
    return and(rhs);
  }

  SPLObject or(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceOr(SPLObject rhs) throws SPLInternalException {
    return or(rhs);
  }

  SPLObject xor(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceXor(SPLObject rhs) throws SPLInternalException {
    return xor(rhs);
  }

  SPLObject not() throws SPLInternalException;

  SPLObject neg() throws SPLInternalException;

  SPLObject eq(SPLObject rhs) throws SPLInternalException;

  SPLObject ne(SPLObject rhs) throws SPLInternalException;

  SPLObject lt(SPLObject rhs) throws SPLInternalException;

  SPLObject gt(SPLObject rhs) throws SPLInternalException;

  SPLObject le(SPLObject rhs) throws SPLInternalException;

  SPLObject ge(SPLObject rhs) throws SPLInternalException;

  SPLObject conditionalAnd(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceConditionalAnd(SPLObject rhs) throws SPLInternalException {
    return conditionalAnd(rhs);
  }

  SPLObject conditionalOr(SPLObject rhs) throws SPLInternalException;

  default SPLObject inplaceConditionalOr(SPLObject rhs) throws SPLInternalException {
    return conditionalOr(rhs);
  }

  SPLObject invert() throws SPLInternalException;

  SPLObject call(SPLObject... args) throws SPLInternalException;

  SPLObject str();

  default Map<SPLObject, SPLObject> getGlobals() {
    return null;
  }

  default void setGlobals(Map<SPLObject, SPLObject> globals) {
    return;
  }
}
