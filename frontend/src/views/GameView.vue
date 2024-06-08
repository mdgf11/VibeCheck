<template>
  <div class="game-view">
    <HeaderComponent />
    <div class="content">
      <h2>Welcome to The Game</h2>
      <p>Here you can play and have fun with music trivia.</p>
      <button @click="redirectToLeaderboard" class="leaderboard-button">View Leaderboard</button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import { useRouter } from "vue-router";

export default defineComponent({
  name: 'GameView',
  components: { HeaderComponent },
  setup() {
    const router = useRouter();

    const redirectToLeaderboard = () => {
      router.push('/leaderboard');
    };

    return { redirectToLeaderboard };
  }
});
</script>

<style scoped>
.game-view {
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

.leaderboard-button {
  margin-top: 20px;
  padding: 10px 20px;
  font-size: 18px;
  background-color: #2B4570;
  color: #cacbcf;
  border: none;
  cursor: pointer;
  border-radius: 5px;
}

.leaderboard-button:hover {
  background-color: #A37A74;
}
</style>
