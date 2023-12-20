package org.spl.compiler.visualizer;

import org.junit.jupiter.api.Test;
import org.spl.compiler.SPLCompiler;
import org.spl.compiler.exceptions.SPLSyntaxError;
import org.spl.compiler.ir.ASTVisualizer;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VisualizerTest {

  public String getResource(String filename) {
    URL resource = Thread.currentThread().
        getContextClassLoader().
        getResource(filename);
    assert resource != null;
    return resource.getPath();
  }

  public void saveStringToFile(String content, String filename) throws IOException {
    File file = new File(filename);
    if (!file.exists()) {
      if (!file.getParentFile().exists())
        file.getParentFile().mkdirs();
      file.createNewFile();
    }
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
      writer.write(content);
    }
  }

  @Test
  public void testVisualizer() throws SPLSyntaxError, IOException {
    SPLCompiler compiler = new SPLCompiler(getResource("datastruct/dict03.spl"));
    compiler.compile();
    ASTVisualizer vis = new ASTVisualizer(compiler.getIr(), "add.spl");
    String content = vis.getDotFileContent();
    System.out.println(content);
    saveStringToFile(content, "dots/add.dot");
  }
}
