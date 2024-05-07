<template>
    <div class="song-list">
      <p>Ordered by: {{ order }}</p>
        <ul>
            <li v-for="song in orderedSongs" :key="song.id">
                <h2> {{ song.title }} -- {{ song.artist }}</h2>
                <div class="artist">
                    <p> {{ song.date }}</p>
                </div>
                <div class="description">
                    <p>Lorem, ipsum dolor sit amet consectetur adipisicing elit. Aspernatur recusandae vero aut at velit iste in, autem est voluptatibus explicabo dignissimos consequatur minus tenetur sequi accusamus, provident quasi dolor excepturi?

                    </p>
                </div>
            </li>
        </ul>
    </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType } from 'vue'
import Song from '@/types/Song';
import OrderTerm from '@/types/OrderTerm';

export default defineComponent({
    props: {
        songs: {
            required: true,
            type: Array as PropType<Song[]>
        },
        order: {
          required: true,
          type: String as PropType<OrderTerm>
        }
    },
    setup(props) {
      const orderedSongs = computed(() => {
        return [...props.songs].sort((a: Song, b: Song) => {
          return a[props.order] > b[props.order] ? 1 : -1
        })
      })

      return {orderedSongs}
    }
})
</script>

<style scoped>

  .song-list {
    max-width: 960px;
    margin: 40px auto;
  }
  .song-list ul {
    padding: 0;
  }
  .song-list li {
    list-style-type: none;
    background: white;
    padding: 16px;
    margin: 16px 0;
    border-radius: 4px;
  }
  .song-list h2 {
    margin: 0 0 10px;
    text-transform: capitalize;
  }
  .artist {
    display: flex;
  }
  .artist img {
    width: 30px;
  }
  .artist p {
    color: #17bf66;
    font-weight: bold;
    margin: 10px 4px;
  }

</style>
