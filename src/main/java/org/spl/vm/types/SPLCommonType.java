package org.spl.vm.types;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLRuntimeException;
import org.spl.vm.internal.objs.SPLFuncObject;
import org.spl.vm.objects.SPLCallObject;
import org.spl.vm.objects.SPLObject;

import java.lang.reflect.Method;
import java.util.HashMap;

public class SPLCommonType extends SPLObject {

  protected final String name;
  protected final Class<? extends SPLObject> clazz;
  protected SPLCommonType base;

  public SPLCommonType(SPLCommonType type, String name, Class<? extends SPLObject> clazz) {
    super(type);
    base = SPLObjectType.getInstance();
    this.name = name;
    this.clazz = clazz;
    methods = new HashMap<>();
  }

  public static boolean isExecMatch(SPLObject o1, SPLCommonType o2) {
    if (o1 == null) return false;
    SPLCommonType type = o1.getType();
    while (type != null) {
      if (type == o2)
        return true;
      type = type.getBase();
    }
    return false;
  }

  public SPLCommonType getBase() {
    return base;
  }

  public String getName() {
    return name;
  }

  public Class<? extends SPLObject> getObjectClazz() {
    return clazz;
  }

  @Override
  public String toString() {
    return String.format("<type %s @0x%s>", getName(), Integer.toHexString(hashCode()));
  }

  private SPLObject getFromCache(SPLObject name) {
    if (methods.containsKey(name)) {
      Object method = methods.get(name);
      if (method instanceof Method m) {
        return new SPLCallObject(m, null, true);
      } else if (method instanceof SPLFuncObject func) {
        return func;
      }
    }
    return null;
  }

  @Override
  public SPLObject __getAttr__(SPLObject name) throws SPLInternalException {
    if (attrs != null && attrs.containsKey(name)) {
      return attrs.get(name);
    }
    return __getMethod__(name);
  }

  @Override
  public SPLObject __getMethod__(SPLObject name) throws SPLInternalException {
    if (attrs != null && attrs.containsKey(name)) {
      SPLObject func = attrs.get(name);
      if (func instanceof SPLFuncObject) {
        return func;
      }
    }
    SPLObject res = getFromCache(name);
    if (res != null) return res;
    try {
      Method method = clazz.getMethod(name.toString(), SPLObject[].class);
      if (method.isAnnotationPresent(SPLExportMethod.class) && method.getReturnType().isAssignableFrom(SPLObject.class)) {
        methods.put(name, method);
        return new SPLCallObject(method, null, true);
      }
    } catch (NoSuchMethodException ignore) {
    }
    // check super class only single inheritance allowed in SPL
    if (base != null) { // SPLObjectType's type
      res = base.__getMethod__(name);
      if (res != null) {
        methods.put(name, res);
        return res;
      }
    }
    return SPLErrorUtils.splErrorFormat(new SPLRuntimeException("Not found an attribute or method  named '" + name + "'"));
  }
}
