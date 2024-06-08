<template>
  <div class="header">
    <h3 @click="redirectToHome">VibeCheck</h3>
    <nav class="nav-tabs">
      <a @click="redirectToPlaylist" class="nav-tab">Playlist Generator</a>
      <a @click="redirectTo('game')" class="nav-tab">The Game</a>
    </nav>
    <LoginButton class="login-button" />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter, useRoute } from "vue-router";
import LoginButton from "./LoginButton.vue";

export default defineComponent({
  name: 'HeaderComponent',
  components: { LoginButton },
  setup() {
    const router = useRouter();
    const route = useRoute();
    
    const redirectToHome = () => {
      router.push('/');
    };

    const redirectTo = (path: string) => {
      router.push(`/${path}`);
    };

    const redirectToPlaylist = () => {
      if (route.name !== 'playlist') {
        router.push('/search');
      }
    };

    return { redirectToHome, redirectTo, redirectToPlaylist };
  }
});
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  padding-top: 10px;
  height: 70px;
  background: #E49273;
  background-image: linear-gradient(to left, #2B4570, #A37A74);
  color: #cacbcf;
}

.header h3 {
  padding-left: 15px;
  font-size: 35px;
  color: #cacbcf;
  cursor: pointer;
  user-select: none;
  margin: 0;
}

.nav-tabs {
  display: flex;
  margin-left: 10px; /* Adjust this value to move tabs more to the left */
}

.nav-tab {
  margin-left: 20px;
  font-size: 20px;
  color: #cacbcf;
  cursor: pointer;
  text-decoration: none;
  padding: 10px 15px;
  border-radius: 5px;
  background-color: rgba(202, 203, 207, 0.2);
  transition: background-color 0.3s, color 0.3s;
  font-weight: bold;
}

.nav-tab:hover {
  background-color: rgba(202, 203, 207, 0.4); /* Slightly stronger background hover color */
}

.header .login-button {
  margin-right: 15px;
  margin-top: 6px;
  margin-left: auto; /* Ensure the login button is pushed to the far right */
}
</style>
