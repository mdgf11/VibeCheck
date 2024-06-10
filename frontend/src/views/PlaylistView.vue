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
              <PlaylistButtonComponent :text="artist.name" queryType="artist" />
            </div>
          </div>
          <div class="playlist-genres">
            <span class="section-title">Genres:</span>
            <div
              v-for="(genre, index) in getTopGenres()"
              :key="index"
              @click="generatePlaylist(genre, 'genre')"
            >
              <PlaylistButtonComponent :text="genre" queryType="genre" />
            </div>
          </div>
          <div class="playlist-vibes">
            <span class="section-title">Vibes:</span>
            <div
              v-for="(vibe, index) in getTopVibes()"
              :key="index"
              @click="generatePlaylist(vibe, 'vibe')"
            >
              <PlaylistButtonComponent :text="vibe" queryType="vibe" />
            </div>
          </div>
        </div>
      </div>
    </div>
    <PlaylistSettingsComponent v-if="settingsOpen" :settings="playlistSettings" @update-settings="updateSettings" @close-settings="toggleSettings" />
    <div class="playlist-container">
      <div v-if="playlist">
        <transition-group name="song-list" tag="div">
          <SongItem v-for="song in playlist.songs" :key="song.id" :song="song" />
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
      newSongs: null,
      numSongs: null,
      maxDuration: null,
      minSongsPerArtist: null,
      maxSongsPerArtist: null,
      minSongsPerGenre: null,
      maxSongsPerGenre: null,
      minSongsPerVibe: null,
      maxSongsPerVibe: null,
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

/* Fixed settings panel */
.playlist-settings {
  position: fixed;
  right: 0;
  top: 50px;
  bottom: 0; /* Add this to make it occupy the remaining height */
  background: rgba(51, 51, 51, 0.9);
  color: #fff;
  padding: 20px;
  border-radius: 0 10px 10px 0;
  z-index: 1000;
  width: 300px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  max-height: calc(100vh - 50px); /* Adjust max-height */
  display: flex;
  flex-direction: column;
}

.close-button {
  position: absolute;
  left: -20px;
  top: 0;
  height: 100%;
  width: 20px;
  background: rgba(51, 51, 51, 0.9);
  color: #fff;
  text-align: center;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
  transition: background 0.3s ease;
}

.close-button:hover {
  background: rgba(30, 30, 30, 0.9);
}

.setting-content {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
}

.setting-content::-webkit-scrollbar {
  width: 0;
  height: 0;
}

.setting-content {
  -ms-overflow-style: none;  /* Internet Explorer 10+ */
  scrollbar-width: none;  /* Firefox */
}

.setting-option {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.setting-option label {
  flex: 1;
  margin-bottom: 5px;
  font-weight: bold;
}

.slider-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

.slider-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  margin-bottom: 10px;
}

.slider {
  width: 100%;
  margin: 2px 0;
  height: 10px; /* Adjust the height to make it smaller */
}

.number-input {
  width: 50px;
  margin: 2px 0;
  height: 20px; /* Adjust the height to make it smaller */
  /* Remove the little up and down arrows for the input box */
  -moz-appearance: textfield;
}

.number-input::-webkit-outer-spin-button,
.number-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

label {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  font-size: 12px; /* Adjust the font size to make it smaller */
}

.toggle-button {
  background: #888;
  color: #fff;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 12px;
  align-self: left; /* Center the button vertically */
  height: 30px; /* Match the height of the slider for better alignment */
  margin-top: 10px;
}

.toggle-button:hover {
  background: #777;
}

.toggle-new-songs-button {
  background: #888;
  color: #fff;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 12px;
  margin-top: 10px;
}

.toggle-new-songs-button:hover {
  background: #777;
}

.suggestions {
  max-width: 100%;
  padding: 10px;
  list-style: none;
  border: 1px solid #ccc;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.2);
  background: #bfbebe;
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  font-family: 'Satoshi', sans-serif;
  transition: max-height 0.3s ease, opacity 0.3s ease;
  overflow-y: auto;
  max-height: 50vh;
}

.suggestion {
  padding: 5px;
  font-size: 14px;
  color: white;
  border-bottom: 1px solid #ccc;
}

.suggestion:last-child {
  border-bottom: none;
}

.suggestion:hover {
  background-color: rgba(255, 255, 255, 0.3);
  cursor: pointer;
}
.apply-settings-button {
  background-color: #4CAF50; /* Green */
  color: white;
  border: none;
  padding: 10px 20px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 10px 0;
  cursor: pointer;
  border-radius: 5px;
}
</style>
