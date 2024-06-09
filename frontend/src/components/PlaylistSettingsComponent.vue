<template>
  <div class="playlist-settings">
    <div class="close-button" @click="closeSettings">&gt;</div>
    <h3>Playlist Settings</h3>
    <div class="setting-content">
      <!-- Artist Selected Items and Search -->
      <div class="setting-option">
        <div v-for="item in selectedItems.filter(item => item.type === 'artist')" :key="item.name">
          <SelectedItemComponent :item="item" />
        </div>
        <label>Include/Exclude Artists:</label>
        <input type="text" v-model="artistQuery" placeholder="Search Artists" @keyup="handleKeyUp('artist', $event)" />
        <transition name="box" appear @before-enter="beforeEnter" @enter="enter" @after-enter="afterEnter" @before-leave="beforeLeave" @leave="leave" @after-leave="afterLeave">
          <div v-if="showArtistSuggestions" class="suggestions" ref="artistSuggestionsBox">
            <div v-for="artist in filteredArtistResults" :key="artist.name" class="suggestion" @click="selectArtist(artist)">
              {{ artist.name }}
            </div>
          </div>
        </transition>
      </div>

      <!-- Genre Selected Items and Search -->
      <div class="setting-option">
        <div v-for="item in selectedItems.filter(item => item.type === 'genre')" :key="item.name">
          <SelectedItemComponent :item="item" />
        </div>
        <label>Include/Exclude Genres:</label>
        <input type="text" v-model="genreQuery" placeholder="Search Genres" @keyup="handleKeyUp('genre', $event)" />
        <transition name="box" appear @before-enter="beforeEnter" @enter="enter" @after-enter="afterEnter" @before-leave="beforeLeave" @leave="leave" @after-leave="afterLeave">
          <div v-if="showGenreSuggestions" class="suggestions" ref="genreSuggestionsBox">
            <div v-for="genre in filteredGenreResults" :key="genre" class="suggestion" @click="selectGenre(genre)">
              {{ genre }}
            </div>
          </div>
        </transition>
      </div>

      <!-- Vibe Selected Items and Search -->
      <div class="setting-option">
        <div v-for="item in selectedItems.filter(item => item.type === 'vibe')" :key="item.name">
          <SelectedItemComponent :item="item" />
        </div>
        <label>Include/Exclude Vibes:</label>
        <input type="text" v-model="vibeQuery" placeholder="Search Vibes" @keyup="handleKeyUp('vibe', $event)" />
        <transition name="box" appear @before-enter="beforeEnter" @enter="enter" @after-enter="afterEnter" @before-leave="beforeLeave" @leave="leave" @after-leave="afterLeave">
          <div v-if="showVibeSuggestions" class="suggestions" ref="vibeSuggestionsBox">
            <div v-for="vibe in filteredVibeResults" :key="vibe" class="suggestion" @click="selectVibe(vibe)">
              {{ vibe }}
            </div>
          </div>
        </transition>
      </div>
    </div>
    <button class="apply-settings-button" @click="applySettings">Apply Settings</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch, computed, Ref } from 'vue';
import { PlaylistSettings } from '@/types/PlaylistSettings';
import { Artist } from '@/types/Artist'; // Ensure the path to your Artist type is correct
import SelectedItemComponent from '@/components/SelectedItemComponent.vue';

const env = import.meta.env;

