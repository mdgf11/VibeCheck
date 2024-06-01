<template>
  <div class="app">
  </div>
  <router-view/>
</template>

<script lang="ts">
import { defineComponent } from 'vue';


export default defineComponent({
  name: 'App',
  components: {  },
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
