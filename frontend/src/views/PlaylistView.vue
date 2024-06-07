<template>
  <div class="playlist-view" v-if="playlist">
    <HeaderComponent />
    <div class="text-container">
      <div class="playlist-cover">
        <img :src="getLargeImageUrl(playlist.songs[0].images)" alt="Cover Art" />
      </div>
      <div class="playlist-details">
        <h2 class="title">{{ playlist.title }}</h2>
        <div class="genres">
          <p v-for="genre in uniqueGenres" :key="genre">{{ genre }}</p>
        </div>
      </div>
    </div>
    <div class="playlist-container">
      <SongItem v-for="song in playlist.songs" :key="song.name" :song="song" />
    </div>
  </div>
  <div v-else>
    <p>Loading...</p>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import SongItem from "@/components/SongItem.vue";
import usePlaylistStore from "@/stores/playlistStore";

export default defineComponent({
  name: 'PlaylistView',
  components: { HeaderComponent, SongItem },
  setup() {
    const playlistStore = usePlaylistStore();

    const getImageUrl = (images: Map<number, string>) => {
      const url = images.get(64) || images.values().next().value;
      return url;
    };

    const getLargeImageUrl = (images: Map<number, string>) => {
      const url = images.get(640) || images.get(300) || images.values().next().value;
      return url;
    };

    const uniqueGenres = computed(() => {
      const genresSet = new Set<string>();
      for (const song of playlistStore.playlist?.songs || []) {
        for (const genre of song.genres) {
          if (genresSet.size < 4) {
            genresSet.add(genre);
          }
        }
        if (genresSet.size >= 4) {
          break;
        }
      }
      return Array.from(genresSet);
    });

    return { 
      playlist: playlistStore.playlist,
      getImageUrl,
      getLargeImageUrl,
      uniqueGenres
    };
  }
});
</script>

<style scoped>
.playlist-view {
  position: relative;
  background-size: cover;
  width: 100%;
  height: 100vh;
  background-color: #bfbebe;
  background-position: center;
  background: rgb(0, 255, 166);
  background-image: linear-gradient(to left, #2B4570 , #A37A74);
  color: white;
  cursor: default;
  overflow-y: auto; /* Make page scrollable */
}

.text-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  position: relative;
  margin-right: 200px;
}

.playlist-cover img {
  width: 150px; /* Adjusted size */
  height: 150px; /* Adjusted size */
  border-radius: 10px;
}

.playlist-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 20px; /* Adjusted for better alignment */
}

.text-container .title {
  font-size: min(4vw, 24px);
}

.genres {
  display: flex;
  flex-direction: column;
  margin-top: 10px;
}

.genres p {
  margin: 0;
  font-size: 0.9em;
}

.playlist-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}
</style>
