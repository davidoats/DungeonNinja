package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LevelHandler {

    public static Map<String, String> getAllLvlData(String levelNum) throws IOException {

        String path = "/res/levels/level_" + levelNum + ".json";

        String jsondata;

        try (java.io.InputStream is = LevelHandler.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new java.io.FileNotFoundException("Could not find: " + path);
            }

            jsondata = new String(
                is.readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8
            );
        }

        Map<String, String> data = new HashMap<>();

        Pattern pattern = Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|(-?\\d+))"
        );

        Matcher matcher = pattern.matcher(jsondata);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2) != null
                ? matcher.group(2)
                : matcher.group(3);

            data.put(key, value);
        }

        return data;
    }

    public static String getLvlData(String levelNum, String key) throws IOException {
        return getAllLvlData(levelNum).get(key);
    }
}