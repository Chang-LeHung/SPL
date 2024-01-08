package org.spl.vm.impsys;

import org.spl.vm.exceptions.SPLErrorUtils;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.exceptions.splexceptions.SPLImportError;
import org.spl.vm.interfaces.SPLModuleInterface;
import org.spl.vm.objects.SPLModuleObject;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SPLLoader {

  private final List<String> paths;
  private final String STLIB_NAME = "org/spl/vm/stlib";

  private final ClassLoader loader;

  public static final String main = "ModuleMain";

  public SPLLoader() {
    this.paths = new ArrayList<>();
    loader = new SPLClassFileLoader();
  }

  public void addNewPath(String path) {
    paths.add(path);
  }

  private SPLModuleObject searchSTLib(String name) {
    Path path = Paths.get(STLIB_NAME, name, "ModuleMain");
    try {
      Class<?> clazz = loader.loadClass(path + ".class");
      if (SPLModuleObject.class.isAssignableFrom(clazz) && SPLModuleInterface.class.isAssignableFrom(clazz)) {
        return (SPLModuleObject) clazz.getConstructor().newInstance();
      }
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
             InvocationTargetException e) {
    }
    return null;
  }

  public SPLModuleObject load(String moduleName) throws SPLInternalException {
    SPLModuleObject res = searchSTLib(moduleName);
    if (res != null) return res;
    SPLErrorUtils.splErrorFormat(new SPLImportError("Cannot find module \"" + moduleName + "\""));
    return null;
  }

  public static void main(String[] args) throws SPLInternalException, ClassNotFoundException {
    SPLLoader loader = new SPLLoader();
    System.out.println(loader.searchSTLib("vm"));
  }
}
