<template>
  <div class="playlist-view" v-if="playlist">
    <HeaderComponent />
    <div class="text-container">
      <h2 class="title">{{ playlist.title }}</h2>
    </div>
    <div class="playlist-container">
      <div v-for="song in playlist.songs" :key="song.name" class="song-item">
        <h3>{{ song.name }}</h3>
        <p>Artist: {{ song.artists.join(', ') }}</p>
        <p>Date: {{ song.date }}</p>
        <p>Genre: {{ song.genres.join(', ') }}</p>
        <p>Tags: {{ song.vibes.join(', ') }}</p>
      </div>
    </div>
  </div>
  <div v-else>
    <p>Loading...</p>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted } from "vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import usePlaylistStore from "@/stores/playlistStore";

export default defineComponent({
  name: 'PlaylistView',
  components: { HeaderComponent },
  setup() {
    const playlistStore = usePlaylistStore();

    return { playlist: playlistStore.playlist };
  }
});
</script>

<style scoped>
  .playlist-view {
    /** Position **/
    position: relative;
    /** Size **/
    background-size: cover;
    width: 100%;
    height: 100vh;
    width: 100vw;
    /** Color **/
    background-color: #bfbebe;
    background-position: center;
    background: rgb(0, 255, 166);
    background-image: linear-gradient(to left, #2B4570 , #A37A74);
    color: white;
    
    cursor:default;
    /** Other **/
    overflow: hidden;
  }
  
  .text-container {
    display: block;
    text-align: center;
    margin-top: 20px;
  }

  .text-container .title {
    font-size: min(5vw, 30px);
  }

  .playlist-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 20px;
  }

  .song-item {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    padding: 10px 20px;
    margin: 10px;
    width: min(80%, 600px);
    text-align: center;
    color: white;
  }

  .song-item h3 {
    font-size: 1.5em;
  }

  .song-item p {
    margin: 5px 0;
  }
</style>
