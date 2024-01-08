package org.spl.vm.stlib.vm;

import org.spl.vm.annotations.SPLExportMethod;
import org.spl.vm.interfaces.SPLModuleInterface;
import org.spl.vm.objects.SPLModuleObject;
import org.spl.vm.objects.SPLNoneObject;
import org.spl.vm.objects.SPLObject;

public class ModuleMain extends SPLModuleObject implements SPLModuleInterface {
  public ModuleMain() {
    super("vm");
  }

  @Override
  public void init() {

  }

  @Override
  public void destroy() {

  }

  @SPLExportMethod
  public SPLObject spawn(SPLObject... args) {
    return SPLNoneObject.getInstance();
  }
}
