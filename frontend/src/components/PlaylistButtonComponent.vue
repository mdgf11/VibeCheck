<template>
  <button class="playlist-button" @click="generatePlaylist">{{ text }}</button>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import usePlaylistStore from "@/stores/playlistStore";

export default defineComponent({
  name: "PlaylistButtonComponent",
  props: {
    text: {
      type: String,
      required: true
    },
    queryType: {
      type: String as PropType<'artist' | 'album' | 'song' | 'genre' | 'vibe'>,
      required: true
    }
  },
  setup(props) {
    const playlistStore = usePlaylistStore();

    const generatePlaylist = () => {
      playlistStore.fetchAndCreatePlaylist(props.text, props.queryType);
    };

    return {
      generatePlaylist
    };
  }
});
</script>

<style scoped>
.playlist-button {
  background-color: #2B4570;
  color: white;
  border: none;
  padding: 5px 10px;
  margin: 3px;
  border-radius: 15px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.playlist-button:hover {
  background-color: #1e3458;
}
</style>
