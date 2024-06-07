<template>
  <div v-if="song" class="song-item" :class="{ expanded: isExpanded, 'fade-out': isRemoving }" @click="toggleExpand">
    <div class="song-image">
      <img :src="getImageUrl(song.images)" alt="Album Art" />
    </div>
    <div class="song-details">
      <div class="song-info">
        <h3>{{ song.name }}</h3>
        <div class="song-lists">
          <div class="list-group">
            <ul>
              <li v-for="(artist, index) in displayedArtists" :key="index">{{ artist }}</li>
            </ul>
          </div>
        </div>
      </div>
      <div class="song-actions">
        <p class="song-duration">{{ formattedDuration }}</p>
        <button @click.stop="removeSong" class="remove-button">-</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import usePlaylistStore from "@/stores/playlistStore"; // Import the store
import { Song } from "@/types/Song"; // Import the Song type

export default defineComponent({
  name: "SongItem",
  props: {
    song: {
      type: Object as PropType<Song>,
      required: true,
    },
  },
  data() {
    return {
      isExpanded: false,
      isRemoving: false, // New state for removing animation
    };
  },
  methods: {
    getImageUrl(images: Map<number, string>): string {
      const url = images.get(300) || images.get(64) || images.values().next().value;
      return url;
    },
    toggleExpand() {
      this.isExpanded = !this.isExpanded;
    },
    removeSong() {
      this.isRemoving = true;
      setTimeout(() => {
        const playlistStore = usePlaylistStore();
        playlistStore.removeSong(this.song); // Call the store action to remove the song
        this.isRemoving = false;
      }, 300); // Delay to allow for fade-out animation
    },
  },
  computed: {
    displayedArtists() {
      return this.isExpanded ? this.song.artists : this.song.artists.slice(0, 2);
    },
    formattedDuration() {
      const minutes = Math.floor(Number(this.song.duration) / 60000);
      const seconds = Math.floor((Number(this.song.duration) % 60000) / 1000);
      return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    },
  },
});
</script>

<style scoped>
.song-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 10px;
  margin: 5px 0;
  width: 900px;
  color: white;
  min-height: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.song-item.expanded {
  min-height: 120px;
}

.song-item.fade-out {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.song-image {
  height: 60px;
  width: 60px;
  margin-right: 10px;
}

.song-image img {
  height: 100%;
  width: auto;
  border-radius: 5px;
}

.song-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: calc(100% - 70px);
}

.song-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 80%;
}

.song-lists {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.list-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.list-title {
  font-weight: bold;
  margin-bottom: 3px;
  font-size: 0.9em;
}

.list-group ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.list-group li {
  margin: 1px 0;
  font-size: 0.8em;
}

.song-details h3 {
  font-size: 1em;
  margin: 0 0 5px 0;
}

.song-details p {
  margin: 3px 0;
  font-size: 0.8em;
}

.song-duration {
  font-size: 0.8em;
  color: #ccc;
  margin-right: 50px; /* Increased margin for spacing */
}

.song-actions {
  display: flex;
  align-items: center;
}

.remove-button {
  background: rgba(255, 255, 255, 0.1); /* Same background as song-item */
  color: white;
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  cursor: pointer;
  font-size: 1.2em;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: box-shadow 0.3s ease;
}

.remove-button:hover {
  box-shadow: 0 0 5px 2px rgba(0, 0, 0, 0.2); /* Shadow effect on hover */
}

/* Media Queries */
@media (max-width: 1200px) {
  .song-item {
    width: 700px;
  }
  .song-details {
    flex-direction: row;
    align-items: center;
  }
  .song-info {
    width: 70%;
  }
  .song-actions {
    width: 30%;
    justify-content: flex-end;
  }
}

@media (max-width: 1024px) {
  .song-item {
    width: 600px;
  }
  .song-details {
    flex-direction: row;
    align-items: center;
  }
  .song-info {
    width: 70%;
  }
  .song-actions {
    width: 30%;
    justify-content: flex-end;
  }
}

@media (max-width: 900px) {
  .song-item {
    width: 550px;
  }
  .song-details {
    flex-direction: row;
    align-items: center;
  }
  .song-info {
    width: 70%;
  }
  .song-actions {
    width: 30%;
    justify-content: flex-end;
  }
}

@media (max-width: 768px) {
  .song-item {
    width: 500px;
  }
  .song-details {
    flex-direction: row;
    align-items: center;
  }
  .song-info {
    width: 70%;
  }
  .song-actions {
    width: 30%;
    justify-content: flex-end;
  }
  .song-image {
    height: 50px;
    width: 50px;
    margin-right: 10px;
  }
  .song-details h3 {
    font-size: 0.9em;
  }
}

@media (max-width: 600px) {
  .song-item {
    width: 400px;
  }
  .song-details {
    flex-direction: row;
    align-items: center;
  }
  .song-info {
    width: 70%;
  }
  .song-actions {
    width: 30%;
    justify-content: flex-end;
  }
}

@media (max-width: 480px) {
  .song-item {
    width: 100%;
    padding: 8px;
  }
  .song-details {
    flex-direction: row;
    align-items: center;
  }
  .song-info {
    width: 70%;
  }
  .song-actions {
    width: 30%;
    justify-content: flex-end;
  }
  .song-image {
    height: 40px;
    width: 40px;
  }
  .song-details h3 {
    font-size: 0.8em;
  }
  .song-duration {
    font-size: 0.7em;
    margin-right: 20px; /* Adjust margin-right for smaller screens */
  }
  .remove-button {
    width: 25px;
    height: 25px;
    font-size: 1em;
  }
}
</style>
