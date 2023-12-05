package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLNotImplemented;
import org.spl.vm.types.SPLCommonType;

public class SPLObject implements SPLInterface {

  private final SPLCommonType type;

  public SPLObject(SPLCommonType type) {
    this.type = type;
  }

  @Override
  public SPLCommonType getType() {
    return type;
  }

  @Override
  public SPLObject add(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '+' not implemented"));
  }

  @Override
  public SPLObject sub(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '-' not implemented"));
  }

  @Override
  public SPLObject mul(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '*' not implemented"));
  }

  @Override
  public SPLObject div(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '/' not implemented"));
  }

  @Override
  public SPLObject trueDiv(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '//' not implemented"));  }

  @Override
  public SPLObject mod(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '%' not implemented"));
  }

  @Override
  public SPLObject pow(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '**' not implemented"));
  }

  @Override
  public SPLObject lshift(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '<<' not implemented"));
  }

  @Override
  public SPLObject URshift(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '>>=' not implemented"));
  }

  @Override
  public SPLObject rshift(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '>>' not implemented"));
  }


  @Override
  public SPLObject and(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '&' not implemented"));
  }

  @Override
  public SPLObject or(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '|' not implemented"));
  }


  @Override
  public SPLObject not() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '~' not implemented"));
  }

  @Override
  public SPLObject xor(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '^' not implemented"));
  }


  @Override
  public SPLObject neg() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '-' not implemented"));
  }

  @Override
  public SPLObject eq(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '==' not implemented"));
  }

  @Override
  public SPLObject ne(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '!=' not implemented"));
  }

  @Override
  public SPLObject lt(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '<' not implemented"));
  }

  @Override
  public SPLObject gt(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '>' not implemented"));
  }

  @Override
  public SPLObject le(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '<=' not implemented"));
  }

  @Override
  public SPLObject ge(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation '>=' not implemented"));
  }

  @Override
  public SPLObject conditionalAnd(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '&&' not implemented"));
  }

  @Override
  public SPLObject conditionalOr(SPLObject rhs) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '||' not implemented"));
  }

  @Override
  public SPLObject invert() throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLNotImplemented("operation '~' not implemented"));
  }

  @Override
  public SPLObject call(SPLObject... args) throws SPLInternalException {
    return SPLErrorUtils
        .splErrorFormat(new SPLNotImplemented("operation 'call' not implemented"));
  }

  @Override
  public SPLObject str() {
    return new SPLStringObject(this.toString());
  }
}
