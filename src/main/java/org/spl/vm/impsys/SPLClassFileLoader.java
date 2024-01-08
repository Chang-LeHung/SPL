package org.spl.vm.impsys;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class SPLClassFileLoader extends ClassLoader {


  public SPLClassFileLoader() {
    super(Thread.currentThread().getContextClassLoader());
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
    if (resource == null)
      return null;
    try (FileInputStream stream = new FileInputStream(resource.getPath())) {
      byte[] code = stream.readAllBytes();
      if (name.charAt(0) == '/')
        name = name.substring(1);
      name = name.replaceAll("/", ".");
      name = name.substring(0, name.lastIndexOf('.'));
      return defineClass(name, code, 0, code.length);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
