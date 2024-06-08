<template>
  <div class="playlist-view">
    <HeaderComponent />
    <div class="playlist-header" v-if="playlist && playlist.songs.length > 0">
      <div class="playlist-cover-container">
        <img :src="getCoverArtUrl(playlist.songs[0])" class="playlist-cover" />
      </div>
      <div class="playlist-info">
        <h1 class="playlist-title">{{ playlist.title }}</h1>
        <div class="playlist-buttons">
          <div class="playlist-artists">
            <span class="section-title">Featuring:</span>
            <PlaylistButtonComponent 
              v-for="(artist, index) in getTopArtists()" 
              :key="'artist' + index" 
              :text="artist" 
              queryType="artist" 
            />
          </div>
          <div class="playlist-genres">
            <span class="section-title">Genres:</span>
            <PlaylistButtonComponent 
              v-for="(genre, index) in getTopGenres()" 
              :key="'genre' + index" 
              :text="genre" 
              queryType="genre" 
            />
          </div>
          <div class="playlist-vibes">
            <span class="section-title">Vibes:</span>
            <PlaylistButtonComponent 
              v-for="(vibe, index) in getTopVibes()" 
              :key="'vibe' + index" 
              :text="vibe" 
              queryType="vibe" 
            />
          </div>
        </div>
      </div>
    </div>
    <div class="playlist-container">
      <div v-if="playlist">
        <transition-group name="song-list" tag="div">
          <SongItem
            v-for="song in playlist.songs"
            :key="song.id"
            :song="song"
          />
        </transition-group>
      </div>
      <div v-else class="no-playlist">
        <p>No playlist available</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";
import usePlaylistStore from "@/stores/playlistStore";
import HeaderComponent from "@/components/HeaderComponent.vue";
import SongItem from "@/components/SongItem.vue";
import PlaylistButtonComponent from "@/components/PlaylistButtonComponent.vue";
import { Song } from "@/types/Song";

export default defineComponent({
  name: "PlaylistView",
  components: {
    HeaderComponent,
    SongItem,
    PlaylistButtonComponent,
  },
  setup() {
    const playlistStore = usePlaylistStore();
    const playlist = computed(() => playlistStore.getPlaylist);

    const getCoverArtUrl = (song: Song) => {
      if (window.innerWidth <= 480) {
        return song.images.get(64);
      } else if (window.innerWidth <= 768) {
        return song.images.get(300);
      } else {
        return song.images.get(640);
      }
    };

    const getTopArtists = () => {
      if (!playlist.value) return [];
      const artists = new Set<string>();
      playlist.value.songs.forEach(song => {
        song.artists.forEach(artist => {
          artists.add(artist);
        });
      });
      return Array.from(artists).slice(0, 5);
    };

    const getTopGenres = () => {
      if (!playlist.value) return [];
      const genres = new Set<string>();
      playlist.value.songs.forEach(song => {
        song.genres.forEach(genre => {
          genres.add(genre);
        });
      });
      return Array.from(genres).slice(0, 5);
    };

    const getTopVibes = () => {
      if (!playlist.value) return [];
      const vibes = new Set<string>();
      playlist.value.songs.forEach(song => {
        song.vibes.forEach(vibe => {
          vibes.add(vibe);
        });
      });
      return Array.from(vibes).slice(0, 5);
    };

    return {
      playlist,
      getCoverArtUrl,
      getTopArtists,
      getTopGenres,
      getTopVibes,
    };
  },
});
</script>

<style scoped>
.song-list-enter-active, .song-list-leave-active, .song-list-move {
  transition: all 0.5s;
}
.song-list-enter, .song-list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

.playlist-view {
  position: relative;
  width: 100vw;
  height: 100vh;
  background-color: #bfbebe;
  background-image: linear-gradient(to left, #2B4570, #A37A74);
  background-size: cover;
  background-position: center;
  color: white;
  cursor: default;
  overflow-y: auto;
}

.playlist-header {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  margin: 20px;
}

.playlist-cover-container {
  width: 30%;
  display: flex;
  justify-content: center;
}

.playlist-cover {
  width: 100%;
  max-width: 150px;
  height: auto;
  border-radius: 10px;
}

.playlist-info {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 60%;
  margin-left: 20px;
}

.playlist-title {
  font-size: 24px;
  margin: 0;
}

.playlist-buttons {
  margin: 10px 0 0 0;
  font-size: 14px;
  display: flex;
  flex-direction: column;
}

.section-title {
  font-size: 10px; /* Adjusted to make the section titles smaller */
  font-weight: bold;
  margin-right: 5px;
}

.playlist-artists,
.playlist-genres,
.playlist-vibes {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-top: 5px;
}

.button-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  margin: 20px;
}

.playlist-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.song-item {
  margin: 10px 0;
}

.no-playlist {
  text-align: center;
  margin-top: 20px;
  font-size: 20px;
}

/* Media Queries */
@media (max-width: 1200px) {
  .playlist-header {
    flex-direction: row;
    align-items: center;
  }
  .playlist-cover-container {
    width: 30%;
  }
  .playlist-info {
    width: 70%;
    margin-left: 20px;
  }
  .playlist-title {
    font-size: 22px;
  }
  .playlist-buttons {
    font-size: 12px;
  }
}

@media (max-width: 1024px) {
  .playlist-header {
    flex-direction: row;
    align-items: center;
  }
  .playlist-cover-container {
    width: 40%;
  }
  .playlist-info {
    width: 60%;
    margin-left: 20px;
  }
  .playlist-title {
    font-size: 20px;
  }
  .playlist-buttons {
    font-size: 12px;
  }
}

@media (max-width: 900px) {
  .playlist-header {
    flex-direction: row;
    align-items: center;
  }
  .playlist-cover-container {
    width: 50%;
  }
  .playlist-info {
    width: 50%;
    margin-left: 20px;
  }
  .playlist-title {
    font-size: 18px;
  }
  .playlist-buttons {
    font-size: 10px;
  }
}

@media (max-width: 768px) {
  .playlist-header {
    flex-direction: column;
    align-items: center;
  }
  .playlist-cover-container {
    width: 60%;
  }
  .playlist-info {
    width: 90%;
    margin-left: 0;
    text-align: center;
  }
  .playlist-title {
    font-size: 16px;
  }
  .playlist-buttons {
    font-size: 10px;
    align-items: center;
  }
  .playlist-artists,
  .playlist-genres,
  .playlist-vibes {
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .playlist-header {
    flex-direction: column;
    align-items: center;
  }
  .playlist-cover-container {
    width: 70%;
  }
  .playlist-info {
    width: 100%;
    margin-left: 0;
    text-align: center;
  }
  .playlist-title {
    font-size: 14px;
  }
  .playlist-buttons {
    font-size: 8px;
    align-items: center;
  }
  .playlist-artists,
  .playlist-genres,
  .playlist-vibes {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .playlist-header {
    flex-direction: column;
    align-items: center;
  }
  .playlist-cover-container {
    width: 90%;
  }
  .playlist-info {
    width: 100%;
    margin-left: 0;
    text-align: center;
  }
  .playlist-title {
    font-size: 12px;
  }
  .playlist-buttons {
    font-size: 8px;
    align-items: center;
  }
  .playlist-artists,
  .playlist-genres,
  .playlist-vibes {
    justify-content: center;
  }
}
</style>
