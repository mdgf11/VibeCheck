<template>
  <div>
    <input type="text" v-model="message" :placeholder="defaultSearch" @input="handleInput">
    <transition
      name="box"
      appear
      @before-enter="beforeEnter"
      @enter="enter"
      @after-enter="afterEnter"
      @before-leave="beforeLeave"
      @leave="leave"
      @after-leave="afterLeave"
    >
      <div v-if="shouldShowSuggestions" class="suggestions" ref="suggestionsBox">
        <div v-for="category in Object.keys(filteredGroupedResults)" :key="category" class="suggestion-group">
          <div
            :class="{'category-header': true, 'category-header-clickable': shouldShowArrow(category)}"
            @click="shouldShowArrow(category) ? toggleCategory(category) : null"
          >
            <h4>{{ capitalize(category) }}</h4>
            <span v-if="shouldShowArrow(category)" :class="{'arrow': true, 'expanded': expandedCategories[category]}">▲</span>
          </div>
          <div>
            <div v-for="result in filteredVisibleResults(category)" :key="result.name" class="suggestion" @click="selectSuggestion(result)">
              <span>{{ result.name }} <span v-if="result.artist">by {{ result.artist }}</span></span>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { useRouter } from 'vue-router';
import usePlaylistStore from '@/stores/playlistStore';
import { Playlist } from '@/types/Playlist';

const env = import.meta.env;

interface SearchResult {
  name: string;
  artist: string | null;
  type: string;
}

interface GroupedResults {
  artists: SearchResult[];
  albums: SearchResult[];
  songs: SearchResult[];
  vibes: SearchResult[];
  genres: SearchResult[];
  [key: string]: SearchResult[];
}

export default defineComponent({
  name: 'SearchInputComponent',
  setup() {
    const router = useRouter();
    const playlistStore = usePlaylistStore();
    const message = ref<string>("");
    const defaultSearch = ref<string>("Search for anything!");
    const searchResults = ref<SearchResult[]>([]);
    const expandedCategories = ref<{ [key: string]: boolean }>({ artists: false, albums: false, songs: false, vibes: false, genres: false });
    const suggestionsBox = ref<HTMLElement | null>(null);
    let timeout: ReturnType<typeof setTimeout>;

    const handleInput = () => {
      clearTimeout(timeout);
      timeout = setTimeout(search, 100);
    };

    const search = async () => {
      if (!message.value.trim()) {
        searchResults.value = [];
        return;
      }
      try {
        const response = await fetch(env.VITE_APP_BACKEND_URL +`/search?query=${message.value}`);
        searchResults.value = await response.json();
      } catch (error) {
        console.error("Failed to fetch search results:", error);
      }
    };

    const selectSuggestion = async (result: SearchResult) => {
      message.value = result.name;
      const { name, type } = result;
      const response = await fetch(`${env.VITE_APP_BACKEND_URL}/playlist?query=${name}&type=${type}`);
      const data = await response.json();
      console.log(data);

      // Create Playlist object
      const playlist: Playlist = {
        title: data.name,
        songs: data.songs.map((song: any) => ({
          name: song.name,
          artists: song.artists,
          albums: song.albums,
          genres: song.genres,
          vibes: song.vibes,
          date: song.date
        })),
        artists: data.artists.map((artist: any) => ({
          name: artist.name,
          albums: artist.albums,
          features: artist.features,
          songs: artist.songs,
          genres: artist.genres,
          vibes: artist.vibes,
          spotifyId: artist.spotifyId,
          images: artist.images
        }))
    };

  // Set the playlist
  playlistStore.setPlaylist(playlist);

  // Navigate to the playlist
  router.push({ name: 'playlist' });
};

    const toggleCategory = (category: string) => {
      expandedCategories.value[category] = !expandedCategories.value[category];
    };

    const groupedResults = computed<GroupedResults>(() => ({
      artists: searchResults.value.filter(result => result.type === 'artist'),
      albums: searchResults.value.filter(result => result.type === 'album'),
      songs: searchResults.value.filter(result => result.type === 'song'),
      vibes: searchResults.value.filter(result => result.type === 'vibe'),
      genres: searchResults.value.filter(result => result.type === 'genre'),
    }));

    const filteredGroupedResults = computed(() => {
      const result: Partial<GroupedResults> = {};
      for (const category in groupedResults.value) {
        if (groupedResults.value[category].length > 0) {
          result[category] = groupedResults.value[category];
        }
      }
      return result as GroupedResults;
    });

    const visibleResults = computed<GroupedResults>(() => {
      const limit = 3;
      return {
        artists: expandedCategories.value.artists ? groupedResults.value.artists : groupedResults.value.artists.slice(0, limit),
        albums: expandedCategories.value.albums ? groupedResults.value.albums : groupedResults.value.albums.slice(0, limit),
        songs: expandedCategories.value.songs ? groupedResults.value.songs : groupedResults.value.songs.slice(0, limit),
        vibes: expandedCategories.value.vibes ? groupedResults.value.vibes : groupedResults.value.vibes.slice(0, limit),
        genres: expandedCategories.value.genres ? groupedResults.value.genres : groupedResults.value.genres.slice(0, limit),
      };
    });

    const filteredVisibleResults = (category: string) => {
      return visibleResults.value[category as keyof GroupedResults];
    };

    const shouldShowSuggestions = computed(() => searchResults.value.length > 0 && message.value.trim() !== '');

    const shouldShowArrow = (category: string) => {
      return groupedResults.value[category].length > 3;
    };

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

    const capitalize = (s: string) => s.charAt(0).toUpperCase() + s.slice(1);

    return {
      message, defaultSearch, handleInput, search, searchResults, groupedResults, filteredGroupedResults, visibleResults, filteredVisibleResults, selectSuggestion, toggleCategory, expandedCategories, shouldShowSuggestions, shouldShowArrow, beforeEnter, enter, afterEnter, beforeLeave, leave, afterLeave, capitalize, suggestionsBox
    };
  }
});
</script>

