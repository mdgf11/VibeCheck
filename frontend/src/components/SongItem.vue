<template>
  <div v-if="song" class="song-item" :class="{ expanded: isExpanded, 'fade-out': isRemoving }" @click="toggleExpand">
    <div class="song-image">
      <img :src="getImageUrl(song.images)" alt="Album Art" />
    </div>
    <div class="song-details">
      <div class="song-info">
        <h3>{{ song.name }}</h3>
        <div class="song-lists" ref="songLists">
          <div class="list-group">
            <ul>
              <!-- Always display the first artist's button -->
              <li>
                <PlaylistButtonComponent :text="song.artists[0]" queryType="artist" />
              </li>
            </ul>
          </div>
          <div class="expanded-lists" v-if="isExpanded">
            <div class="list-group">
              <span class="list-title">Additional Artists:</span>
              <ul>
                <!-- Display additional artists only when expanded -->
                <li v-for="(artist, index) in additionalArtists" :key="index">
                  <PlaylistButtonComponent :text="artist" queryType="artist" />
                </li>
              </ul>
            </div>
            <div class="list-group genres">
              <span class="list-title">Genres:</span>
              <ul>
                <li v-for="(genre, index) in song.genres" :key="index">
                  <PlaylistButtonComponent :text="genre" queryType="genre" />
                </li>
              </ul>
            </div>
            <div class="list-group">
              <span class="list-title">Vibes:</span>
              <ul>
                <li v-for="(vibe, index) in song.vibes" :key="index">
                  <PlaylistButtonComponent :text="vibe" queryType="vibe" />
                </li>
              </ul>
            </div>
            <div class="list-group" v-if="song.albums.length">
              <span class="list-title">Albums:</span>
              <ul>
                <li v-for="(album, index) in song.albums" :key="index">
                  <PlaylistButtonComponent :text="album" queryType="album" />
                </li>
              </ul>
            </div>
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
import { defineComponent, PropType, ref, nextTick } from "vue";
import usePlaylistStore from "@/stores/playlistStore"; // Import the store
import { Song } from "@/types/Song"; // Import the Song type
import PlaylistButtonComponent from "@/components/PlaylistButtonComponent.vue";

export default defineComponent({
  name: "SongItem",
  components: {
    PlaylistButtonComponent,
  },
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
            nextTick(() => {
            this.adjustHeight();
            });
        },
        removeSong() {
            this.isRemoving = true;
            setTimeout(() => {
            const playlistStore = usePlaylistStore();
            playlistStore.removeSong(this.song); // Call the store action to remove the song
            this.isRemoving = false;
            }, 300); // Delay to allow for fade-out animation
        },
        adjustHeight() {
            const songLists = this.$refs.songLists as HTMLElement;
            const extraHeight = 10; // Extra height to ensure the first artist's category is fully visible
            const baseHeight = this.isExpanded ? songLists.scrollHeight : songLists.firstElementChild!.scrollHeight;
            songLists.style.maxHeight = `${baseHeight + extraHeight}px`;
        },
    },
  computed: {
    additionalArtists() {
      return this.song.artists.slice(1);
    },
    formattedDuration() {
      const minutes = Math.floor(Number(this.song.duration) / 60000);
      const seconds = Math.floor((Number(this.song.duration) % 60000) / 1000);
      return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    },
  },
  watch: {
    isExpanded(newVal) {
      nextTick(() => {
        this.adjustHeight();
      });
    }
  },
  mounted() {
    this.adjustHeight(); // Adjust height on component mount
  },
});
</script>

<style scoped>
.song-item {
  display: flex;
  align-items: flex-start; /* Align items to the start */
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
  overflow: hidden; /* Added to handle the overflow */
}

.song-item.expanded {
  min-height: 120px; /* Minimum height when expanded */
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
  flex-direction: column;
  justify-content: space-between;
  transition: max-height 0.5s ease; /* Consistent transition duration */
  overflow: hidden;
  max-height: 1; /* Set initial max-height to 1 to display the first item */
}

.expanded-lists {
  display: grid;
  gap: 0; /* Remove spacing between columns */
}

.list-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  border: 1px solid rgba(255, 255, 255, 0.3); /* Added border */
  padding: 5px; /* Padding inside the border */
  border-radius: 10px; /* Rounded corners */
  height: 100%; /* Ensure each category fills the height */
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
  margin-top: 2px; /* Adjust margin to position below the title */
}

.song-actions {
  display: flex;
  flex-direction: column; /* Change to column layout */
  align-items: flex-end;
  justify-content: flex-start; /* Align to the start */
  margin-left: auto; /* Push to the right */
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
  margin-top: 5px; /* Add margin to separate from duration */
}

.remove-button:hover {
  box-shadow: 0 0 5px 2px rgba(0, 0, 0, 0.2); /* Shadow effect on hover */
}

/* Media Queries */
@media (min-width: 1600px) {
  .song-item {
    width: 1200px;
  }
  .expanded-lists {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
}

@media (min-width: 1400px) and (max-width: 1599px) {
  .song-item {
    width: 1100px;
  }
  .expanded-lists {
    grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  }
}

@media (min-width: 1200px) and (max-width: 1399px) {
  .song-item {
    width: 1000px;
  }
  .expanded-lists {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  }
}

@media (min-width: 1000px) and (max-width: 1199px) {
  .song-item {
    width: 900px;
  }
  .expanded-lists {
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  }
}

@media (min-width: 800px) and (max-width: 999px) {
  .song-item {
    width: 700px;
  }
  .expanded-lists {
    grid-template-columns: repeat(auto-fit, minmax(90px, 1fr));
  }
}

@media (min-width: 600px) and (max-width: 799px) {
  .song-item {
    width: 500px;
  }
  .expanded-lists {
    grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  }
}

@media (max-width: 599px) {
  .song-item {
    width: 100%;
    padding: 8px;
  }
  .song-details {
    flex-direction: row;
    align-items: flex-start;
    justify-content: space-between;
  }
  .song-info {
    width: calc(100% - 70px);
  }
  .song-actions {
    flex-direction: row;
    align-items: center;
    margin-left: 0;
    margin-top: 0;
  }
  .song-duration {
    font-size: 0.7em;
    margin-right: 10px; /* Adjust margin for alignment */
  }
  .remove-button {
    width: 25px;
    height: 25px;
    font-size: 1em;
    margin-left: 10px; /* Adjust margin for alignment */
  }
  .expanded-lists {
    grid-template-columns: 1fr;
  }
  .song-lists {
    max-height: none; /* Remove max-height for small screens */
  }
}
</style>




