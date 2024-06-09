<template>
  <div class="selected-item">
    <PlaylistButtonComponent :text="item.name" :queryType="item.type" />
    <input type="number" v-model.number="item.sliderValue[0]" min="0" max="100" />
    <input type="range" v-model.number="item.sliderValue[0]" :min="0" :max="100" />
    <input type="range" v-model.number="item.sliderValue[1]" :min="0" :max="100" />
    <input type="number" v-model.number="item.sliderValue[1]" min="0" max="100" />
  </div>
</template>


<script lang="ts">
import { defineComponent } from 'vue';
import PlaylistButtonComponent from '@/components/PlaylistButtonComponent.vue';

export default defineComponent({
  name: 'SelectedItemComponent',
  components: {
    PlaylistButtonComponent
  },
  props: {
    item: {
      type: Object as () => { name: string; type: string; sliderValue: [number, number]; },
      required: true
    }
  }
});
</script>

<style scoped>
.selected-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.selected-item input[type="range"] {
  margin-left: 10px;
  flex: 1;
}

.selected-item input[type="number"] {
  width: 50px;
  margin-left: 10px;
}
</style>

