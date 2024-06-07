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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class SpotifyTokenService {

    @Value("${env.spotify.id}")
    private String CLIENT_ID;
    @Value("${env.spotify.secret}")
    private String CLIENT_SECRET;
    private String accessToken = null;
    private int expiresIn = 0;
    private LocalDateTime generatedDate = LocalDateTime.now();

    private JsonNode sendRequest(String urlString, String method, Map<String, String> headers, String content) throws IOException {
        try {
            // Add a delay before each request
            Thread.sleep(750); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset interrupted status
            throw new IOException("Thread was interrupted during sleep", e);
        }
    
        URL url = new URL(urlString);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        if ("POST".equals(method)) {
            http.setDoOutput(true);
        }
        for (var header : headers.entrySet()) {
            http.setRequestProperty(header.getKey(), header.getValue());
        }
        if ("POST".equals(method) && content != null) {
            try (OutputStream stream = http.getOutputStream()) {
                stream.write(content.getBytes(StandardCharsets.UTF_8));
            }
        }
        System.out.println(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.toString());
        } catch (IOException e) {
            if (invalidToken()) {
                generateToken();
                return sendRequest(urlString, method, headers, content); // Retry the request with a new token
            } else {
                throw e;
            }
        } finally {
            http.disconnect();
        }
    }
    


    private synchronized void generateToken() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        this.generatedDate = LocalDateTime.now();

        String content = "grant_type=client_credentials&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;
        JsonNode jsonObject = sendRequest("https://accounts.spotify.com/api/token", "POST", headers, content);
        
        if (jsonObject != null) {
            this.accessToken = jsonObject.get("access_token").asText();
            this.expiresIn = jsonObject.get("expires_in").asInt();
        } else {
            throw new IOException("Failed to generate access token");
        }
    }

    private boolean invalidToken() {
        return accessToken == null || LocalDateTime.now().isAfter(generatedDate.plusSeconds(expiresIn));
    }

    public JsonNode searchArtist(String artistName) throws IOException {
        if (invalidToken()) generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        JsonNode searchJsonNode = sendRequest(
            "https://api.spotify.com/v1/search?q=" + URLEncoder.encode(artistName, StandardCharsets.UTF_8.toString()) + "&type=artist&limit=1",
            "GET", headers, null
        );
        String artistId = searchJsonNode.get("artists").get("items").get(0).get("id").asText();
        return sendRequest("https://api.spotify.com/v1/artists/" + artistId, "GET", headers, null);
    }

    public JsonNode getArtistFull(String artistSpotifyId) throws IOException {
        if (invalidToken()) generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        return sendRequest("https://api.spotify.com/v1/artists/" + artistSpotifyId, "GET", headers, null);
    }

    public Set<JsonNode> getArtistsFull(Set<String> artistSpotifyIds) throws IOException {
        if (invalidToken()) generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        Set<JsonNode> artistJsonNodes = new HashSet<>();

        if (artistSpotifyIds.isEmpty()) return artistJsonNodes;
        String artistIdsParam = String.join(",", artistSpotifyIds);

        String url = "https://api.spotify.com/v1/artists?ids=" + artistIdsParam;
        JsonNode responseJsonNode = sendRequest(url, "GET", headers, null);
        responseJsonNode.get("artists").forEach(artistJsonNodes::add);

        return artistJsonNodes;
    }

    public JsonNode getArtistAlbums(String spotifyId) throws IOException {
        if (invalidToken()) generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        List<JsonNode> allAlbums = new ArrayList<>();
        int limit = 20;
        int offset = 0;
        JsonNode albumsJsonNode;

        do {
            albumsJsonNode = sendRequest("https://api.spotify.com/v1/artists/" + spotifyId + "/albums?include_groups=album,appears_on,single&limit=" + limit + "&offset=" + offset,
                "GET", headers, null);

            for (JsonNode albumJsonNode : albumsJsonNode.get("items")) {
                if (!"compilation".equals(albumJsonNode.get("album_type").asText())) {
                    allAlbums.add(albumJsonNode);
                }
            }

            offset += limit;
        } while (albumsJsonNode.get("items").size() == limit);

        List<String> ids = new ArrayList<>();
        for (JsonNode albumJsonNode : allAlbums) {
            ids.add(albumJsonNode.get("id").asText());
        }

        List<JsonNode> allAlbumDetails = new ArrayList<>();
        int batchSize = 20;

        for (int i = 0; i < ids.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ids.size());
            String batchIds = String.join(",", ids.subList(i, end));

            JsonNode batchAlbumsJsonNode = sendRequest("https://api.spotify.com/v1/albums?ids=" + batchIds, "GET", headers, null);
            for (JsonNode album : batchAlbumsJsonNode.get("albums")) {
                allAlbumDetails.add(album);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode finalArrayNode = mapper.createArrayNode();
        finalArrayNode.addAll(allAlbumDetails);

        return finalArrayNode;
    }

    public List<String> getAlbumTracks(String albumId) throws IOException {
        if (invalidToken()) generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        String baseUrl = "https://api.spotify.com/v1/albums/" + albumId + "/tracks?limit=50";
        int offset = 0;
        boolean moreTracks = true;

        List<String> allTrackIds = new ArrayList<>();

        while (moreTracks) {
            String urlString = baseUrl + "&offset=" + offset;
            JsonNode albumTracksJsonNode = sendRequest(urlString, "GET", headers, null);

            JsonNode items = albumTracksJsonNode.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    allTrackIds.add(item.get("id").asText());
                }
            }

            JsonNode next = albumTracksJsonNode.get("next");
            moreTracks = next != null && !next.isNull();
            offset += 50;
        }

        return allTrackIds;
    }

    public JsonNode getAlbumTrackObjects(String albumId) throws IOException {
        if (invalidToken()) generateToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        List<String> trackIds = getAlbumTracks(albumId);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode allTrackObjects = objectMapper.createArrayNode();

        int batchSize = 50;
        for (int i = 0; i < trackIds.size(); i += batchSize) {
            List<String> batchIds = trackIds.subList(i, Math.min(i + batchSize, trackIds.size()));
            String idsParam = String.join(",", batchIds);

            String urlString = "https://api.spotify.com/v1/tracks?ids=" + idsParam;
            JsonNode trackObjectsJsonNode = sendRequest(urlString, "GET", headers, null);

            JsonNode tracks = trackObjectsJsonNode.get("tracks");
            if (tracks != null && tracks.isArray()) {
                for (JsonNode track : tracks) {
                    allTrackObjects.add(track);
                }
            }
        }

        return allTrackObjects;
    }
}