export default defineComponent({
  name: 'PlaylistSettingsComponent',
  components: {
    SelectedItemComponent
  },
  props: {
    settings: {
      type: Object as () => PlaylistSettings,
      required: true
    }
  },
  setup(props, { emit }) {
    const showNumSongs = ref(true);

    const numSongs = ref(props.settings.numSongs);
    const maxDuration = ref(props.settings.maxDuration);

    const artistQuery = ref('');
    const genreQuery = ref('');
    const vibeQuery = ref('');

    const artistResults: Ref<Artist[]> = ref([]);
    const genreResults: Ref<string[]> = ref([]);
    const vibeResults: Ref<string[]> = ref([]);

    const showArtistSuggestions = ref(false);
    const showGenreSuggestions = ref(false);
    const showVibeSuggestions = ref(false);

    interface SelectedItem {
      name: string;
      type: string;
      sliderValue: number;
    }

    const selectedItems: Ref<SelectedItem[]> = ref([]);

    const artistSuggestionsBox = ref<HTMLElement | null>(null);
    const genreSuggestionsBox = ref<HTMLElement | null>(null);
    const vibeSuggestionsBox = ref<HTMLElement | null>(null);
    let timeout: ReturnType<typeof setTimeout>;

    const toggleOption = () => {
      showNumSongs.value = !showNumSongs.value;
    };

    const applySettings = () => {
      emit('update-settings', {
        numSongs: showNumSongs.value ? numSongs.value : null,
        maxDuration: !showNumSongs.value ? maxDuration.value : null,
      });
    };

    const handleKeyUp = (type: string, event: KeyboardEvent) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => {
        if (event.key === "Enter") {
          if (type === 'artist' && artistResults.value.length > 0) {
            selectArtist(artistResults.value[0]);
          } else if (type === 'genre' && genreResults.value.length > 0) {
            selectGenre(genreResults.value[0]);
          } else if (type === 'vibe' && vibeResults.value.length > 0) {
            selectVibe(vibeResults.value[0]);
          }
        } else {
          search(type);
        }
      }, 200);
    };

    const search = async (type: string) => {
      let query = '';
      let results: Ref<Artist[]> | Ref<string[]> | null = null;
      if (type === 'artist') {
        query = artistQuery.value;
        results = artistResults;
        showArtistSuggestions.value = true;
      } else if (type === 'genre') {
        query = genreQuery.value;
        results = genreResults;
        showGenreSuggestions.value = true;
      } else if (type === 'vibe') {
        query = vibeQuery.value;
        results = vibeResults;
        showVibeSuggestions.value = true;
      }

      if (query.trim() && results !== null) {
        try {
          const url = env.VITE_APP_BACKEND_URL + `/search?query=${query}&type=${type}`;
          const response = await fetch(url);
          const data = await response.json();
          console.log(data);
          if (type === 'artist') {
            artistResults.value = data || [];
          } else if (type === 'genre') {
            genreResults.value = data.map((item: any) => item.name) || [];
          } else if (type === 'vibe') {
            vibeResults.value = data.map((item: any) => item.name) || [];
          }
        } catch (error) {
          console.error(`Failed to fetch ${type} results:`, error);
        }
      } else if (results !== null) {
        results.value = [];
      }
    };

    const selectArtist = (artist: Artist) => {
      artistQuery.value = '';
      showArtistSuggestions.value = false;
      addItem({ name: artist.name, type: 'artist' });
    };

    const selectGenre = (genre: string) => {
      genreQuery.value = '';
      showGenreSuggestions.value = false;
      addItem({ name: genre, type: 'genre' });
    };

    const selectVibe = (vibe: string) => {
      vibeQuery.value = '';
      showVibeSuggestions.value = false;
      addItem({ name: vibe, type: 'vibe' });
    };

    const addItem = (item: { name: string, type: string }) => {
      selectedItems.value.push({ ...item, sliderValue: 50 });
    };

    const filteredArtistResults = computed(() => artistResults.value.filter(artist => artist.name.toLowerCase().includes(artistQuery.value.toLowerCase())));
    const filteredGenreResults = computed(() => genreResults.value.filter(genre => genre.toLowerCase().includes(genreQuery.value.toLowerCase())));
    const filteredVibeResults = computed(() => vibeResults.value.filter(vibe => vibe.toLowerCase().includes(vibeQuery.value.toLowerCase())));

    watch(() => props.settings, (newSettings) => {
      if (newSettings) {
        numSongs.value = newSettings.numSongs;
        maxDuration.value = newSettings.maxDuration;
      }
    }, { immediate: true });

    const beforeEnter = (el: HTMLElement) => { el.style.opacity = '0'; el.style.maxHeight = '0'; };
    const enter = (el: HTMLElement, done: () => void) => {
      el.offsetHeight;
      el.style.transition = 'max-height 0.5s ease, opacity 0.5s ease';
      el.style.opacity = '1';
      el.style.maxHeight = '40vh'; // Set max-height to 40% of the viewport height
      done();
    };
    const afterEnter = (el: HTMLElement) => { el.style.transition = ''; };
    const beforeLeave = (el: HTMLElement) => { el.style.opacity = '1'; el.style.maxHeight = el.scrollHeight + 'px'; };
    const leave = (el: HTMLElement, done: () => void) => {
      el.offsetHeight;
      el.style.transition = 'max-height 0.3s ease, opacity 0.3s ease';
      el.style.opacity = '0';
      el.style.maxHeight = '0';
      setTimeout(done, 200);
    };
    const afterLeave = (el: HTMLElement) => { el.style.transition = ''; };

    const closeSettings = () => {
      emit('close-settings');
    };

    return {
      showNumSongs,
      numSongs,
      maxDuration,
      artistQuery,
      genreQuery,
      vibeQuery,
      toggleOption,
      applySettings,
      handleKeyUp,
      search,
      selectArtist,
      selectGenre,
      selectVibe,
      showArtistSuggestions,
      showGenreSuggestions,
      showVibeSuggestions,
      filteredArtistResults,
      filteredGenreResults,
      filteredVibeResults,
      artistSuggestionsBox,
      genreSuggestionsBox,
      vibeSuggestionsBox,
      selectedItems,
      beforeEnter,
      enter,
      afterEnter,
      beforeLeave,
      leave,
      afterLeave,
      closeSettings
    };
  }
});
</script>

<style scoped>
.playlist-settings {
  position: absolute;
  right: 0;
  top: 50px;
  background: rgba(51, 51, 51, 0.9);
  color: #fff;
  padding: 20px;
  border-radius: 0 10px 10px 0;
  z-index: 1000;
  width: 300px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  max-height: 80vh;
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

.setting-option input {
  width: calc(100% - 30px);
  padding: 10px;
  border: none;
  border-radius: 5px;
  background: #555;
  color: #fff;
}

.toggle-button {
  background: #888;
  color: #fff;
  padding: 5px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 12px;
  margin-left: 10px;
}

.apply-settings-button {
  background: #f39c12;
  color: #fff;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s ease;
}

.apply-settings-button:hover {
  background: #e67e22;
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
</style>
