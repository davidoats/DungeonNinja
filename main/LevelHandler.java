
package main;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LevelHandler {

    // Read and return the entire level JSON
    public static JsonObject getLevel(String levelNum) throws IOException {

        String path = "/res/levels/level_" + levelNum + ".json";

        try (InputStream is = LevelHandler.class.getResourceAsStream(path)) {

            if (is == null) {
                throw new IOException("Could not find level file: " + path);
            }

            InputStreamReader reader = new InputStreamReader(
                    is, StandardCharsets.UTF_8);

            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    // Return the objects array from the JSON
    public static JsonArray getObjects(String levelNum) throws IOException {
        return getLevel(levelNum).getAsJsonArray("Objects");
    }

    // Return the player data from the JSON
    public static JsonObject getPlayer(String levelNum) throws IOException {
        return getLevel(levelNum).getAsJsonObject("Player");
    }
}
