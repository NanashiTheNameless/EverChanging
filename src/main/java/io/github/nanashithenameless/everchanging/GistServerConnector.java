package io.github.nanashithenameless.everchanging;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GistServerConnector implements ClientModInitializer {

    private static  final String GITHUB_API_BASE_URL = "https://api.github.com";

    @Override
    public void onInitializeClient() {

    }

    public static String fetchServerIpFromGist(String gistStr) {
        try
        {
            String gistContent;
            if(gistStr.startsWith(GITHUB_API_BASE_URL + "/gists/"))
                gistContent = fetchGistContentApi(gistStr, false);
            else if(gistStr.startsWith("https://gist.github.com/"))
                gistContent = fetchGistContentDirect(gistStr);
            else if(gistStr.startsWith("raw:"))
                gistContent = fetchGistContentApi(gistStr.substring(4), true);
            else
                gistContent = "Incorrect format!";

            return parseServerIpFromGistContent(gistContent); //return ipAddress
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching gist content!";
        }
    }

    private static String fetchGistContentApi(String gistStr, boolean rawId) throws IOException {
        if(gistStr.isEmpty())
            return "Error fetching gist content!";
        String apiUrl;
        if(rawId)
            apiUrl = GITHUB_API_BASE_URL + "/gists/" + gistStr;
        else
            apiUrl = gistStr;

        URI uri = URI.create(apiUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("Authorization", "token " + GITHUB_TOKEN);

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                content.append(responseLine);
            }
        } finally {
            connection.disconnect();
        }
        // Parse the JSON response to extract the gist content
        return content.toString();
    }

    private static String fetchGistContentDirect(String gistUrl) throws IOException
    {
        String[] cells = gistUrl.split("/");

        return fetchGistContentApi(cells[4], true);
    }

    private static String parseServerIpFromGistContent(String content) {
        JsonObject gistJson = JsonParser.parseString(content).getAsJsonObject();
        JsonObject files = gistJson.getAsJsonObject("files");

        String fileName = files.keySet().iterator().next(); // Get the first file name
        JsonObject file = files.getAsJsonObject(fileName);

        return file.get("content").getAsString();
    }
}
