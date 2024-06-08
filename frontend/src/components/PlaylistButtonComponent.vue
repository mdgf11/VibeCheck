<template>
  <button
    class="playlist-button"
    :style="buttonStyle"
    @click="generatePlaylist"
  >
    {{ text }}
  </button>
</template>

<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import usePlaylistStore from "@/stores/playlistStore";

function hashStringToColorSeed(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  return hash;
}

function generateColor(seed: number): { r: number; g: number; b: number } {
  const min = 50; // Minimum value for RGB to avoid too dark colors
  const max = 200; // Maximum value for RGB to avoid too light colors
  const r = min + ((seed >> 16) & 255) % (max - min);
  const g = min + ((seed >> 8) & 255) % (max - min);
  const b = min + (seed & 255) % (max - min);
  return { r, g, b };
}

function adjustColor(color: { r: number, g: number, b: number }, seed: number, variation: number): string {
  const adjust = (value: number, seedValue: number) =>
    Math.max(0, Math.min(255, value + (seedValue % variation) - variation / 2));
  const r = adjust(color.r, (seed >> 16) & 255);
  const g = adjust(color.g, (seed >> 8) & 255);
  const b = adjust(color.b, seed & 255);
  return `rgb(${r}, ${g}, ${b})`;
}

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

    const buttonStyle = computed(() => {
      const seed = hashStringToColorSeed(props.text);
      const baseColor = generateColor(seed);
      const color1 = `rgb(${baseColor.r}, ${baseColor.g}, ${baseColor.b})`;
      const color2 = adjustColor(baseColor, seed, 100); // Adjust color within a small variation using seed
      return {
        backgroundImage: `linear-gradient(to left, ${color1}, ${color2})`
      };
    });

    return {
      generatePlaylist,
      buttonStyle
    };
  }
});
</script>

<style scoped>
.playlist-button {
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
  opacity: 0.8;
}
</style>
