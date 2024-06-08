<template>
  <div class="header">
    <div class="title-and-tabs">
      <h3 @click="redirectToHome">VibeCheck</h3>
      <nav class="nav-tabs">
        <a @click="redirectToPlaylist" class="nav-tab">Playlist Generator</a>
        <a @click="redirectTo('game')" class="nav-tab">The Game</a>
      </nav>
    </div>
    <LoginButton class="login-button" />
  </div>
</template>



<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import LoginButton from "./LoginButton.vue";

export default defineComponent({
  name: 'HeaderComponent',
  components: { LoginButton },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const isSmallScreen = ref(window.innerWidth <= 600);

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

    const handleResize = () => {
      isSmallScreen.value = window.innerWidth <= 600;
    };

    onMounted(() => {
      window.addEventListener('resize', handleResize);
    });

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize);
    });

    return { redirectToHome, redirectTo, redirectToPlaylist, isSmallScreen };
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
  justify-content: space-between; /* Ensure space between title-and-tabs and login-button */
}

.title-and-tabs {
  display: flex;
  align-items: center;
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
  margin-left: 10px;
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
  background-color: rgba(202, 203, 207, 0.4);
}

.header .login-button {
  margin-right: 15px;
  margin-top: 6px;
  flex-shrink: 0; /* Prevent shrinking */
}

/* Media Queries for Small Screens */
@media (max-width: 600px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    height: auto;
    padding-top: 5px; /* Adjust padding for small screens */
  }

  .title-and-tabs {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
  }

  .header h3 {
    font-size: 28px;
    padding-left: 10px;
    margin-bottom: 5px; /* Add margin to separate from tabs */
  }

  .nav-tabs {
    margin-left: 0;
    flex-direction: column;
    width: 100%;
    align-items: flex-start; /* Align tabs to start */
  }

  .nav-tab {
    margin: 5px 0; /* Adjust spacing */
    font-size: 16px; /* Smaller font size */
    padding: 8px 10px; /* Smaller padding */
  }

  .header .login-button {
    align-self: flex-end;
    margin-right: 15px;
    margin-top: 0;
    position: absolute; /* Keep in the same position */
    right: 15px;
    top: 5px; /* Adjust top positioning */
  }
}
</style>
