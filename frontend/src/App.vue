<template>
  <div class="app">
    <SearchView />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import SearchView from "./components/SearchView.vue"


export default defineComponent({
  name: 'App',
  components: { SearchView },
  data() {
    return {}
  },
})
</script>

<style>
  *,*::before,*::after{
    margin: 0;
    padding:0;
    }
  
  * {
    box-sizing: border-box;
  }

  
</style>
