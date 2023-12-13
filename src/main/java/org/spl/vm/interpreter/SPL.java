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
  private DefaultEval frame;

  public SPL(String filename) throws SPLSyntaxError, IOException {
    this.filename = filename;
    SPLCompiler compiler = new SPLCompiler(getResource(filename));
    SPLCodeObject code = compiler.compile();
    this.code = code;
    try {
      this.frame = new DefaultEval(code);
    } catch (SPLInternalException ignore) {
    }
  }

  public static String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    assert resource != null;
    return resource.getPath();
  }

  public SPLObject run() {
    try {
      return this.frame.evalFrame();
    } catch (SPLInternalException ignore) {
      printStackTrace();
    }
    return null;
  }

  private void printStackTrace() {
    // make content below printed in red
    System.err.print("\033[31m");
    System.err.println("Traceback (most recent call last):");
    ThreadState ts = ThreadState.get();
    SPLTraceBackObject trace = ts.getTrace();
    assert trace != null;
    while (trace != null) {
      System.err.println(trace.getErrorMessage());
      trace = trace.getNext();
    }
    System.err.println(ts.getExecVal().getType().getName() + ":" + ts.getExecVal().getMsg());
    System.err.print("\033[0m");
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

  public String getFilename() {
    return filename;
  }
}
