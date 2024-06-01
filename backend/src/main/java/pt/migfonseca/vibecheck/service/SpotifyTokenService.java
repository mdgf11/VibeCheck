package pt.migfonseca.vibecheck.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SpotifyTokenService {

    private String CLIENT_ID = "be0c1a2e2401445a8cb2e6b11bae9429";
    private String CLIENT_SECRET = "690b188640e4489391caf3c889e3b107";
    private String accessToken = null;
    private int expiresIn = 0;
    private LocalDateTime generatedDate = LocalDateTime.now();

    public JsonNode generateRequest(String urlString, String method, Map<String, String> headers, String content) throws IOException {
        URL url = new URL(urlString);
        try {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            System.out.println(url.toString());
            http.setRequestMethod(method);
            if (method.equals("POST"))
                http.setDoOutput(true);
            for (var header : headers.entrySet())
                http.setRequestProperty(header.getKey(), header.getValue());
            if (method.equals("POST")) {
                String data = content;
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = http.getOutputStream();
                stream.write(out);
            }

            BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String currentLine = Lines.readLine();
            StringBuilder response = new StringBuilder();
            while (currentLine != null) {
                response.append(currentLine).append("\n");
                currentLine = Lines.readLine();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonObject = mapper.readTree(response.toString());
            http.disconnect();
            return jsonObject;

        } catch (IOException ioException) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (invalidToken())
                generateToken();
            System.err.println(ioException.getLocalizedMessage());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            System.out.println(url.toString());
            http.setRequestMethod(method);
            if (method.equals("POST"))
                http.setDoOutput(true);
            for (var header : headers.entrySet())
                http.setRequestProperty(header.getKey(), header.getValue());
            if (method.equals("POST")) {
                String data = content;
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = http.getOutputStream();
                stream.write(out);
            }

            BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String currentLine = Lines.readLine();
            StringBuilder response = new StringBuilder();
            while (currentLine != null) {
                response.append(currentLine).append("\n");
                currentLine = Lines.readLine();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonObject = mapper.readTree(response.toString());
            http.disconnect();
            return jsonObject;

        }
    }

    public void generateToken() throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        this.generatedDate = LocalDateTime.now();
        JsonNode jsonObject = generateRequest("https://accounts.spotify.com/api/token",
            "POST",
            headers,
            "grant_type=client_credentials&client_id=" +
                CLIENT_ID +
                "&client_secret=" +
                CLIENT_SECRET +
                "");
        this.accessToken = jsonObject.get("access_token").asText();
        this.expiresIn = Integer.parseInt(jsonObject.get("expires_in").asText());
    }

    public boolean invalidToken() {
        return accessToken == null || expiresIn < this.generatedDate.until(LocalDateTime.now(), ChronoUnit.SECONDS);
    }

    public JsonNode searchArtist(String artistName) throws IOException {
        if (invalidToken())
            generateToken();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + accessToken);
        JsonNode searchJsonNode = generateRequest("https://api.spotify.com/v1/search?q=" + 
                URLEncoder.encode(artistName, StandardCharsets.UTF_8.toString()) + 
                "&type=artist&limit=1",
            "GET",
            headers, "");
        String artistId = searchJsonNode
            .get("artists")
            .get("items")
            .get(0)
            .get("id")
            .asText();
        JsonNode artistJsonNode = generateRequest("https://api.spotify.com/v1/artists/" +
            artistId ,
            "GET",
            headers, "");
        
        return artistJsonNode;
    }

    public JsonNode getArtistFull(String artistSpotifyId) throws IOException {
        if (invalidToken())
            generateToken();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + accessToken);
        JsonNode artistJsonNode = generateRequest("https://api.spotify.com/v1/artists/" +
            artistSpotifyId ,
            "GET",
            headers, "");
        
        return artistJsonNode;
    }

    public Set<JsonNode> getArtistsFull(Set<String> artistSpotifyIds) throws IOException {
        if (invalidToken())
            generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + accessToken);
        Set<JsonNode> artistJsonNodes = new HashSet<>();

        if (artistSpotifyIds.isEmpty())
            return artistJsonNodes;
        String artistIdsParam = String.join(",", artistSpotifyIds);
    
        String url = "https://api.spotify.com/v1/artists?ids=" + artistIdsParam;
    
        JsonNode responseJsonNode = generateRequest(url, "GET", headers, "");
    
        responseJsonNode.get("artists").forEach(artistJsonNodes::add);
    
        return artistJsonNodes;
    }

    public JsonNode getArtistsAlbums(String spotifyId) throws IOException {
        if (invalidToken())
            generateToken();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + accessToken);
        
        JsonNode albumsJsonNode = generateRequest("https://api.spotify.com/v1/artists/" +
            spotifyId + "/albums?include_groups=album,appears_on" ,
            "GET",
            headers, "");

        return albumsJsonNode.get("items");
    }

    public JsonNode getAlbumTracks(String albumId, int offSet) throws IOException {
        if (invalidToken())
            generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("Authorization", "Bearer " + accessToken);

        // Construct the URL for fetching album tracks
        String urlString = "https://api.spotify.com/v1/albums/" + albumId + "/tracks?offset=" + offSet;

        // Make the request and return the JSON response
        JsonNode albumTracksJsonNode = generateRequest(urlString, "GET", headers, "");
        return albumTracksJsonNode.get("items");    
    }



}