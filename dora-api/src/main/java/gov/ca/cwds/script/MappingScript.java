package gov.ca.cwds.script;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 * Base class for execution mappings written with groovy scripts
 *
 * @author CWDS TPT-2
 */
public class MappingScript {
  private String[] variableNames;
  private String script;
  private ScriptEngine scriptEngine;

  /**
   *
   * @param filePath script file location taken from property file
   * @param variableNames script variableNames
   * @throws IOException when can't read the mapping script
   */
  @SuppressFBWarnings("PATH_TRAVERSAL_IN")
  public MappingScript(String filePath, String... variableNames) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(filePath));
    script = new String(bytes, StandardCharsets.UTF_8);
    this.variableNames = variableNames;
    int dotIndex = filePath.lastIndexOf('.');
    String fileExtension = dotIndex == -1 ? "" : filePath.substring(dotIndex + 1);
    ScriptEngineManager factory = new ScriptEngineManager();
    scriptEngine = factory.getEngineByName(fileExtension);
  }

  public Object eval(Object... objects) throws ScriptException {
    ScriptContext scriptContext = new SimpleScriptContext();
    IntStream.range(0, variableNames.length)
        .forEach(i -> scriptContext.setAttribute(variableNames[i], objects[i], ScriptContext.ENGINE_SCOPE));
    return scriptEngine.eval(script, scriptContext);
  }
}
