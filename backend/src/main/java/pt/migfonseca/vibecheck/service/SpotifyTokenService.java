package pt.migfonseca.vibecheck.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetSeveralArtistsRequest;

@Service
public class SpotifyTokenService {

    @Value("${env.spotify.id}")
    private String CLIENT_ID;

    @Value("${env.spotify.secret}")
    private String CLIENT_SECRET;

    private SpotifyApi spotifyApi;
    private Instant tokenExpiryTime;

    private void initializeSpotifyApi() throws IOException, SpotifyWebApiException, ParseException {
        if (spotifyApi == null) {
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(CLIENT_ID)
                    .setClientSecret(CLIENT_SECRET)
                    .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8081/callback"))
                    .build();
            generateToken();
        } else if (tokenExpired()) {
            generateToken();
        }
    }

    private boolean tokenExpired() {
        return spotifyApi == null || spotifyApi.getAccessToken() == null || Instant.now().isAfter(tokenExpiryTime);
    }

    private void generateToken() throws IOException, SpotifyWebApiException, ParseException {
        System.out.println("Generating token...");
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        ClientCredentials clientCredentials = clientCredentialsRequest.execute();
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        tokenExpiryTime = Instant.now().plusSeconds(clientCredentials.getExpiresIn());
        System.out.println("Token generated: " + clientCredentials.getAccessToken());
    }

    public Artist searchArtist(String artistName) throws IOException, SpotifyWebApiException, ParseException {
        initializeSpotifyApi();
        delay();
        System.out.println("Sending request to search artist: " + artistName);
        Paging<Artist> artistPaging = spotifyApi.searchArtists(artistName).limit(1).build().execute();
        return artistPaging.getItems()[0];
    }

    public List<Artist> getArtistsFull(List<String> artistSpotifyIds) throws IOException, SpotifyWebApiException, ParseException {
        initializeSpotifyApi();
        if (artistSpotifyIds == null || artistSpotifyIds.isEmpty()) {
            return new ArrayList<>();
        }
        delay();
        System.out.println("Sending request to get full details for artists: " + String.join(", ", artistSpotifyIds));
        GetSeveralArtistsRequest getSeveralArtistsRequest = spotifyApi.getSeveralArtists(artistSpotifyIds.toArray(new String[0])).build();
        return List.of(getSeveralArtistsRequest.execute());
    }

    public List<Album> getArtistAlbums(String spotifyId) throws IOException, SpotifyWebApiException, ParseException {
        initializeSpotifyApi();
        delay();
        System.out.println("Sending request to get albums for artist ID: " + spotifyId);
        List<Album> fullAlbums = new ArrayList<>();
        List<String> albumIds = new ArrayList<>();

        int offset = 0;
        int limit = 50;
        boolean moreAlbums = true;

        while (moreAlbums) {
            GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(spotifyId)
                    .album_type("album,single,appears_on")
                    .limit(limit)
                    .offset(offset)
                    .build();

            Paging<AlbumSimplified> albumSimplifiedPaging = getArtistsAlbumsRequest.execute();
            for (AlbumSimplified albumSimplified : albumSimplifiedPaging.getItems()) {
                if (albumSimplified.getAlbumType().type.equals("compilation"))
                    continue;
                albumIds.add(albumSimplified.getId());
            }

            if (albumSimplifiedPaging.getItems().length < limit) {
                moreAlbums = false;
            } else {
                offset += limit;
            }
        }

        int batchSize = 20;
        for (int i = 0; i < albumIds.size(); i += batchSize) {
            delay();
            List<String> batchIds = albumIds.subList(i, Math.min(i + batchSize, albumIds.size()));
            System.out.println("Sending request to get full details for albums batch: " + batchIds);
            Album[] albums = spotifyApi.getSeveralAlbums(batchIds.toArray(new String[0])).build().execute();
            fullAlbums.addAll(List.of(albums));
        }

        return fullAlbums;
    }

    public List<Track> getAlbumTrackObjects(String albumId) throws IOException, SpotifyWebApiException, ParseException {
        initializeSpotifyApi();
        delay();
        System.out.println("Sending request to get tracks for album ID: " + albumId);
        List<Track> allTracks = new ArrayList<>();
        List<String> trackIds = new ArrayList<>();
    
        int offset = 0;
        int limit = 50;
        boolean moreTracks = true;
    
        while (moreTracks) {
            Paging<TrackSimplified> albumTracksPaging = spotifyApi.getAlbumsTracks(albumId)
                    .limit(limit)
                    .offset(offset)
                    .build()
                    .execute();
    
            for (TrackSimplified track : albumTracksPaging.getItems()) {
                trackIds.add(track.getId());
            }
    
            if (albumTracksPaging.getItems().length < limit) {
                moreTracks = false;
            } else {
                offset += limit;
            }
        }
    
        int batchSize = 50;
        for (int i = 0; i < trackIds.size(); i += batchSize) {
            delay();
            List<String> batchIds = trackIds.subList(i, Math.min(i + batchSize, trackIds.size()));
            System.out.println("Sending request to get full details for tracks batch: " + batchIds);
            Track[] tracks = spotifyApi.getSeveralTracks(batchIds.toArray(new String[0])).build().execute();
            allTracks.addAll(List.of(tracks));
        }
    
        return allTracks;
    }

    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted, Failed to complete operation");
        }
    }
}
