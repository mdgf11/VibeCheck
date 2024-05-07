<template>
  <div class="app">
    <header>
      <div class="order">
        <button @click="handleClick('title')">Order by title</button>
        <button @click="handleClick('genre')">Order by genre</button>
        <button @click="handleClick('artist')">Order by artist</button>
      </div>
    </header>
    <SongList :songs="songs" :order="order"/>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue';
import Song from "./types/Song"
import OrderTerm from './types/OrderTerm';
import SongList from "./components/SongList.vue"

export default defineComponent({
  name: 'App',
  components: {SongList},
  setup() {
    const songs = ref<Song[]>([
      {id: '1',
      title: "call me maybe",
      artist: ["hey i just met you",
      "and this is crazy",
      "but here's my number",
      "so call me maybe"],
      date: 1,
      genre: ["pop"],
      tag: ["girly"]},

      {id: '2',
      title: "we are never getting back together",
      artist: ["awe, are never ever ever",
      "getting back together"],
      date: 1,
      genre: ["asupper-pop"],
      tag: ["breakup song"]}
    ])

    const order = ref<OrderTerm>('title')

    const handleClick = (term: OrderTerm) => {
      order.value = term
    }

    return { songs, handleClick, order }
  },
})
</script>

<style>
  header {
    text-align: center;
  }
  header .order {
    margin-top: 20px;
  }
  button {
    margin: 0 10px;
    color: #1195c9;
    border: 3px solid #1195c9;
    background: #d5f0ff;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
  }
</style>
