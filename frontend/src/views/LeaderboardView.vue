<template>
  <div class="leaderboard-view">
    <HeaderComponent />
    <div class="content">
      <h2>Leaderboard</h2>
      <p>Check out the top players and their scores.</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import HeaderComponent from "@/components/HeaderComponent.vue";

export default defineComponent({
  name: 'LeaderboardView',
  components: { HeaderComponent }
});
</script>

<style scoped>
.leaderboard-view {
  width: 100vw;
  height: 100vh;
  background-color: #bfbebe;
  background-image: linear-gradient(to left, #2B4570, #A37A74);
  background-size: cover;
  background-position: center;
  color: white;
}

.content {
  text-align: center;
  margin-top: 100px;
}

.content h2 {
  font-size: 36px;
}

.content p {
  font-size: 24px;
}
</style>
