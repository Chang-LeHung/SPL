package org.spl.vm.objects;

import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.types.SPLCommonType;

import java.util.Map;

public interface SPLInterface {


  SPLCommonType getType();

  SPLObject add(SPLObject rhs) throws SPLInternalException;

  SPLObject sub(SPLObject rhs) throws SPLInternalException;

  SPLObject mul(SPLObject rhs) throws SPLInternalException;

  SPLObject div(SPLObject rhs) throws SPLInternalException;
  SPLObject trueDiv(SPLObject rhs) throws SPLInternalException;

  SPLObject mod(SPLObject rhs) throws SPLInternalException;

  SPLObject pow(SPLObject rhs) throws SPLInternalException;

  SPLObject lshift(SPLObject rhs) throws SPLInternalException;

  SPLObject rshift(SPLObject rhs) throws SPLInternalException;

  SPLObject URshift(SPLObject rhs) throws SPLInternalException;

  SPLObject and(SPLObject rhs) throws SPLInternalException;

  SPLObject or(SPLObject rhs) throws SPLInternalException;

  SPLObject xor(SPLObject rhs) throws SPLInternalException;

  SPLObject not() throws SPLInternalException;

  SPLObject neg() throws SPLInternalException;

  SPLObject eq(SPLObject rhs) throws SPLInternalException;

  SPLObject ne(SPLObject rhs) throws SPLInternalException;

  SPLObject lt(SPLObject rhs) throws SPLInternalException;

  SPLObject gt(SPLObject rhs) throws SPLInternalException;

  SPLObject le(SPLObject rhs) throws SPLInternalException;

  SPLObject ge(SPLObject rhs) throws SPLInternalException;

  SPLObject conditionalAnd(SPLObject rhs) throws SPLInternalException;

  SPLObject conditionalOr(SPLObject rhs) throws SPLInternalException;

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
