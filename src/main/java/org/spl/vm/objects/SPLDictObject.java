package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.types.SPLDictType;

import java.util.HashMap;
import java.util.Map;

public class SPLDictObject extends SPLObject {

  private final Map<SPLObject, SPLObject> dict;

  public SPLDictObject() {
    super(SPLDictType.getInstance());
    dict = new HashMap<>();
  }

  public SPLDictObject(Map<SPLObject, SPLObject> dict) {
    super(SPLDictType.getInstance());
    this.dict = dict;
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      HashMap<SPLObject, SPLObject> res = new HashMap<>(dict);
      res.putAll(other.dict);
      return new SPLDictObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot add " + rhs.getType() + " to " + getType()));
  }

  @Override
  public SPLObject __sub__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      HashMap<SPLObject, SPLObject> map = new HashMap<>(dict);
      other.dict.keySet().forEach(map::remove);
      return new SPLDictObject(map);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot subtract " + rhs.getType() + " from " + getType()));
  }

  @Override
  public SPLObject __and__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      return new SPLDictObject(dict.keySet().stream().filter(other.dict::containsKey).collect(HashMap::new, (m, k) -> m.put(k, dict.get(k)), HashMap::putAll));
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot and " + rhs.getType() + " with " + getType()));
  }

  @Override
  public SPLObject __or__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      HashMap<SPLObject, SPLObject> map = new HashMap<>();
      map.putAll(dict);
      map.putAll(other.dict);
      return new SPLDictObject(map);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot or " + rhs.getType() + " with " + getType()));
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      return dict.equals(other.dict) ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot compare " + rhs.getType() + " with " + getType()));
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      return dict.equals(other.dict) ? SPLBoolObject.getFalse() : SPLBoolObject.getTrue();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot compare " + rhs.getType() + " with " + getType()));
  }

  @Override
  public SPLObject __inplaceAdd__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      dict.putAll(other.dict);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot add " + rhs.getType() + " to " + getType()));
  }

  @Override
  public SPLObject inplaceSub(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      other.dict.keySet().forEach(dict::remove);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot subtract " + rhs.getType() + " from " + getType()));
  }

  @Override
  public SPLObject __inplaceAnd__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      other.dict.keySet().forEach(dict::remove);
      return this;
    } else if (rhs instanceof SPLSetObject other) {
      other.getSet().forEach(dict::remove);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot and " + rhs.getType() + " with " + getType()));
  }

  @Override
  public SPLObject __inplaceOr__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLDictObject other) {
      dict.putAll(other.dict);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot or " + rhs.getType() + " with " + getType()));
  }


  @Override
  public SPLObject __str__() {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    dict.forEach((x, y) -> {
      builder.append(x)
          .append(":")
          .append(y)
          .append(",");
    });
    builder.delete(builder.length() - 1, builder.length());
    return new SPLStringObject(builder.toString());
  }
}
