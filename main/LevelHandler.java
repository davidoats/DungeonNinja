package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;


public class LevelHandler {

  public static String getLvlData(String levelNum, String key) throws IOException {

    String path = "/res/levels/level_" + levelNum + ".json";

String jsondata = null;

try (java.io.InputStream is = LevelHandler.class.getResourceAsStream(path)) {
    if (is == null) {
        throw new java.io.FileNotFoundException("Could not find resource file: " + path);
    }
    jsondata = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
} catch (java.io.IOException e) {
    e.printStackTrace();
    // Handle error (e.g., load a default level or exit)
}
        
    // Regex pattern to match "key" : "value" or "key" : value
        String regex = "\"" + key + "\"\\s*:\\s*\"?([^,\"}]+)\"?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.compile(regex).matcher(jsondata);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        

        return jsondata; // Key not found

    
        

  }
  
  public void changeLvl(int l) throws IOException {
    getLvlData(null, null);
  }

}
