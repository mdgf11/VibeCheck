<template>
  <div class="song-item">
    <div class="song-image">
      <img :src="getImageUrl(song.images)" alt="Album Art" />
    </div>
    <div class="song-details">
      <div class="song-info">
        <h3>{{ song.name }}</h3>
        <p> {{ song.artists.join(', ') }}</p>
      </div>
      <div class="song-meta">
        <p> {{ song.genres.join(', ') }}</p>
        <p> {{ song.vibes.join(', ') }}</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";

export default defineComponent({
  name: "SongItem",
  props: {
    song: {
      type: Object as PropType<{
        name: string;
        artists: string[];
        genres: string[];
        vibes: string[];
        images: Map<number, string>;
      }>,
      required: true
    }
  },
  methods: {
    getImageUrl(images: Map<number, string>): string {
      const url = images.get(64) || images.values().next().value;
      return url;
    }
  }
});
</script>

<style scoped>
.song-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 10px; /* Adjusted padding */
  margin: 5px 0; /* Adjusted margin */
  width: 97%; /* Increased width */
  max-width: 850px; /* Adjusted max-width */
  color: white;
}

.song-image img {
  width: 40px; /* Adjusted width */
  height: 40px; /* Adjusted height */
  border-radius: 5px;
  margin-right: 10px;
}

.song-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.song-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.song-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.song-details h3 {
  font-size: 1em;
  margin: 0;
}

.song-details p {
  margin: 3px 0;
  font-size: 0.8em;
}
</style>
