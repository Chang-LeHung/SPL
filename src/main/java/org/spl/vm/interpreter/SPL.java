package org.spl.vm.interpreter;

import org.spl.compiler.SPLCompiler;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.vm.exceptions.jexceptions.SPLInternalException;
import org.spl.vm.internal.objs.SPLCodeObject;
import org.spl.vm.internal.objs.SPLFrameObject;
import org.spl.vm.internal.utils.Dissembler;
import org.spl.vm.objects.SPLObject;

import java.io.IOException;
import java.net.URL;

public class SPL {

  static {
    Evaluation.init();
  }

  private final String filename;
  private final SPLCodeObject code;
  private final DefaultEval frame;

  public SPL(String filename) throws SPLSyntaxError, IOException, SPLInternalException {
    this.filename = filename;
    SPLCompiler compiler = new SPLCompiler(getResource(filename));
    SPLCodeObject code = compiler.compile();
    this.code = code;
    this.frame = new DefaultEval(code);
  }

  public static String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    assert resource != null;
    return resource.getPath();
  }

  public SPLObject run() throws SPLSyntaxError, IOException, SPLInternalException {
    return this.frame.evalFrame();
  }

  public SPLCodeObject getCode() {
    return code;
  }


  public SPLFrameObject getFrame() {
    return frame;
  }


  public void dis() {
    Dissembler dissembler = new Dissembler(code);
    dissembler.prettyPrint();
  }
}