<style scoped>
body {
  font-family: 'Satoshi', sans-serif;
}

input {
  display: block;
  margin-left: auto;
  margin-right: auto;
  margin-top: 20px;
  height: 60px;
  width: min(50%, 1000px);
  border-radius: 30px;
  text-align: center; 
  font-family: 'Satoshi', sans-serif;
}

input:hover {
  box-shadow: outset 0 0 5px 5px #888;
  background: #fff;
}

.suggestions {
  max-width: 40%;
  margin: 10px auto;
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
  max-height: 50vh; /* Set the maximum height to 50% of the viewport height */
}

/* Transition classes for smooth opening and closing */
.suggestions.box-enter-active,
.suggestions.box-leave-active {
  transition: max-height 0.3s ease, opacity 0.3s ease;
}

.suggestions.box-enter,
.suggestions.box-leave-to {
  max-height: 0;
  opacity: 0;
}

.suggestions.box-enter-to,
.suggestions.box-leave {
  max-height: 50vh; /* Same as the fixed max-height */
  opacity: 1;
}

.suggestion-group {
  margin: 10px 0;
  text-align: left;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-header h4 {
  margin: 5px 0;
  font-size: 14px;
  font-weight: normal;
  color: #f0f0f0;
}

.category-header-clickable {
  cursor: pointer;
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

.arrow {
  display: inline-block;
  transition: transform 0.3s ease, color 0.3s ease;
  font-size: 16px;
  border-radius: 50%;
}

.expanded {
  transform: rotate(180deg);
}

/* Media queries for smaller screens */
@media (max-width: 1200px) {
  input {
    height: 50px;
    width: 60%;
  }

  .suggestions {
    max-width: 60%;
  }

  .category-header h4, .suggestion {
    font-size: 13px;
  }
}

@media (max-width: 992px) {
  input {
    height: 45px;
    width: 70%;
  }

  .suggestions {
    max-width: 70%;
  }

  .category-header h4, .suggestion {
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  input {
    height: 40px;
    width: 80%;
  }

  .suggestions {
    max-width: 80%;
    max-height: 30vh;
  }

  .category-header h4, .suggestion {
    font-size: 11px;
  }
}

@media (max-width: 576px) {
  input {
    height: 35px;
    width: 85%;
  }

  .suggestions {
    max-width: 85%;
    max-height: 20vh;
  }

  .category-header h4, .suggestion {
    font-size: 10px;
  }
}

@media (max-width: 480px) {
  input {
    height: 30px;
    width: 90%;
  }

  .suggestions {
    max-width: 90%;
    max-height: 15vh;
  }

  .category-header h4, .suggestion {
    font-size: 9px;
  }
}

</style>
