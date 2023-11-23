package org.spl.vm.objects;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLException;
import org.spl.vm.types.SPLCallType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SPLCallObject extends SPLObject {

  private final Method method;
  private final boolean isStatic;

  public SPLCallObject(Method method, boolean isStatic) {
    super(SPLCallType.getInstance());
    this.method = method;
    this.isStatic = isStatic;
  }

  @Override
  public SPLObject call(SPLObject... args) throws SPLInternalException {
    try {
      if (isStatic) {
        return (SPLObject) method.invoke(null, (Object) args);
      } else {
        SPLObject[] newArgs = new SPLObject[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        return (SPLObject) method.invoke(args[0], (Object) newArgs);
      }
    } catch (InvocationTargetException | IllegalAccessException e) {
      return SPLErrorUtils.splErrorFormat(new SPLException(e.getMessage()));
    }
  }
}
