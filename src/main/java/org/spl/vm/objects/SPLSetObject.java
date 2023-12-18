package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLTypeError;
import org.spl.vm.types.SPLSetType;

import java.util.HashSet;
import java.util.Set;

public class SPLSetObject extends SPLObject {
  private final Set<SPLObject> set;

  public SPLSetObject() {
    super(SPLSetType.getInstance());
    set = new HashSet<>();
  }

  public SPLSetObject(Set<SPLObject> set) {
    super(SPLSetType.getInstance());
    this.set = set;
  }

  public Set<SPLObject> getSet() {
    return set;
  }

  @Override
  public SPLObject __add__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      HashSet<SPLObject> res = new HashSet<>();
      res.addAll(o.set);
      res.addAll(set);
      return new SPLSetObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot add " + rhs.getType() + " to a SPLSetObject"));
  }

  @Override
  public SPLObject __sub__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      HashSet<SPLObject> res = new HashSet<>(set);
      o.set.forEach(res::remove);
      return new SPLSetObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot subtract " + rhs.getType() + " from a SPLSetObject"));
  }

  @Override
  public SPLObject __and__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject other) {
      HashSet<SPLObject> res = new HashSet<>();
      for (SPLObject o : set) {
        if (other.set.contains(o)) {
          res.add(o);
        }
      }
      return new SPLSetObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot perform '&' on " + rhs.getType() + " and a SPLSetObject"));
  }

  @Override
  public SPLObject __xor__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject other) {
      HashSet<SPLObject> res = new HashSet<>();
      for (SPLObject o : set) {
        if (!other.set.contains(o)) {
          res.add(o);
        }
      }
      return new SPLSetObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot perform '^' on " + rhs.getType() + " and a SPLSetObject"));
  }

  @Override
  public SPLObject __or__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject other) {
      HashSet<SPLObject> res = new HashSet<>();
      res.addAll(set);
      res.addAll(other.set);
      return new SPLSetObject(res);
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot perform '|' on " + rhs.getType() + " and a SPLSetObject"));
  }

  @Override
  public SPLObject __eq__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      return set.equals(o.set) ? SPLBoolObject.getTrue() : SPLBoolObject.getFalse();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot compare " + rhs.getType() + " and a SPLSetObject"));
  }

  @Override
  public SPLObject __ne__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      return set.equals(o.set) ? SPLBoolObject.getFalse() : SPLBoolObject.getTrue();
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot compare " + rhs.getType() + " and a SPLSetObject"));
  }

  @Override
  public SPLObject __str__() throws SPLInternalException {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    for (SPLObject o : set) {
      builder.append(o.__str__());
      builder.append(", ");
    }
    if (builder.length() > 2) {
      builder.delete(builder.length() - 2, builder.length());
    }
    builder.append("}");
    return new SPLStringObject(builder.toString());
  }

  @Override
  public SPLObject __inplaceAdd__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      set.addAll(o.set);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot add " + rhs.getType() + " to a SPLSetObject"));
  }

  @Override
  public SPLObject __inplaceSub__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      for (SPLObject o1 : o.set) {
        set.remove(o1);
      }
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot subtract " + rhs.getType() + " from a SPLSetObject"));
  }

  @Override
  public SPLObject __inplaceAnd__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      set.retainAll(o.set);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot perform '&' on " + rhs.getType() + " and a SPLSetObject"));
  }

  @Override
  public SPLObject __inplaceOr__(SPLObject rhs) throws SPLInternalException {
    if (rhs instanceof SPLSetObject o) {
      set.addAll(o.set);
      return this;
    }
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot perform '|' on " + rhs.getType() + " and a SPLSetObject"));
  }

  @Override
  public SPLCommonIterator __getIterator__() throws SPLInternalException {
    return new SPLCommonIterator(set.stream().toList());
  }

  @Override
  public SPLObject __setAttr__(SPLObject name, SPLObject value) throws SPLInternalException {
    return SPLErrorUtils.splErrorFormat(new SPLTypeError("Cannot set attribute on a 'set' object"));
  }
}
