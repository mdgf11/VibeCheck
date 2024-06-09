<template>
  <div class="playlist-view">
    <HeaderComponent />
    <div class="playlist-header" v-if="playlist && playlist.songs.length > 0">
      <div class="playlist-cover-container">
        <img :src="getCoverArtUrl(playlist.songs[0])" class="playlist-cover" />
      </div>
      <div class="playlist-info">
        <h1 class="playlist-title">
          {{ playlist.title }}
          <span class="playlist-icons">
            <img :src="savePlaylistLogo" class="heart-icon" @click="addToAccount" />
            <img :src="optionsLogo" class="settings-icon" @click="toggleSettings" />
          </span>
        </h1>
        <div class="playlist-buttons">
          <div class="playlist-artists">
            <span class="section-title">Featuring:</span>
            <div
              v-for="(artist, index) in getTopArtists()"
              :key="'artist' + index"
              @click="generatePlaylist(artist.name, 'artist')"
            >
              <PlaylistButtonComponent 
                :text="artist.name"
                queryType="artist"
              />
            </div>
          </div>
          <div class="playlist-genres">
            <span class="section-title">Genres:</span>
            <div
              v-for="(genre, index) in getTopGenres()"
              :key="index"
              @click="generatePlaylist(genre, 'genre')"
            >
              <PlaylistButtonComponent 
                :text="genre"
                queryType="genre"
              />
            </div>
          </div>
          <div class="playlist-vibes">
            <span class="section-title">Vibes:</span>
            <div
              v-for="(vibe, index) in getTopVibes()"
              :key='index'
              @click="generatePlaylist(vibe, 'vibe')"
            >
              <PlaylistButtonComponent 
                :text='vibe'
                queryType="vibe"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    <PlaylistSettingsComponent v-if="settingsOpen" :settings="playlistSettings" @update-settings="updateSettings" @close-settings="toggleSettings" />
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
import { defineComponent, computed, ref } from "vue";
import usePlaylistStore from "@/stores/playlistStore";
import HeaderComponent from "@/components/HeaderComponent.vue";
import SongItem from "@/components/SongItem.vue";
import PlaylistButtonComponent from "@/components/PlaylistButtonComponent.vue";
import PlaylistSettingsComponent from "@/components/PlaylistSettingsComponent.vue";
import { Song } from "@/types/Song";
import { PlaylistSettings } from "@/types/PlaylistSettings";
import optionsLogo from "@/assets/img/options-logo.png";
import savePlaylistLogo from "@/assets/img/save-playlist-logo.png"

export default defineComponent({
  name: "PlaylistView",
  components: {
    HeaderComponent,
    SongItem,
    PlaylistButtonComponent,
    PlaylistSettingsComponent
  },
  setup() {
    const playlistStore = usePlaylistStore();
    const playlist = computed(() => playlistStore.getPlaylist);
    const settingsOpen = ref(false);
    const playlistSettings = ref<PlaylistSettings>({
      numSongs: null,
      maxDuration: null,
      minSongsPerArtist: new Map(),
      maxSongsPerArtist: new Map(),
      minSongsPerGenre: new Map(),
      maxSongsPerGenre: new Map(),
      minSongsPerVibe: new Map(),
      maxSongsPerVibe: new Map(),
    });

    const toggleSettings = () => {
      settingsOpen.value = !settingsOpen.value;
    };

    const addToAccount = () => {
      // Implement functionality to add playlist to account
    };

    const updateSettings = (newSettings: PlaylistSettings) => {
      playlistSettings.value = newSettings;
      // Implement functionality to apply new settings to the playlist
    };

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
      return Array.from(playlist.value.artists).slice(0, 3);
    };

    const getTopGenres = () => {
      if (!playlist.value) return [];
      return Array.from(playlist.value.genres).sort((a, b) => b[1] - a[1]).slice(0, 3);
    };

    const getTopVibes = () => {
      if (!playlist.value) return [];
      return Array.from(playlist.value.vibes).sort((a, b) => b[1] - a[1]).slice(0, 3);
    };

    const generatePlaylist = (text: string | [string, number], queryType: string) => {
      const queryText = Array.isArray(text) ? text[0] : text;
      playlistStore.fetchAndCreatePlaylist(queryText, queryType);
    };

    return {
      playlist,
      getCoverArtUrl,
      getTopArtists,
      getTopGenres,
      getTopVibes,
      settingsOpen,
      playlistSettings,
      toggleSettings,
      addToAccount,
      updateSettings,
      generatePlaylist,
      optionsLogo,
      savePlaylistLogo
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
  position: relative;
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
  margin-left: 5px; /* Reduced the margin-left value */
}

.playlist-title {
  font-size: 24px;
  margin: 0;
  display: flex;
  align-items: center;
}

.playlist-icons {
  display: flex;
  align-items: center;
  margin-left: 10px;
}

.settings-icon, .heart-icon {
  width: 24px;
  height: 24px;
  margin-left: 5px;
  cursor: pointer;
}

.playlist-buttons {
  margin: 10px 0 0 0;
  font-size: 14px;
  display: flex;
  flex-direction: column;
}

.section-title {
  font-size: 10px;
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
    margin-left: 5px; /* Reduced the margin-left value */
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
    margin-left: 5px; /* Reduced the margin-left value */
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
    margin-left: 5px; /* Reduced the margin-left value */
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
    justify-content: center; /* Centering the title */
    width: 100%;
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
    justify-content: center; /* Centering the title */
    width: 100%;
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
    justify-content: center; /* Centering the title */
    width: 100%;
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
