<template>
  <button
    class="playlist-button"
    :style="buttonStyle"
  >
    {{ displayText }}
  </button>
</template>

<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import usePlaylistStore from "@/stores/playlistStore";

function hashStringToColorSeed(input: string | [string, number]): number {
  const str = typeof input === 'string' ? input : input[0];
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
      type: [String, Array] as PropType<string | [string, number]>,
      required: true
    },
    queryType: {
      type: String as PropType<'artist' | 'album' | 'song' | 'genre' | 'vibe'>,
      required: true
    }
  },
  setup(props) {
    const playlistStore = usePlaylistStore();

    const buttonStyle = computed(() => {
      const seed = hashStringToColorSeed(
        typeof props.text === 'string' ? props.text : props.text[0]
      );
      const baseColor = generateColor(seed);
      const color1 = `rgb(${baseColor.r}, ${baseColor.g}, ${baseColor.b})`;
      const color2 = adjustColor(baseColor, seed, 100); // Adjust color within a small variation using seed
      return {
        backgroundImage: `linear-gradient(to left, ${color1}, ${color2})`
      };
    });

    const displayText = computed(() => {
      if (Array.isArray(props.text)) {
        return `${props.text[0]} ${props.text[1]}/10`;
      }
      return props.text;
    });

    return {
      buttonStyle,
      displayText
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

@media (max-width: 768px) {
  .playlist-button {
    padding: 4px 8px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .playlist-button {
    padding: 3px 6px;
    font-size: 9px;
  }
}
</style>
