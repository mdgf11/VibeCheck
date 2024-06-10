<template>
  <div class="selected-item">
    <button class="remove-button" @click="removeItem">x</button>
    <PlaylistButtonComponent :text="item.name" :queryType="item.type" />
    <div class="slider-group">
      <div class="slider-container">
        <label>
          Min songs
          <input type="number" v-model.number="item.minValue" min="0" max="60" class="number-input" />
        </label>
        <input type="range" v-model.number="item.minValue" :min="0" :max="60" class="slider" />
      </div>
      <div class="slider-container">
        <label>
          Max songs
          <input type="number" v-model.number="item.maxValue" min="0" max="60" class="number-input" />
        </label>
        <input type="range" v-model.number="item.maxValue" :min="0" :max="60" class="slider" />
      </div>
    </div>
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
      type: Object as () => { name: string; type: string; minValue: number; maxValue: number; },
      required: true
    },
    index: {
      type: Number,
      required: true
    }
  },
  methods: {
    removeItem() {
      this.$emit('remove-item', this.index);
    }
  }
});
</script>

<style scoped>
.selected-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 10px;
  width: 100%;
}

.remove-button {
  background: transparent;
  border: none;
  color: red;
  font-size: 16px;
  cursor: pointer;
  margin-right: 10px;
}

.slider-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 10px; /* Add some space between the button and sliders */
  width: calc(100% - 150px); /* Adjust the width to fit within the parent container */
}

.slider-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  margin-bottom: 10px;
}

.slider {
  width: 100%;
  margin: 2px 0;
  height: 10px; /* Adjust the height to make it smaller */
}

.number-input {
  width: 50px;
  margin: 2px 0;
  height: 20px; /* Adjust the height to make it smaller */
  /* Remove the little up and down arrows for the input box */
  -moz-appearance: textfield;
}

.number-input::-webkit-outer-spin-button,
.number-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

label {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  font-size: 12px; /* Adjust the font size to make it smaller */
}
</style>
