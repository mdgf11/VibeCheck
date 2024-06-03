<template>
  <div class="search-view">
    <HeaderComponent />
    <div class="text-container">
      <p class="slogan">
        Make Your Own Sound with
      </p>
      <h2 class="title">
        Vibecheck
      </h2>
    </div>
    <div class="container">
      <input type="text" v-model="info.message" :placeholder="defaultSearch" @keypress.enter="search" >
    </div>

    <div class="help">Write any (or multiple) artist, song, genre or vibe and we will generate the playlist you want</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import { useRouter } from 'vue-router';
import usePlaylistStore from '@/stores/playlistStore';
import { Playlist } from '@/types/Playlist';

export default defineComponent({
  name: 'SearchView',
  components: { HeaderComponent },
  setup() {
    const router = useRouter();
    const playlistStore = usePlaylistStore();
    const info = ref({ message: "" });
    const defaultSearch = ref("Search for anything!");

    const search = async () => {
      // Mock backend request and response
      const playlist: Playlist = {
        title: "Your Custom Playlist",
        songs: [
          {
            id: '1',
            title: "Song One",
            artist: ["Artist One"],
            date: 2022,
            genre: ["Genre One"],
            tag: ["Happy", "Energetic"]
          },
          {
            id: '2',
            title: "Song Two",
            artist: ["Artist Two"],
            date: 2021,
            genre: ["Genre Two"],
            tag: ["Sad", "Calm"]
          },
          {
            id: '3',
            title: "Song Three",
            artist: ["Artist Three"],
            date: 2020,
            genre: ["Genre Three"],
            tag: ["Happy", "Dance"]
          }
        ]
      };

      console.log('Setting playlist:', playlist);  // Log playlist data
      // Store playlist in Pinia
      playlistStore.setPlaylist(playlist);

      // Redirect to PlaylistView
      router.push({ name: 'playlist' });
    };

    return { info, defaultSearch, search };
  }
})
</script>

<style scoped>
  .search-view {
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
    background: #00ffa6;
    background-image: linear-gradient(to left, #2B4570 , #A37A74);
    color: white;
    
    cursor:default;
    /** Other **/
    overflow: hidden;
  }
  
  .text-container {
    margin-top: 60px;
    display: block;
    text-align: center;
  }

  .text-container .slogan {
    font-size: min(5vw, 30px);
  }

  .text-container .title {
    font-size: min(5vw, 30px);
  }

  .search-view input {
    /** Position **/
    display: block;
    margin-left: auto;
    margin-right: auto;
    margin-top: 20px;
    
    /** Size **/
    height: 60px;
    width: min(50%, 1000px);
    border-radius: 30px;
    text-align: center; 

  }

  .search-view input:hover {
    box-shadow: outset 0 0 5px 5px #888;
    background: #fff;
  }

  .search-view .help {
    display: block;
    margin-top: 20px;
    margin-left: auto;
    margin-right: auto;
    text-align: center;
    word-wrap: break-word;
    width: 500px;
  }
</style>
