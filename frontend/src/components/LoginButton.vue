<template>
  <div class="login-button">
    <div class="actual-button" @click="handleClick">
      <h3 class="button-text">{{ text }}</h3>
      <span class="logos">
        <img class="login-logo" :src="loginLogo" v-if="!isLoggedIn" />
        <img class="spotify-logo" :src="spotifyLogo" v-else />
      </span>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, watch } from "vue";
import spotifyLogo from "@/assets/img/spotify-logo.png";
import loginLogo from "@/assets/img/login-logo.png";
import { getProfile } from "@/services/SpotifyAPIController";

export default defineComponent({
  name: 'LoginButton',
  props: {
    isLoggedIn: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return { 
      text: this.isLoggedIn ? "Logged In" : "Log In", 
      spotifyLogo, 
      loginLogo 
    };
  },
  methods: {
    async handleClick() {
      if (!this.isLoggedIn) {
        const currentPath = this.$router.currentRoute.value.fullPath;
        await getProfile(currentPath);
      } else {
        // Handle logout if necessary
      }
    }
  },
  watch: {
    isLoggedIn(newVal) {
      this.text = newVal ? "Logged In" : "Log In";
    }
  }
});
</script>

<style scoped>
.login-button {
  display: inline-block;
  user-select: none;
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
  padding-top: 3px;
}

.login-button .actual-button {
  display: flex;
  align-items: center;
  justify-content: center;
  border: none; /* Remove border if any */
  background: none; /* Make background transparent */
}

.actual-button .login-logo {
  width: 30px;
  height: 30px;
  margin-left: 2px; /* Adjust spacing as needed */

}

.actual-button .spotify-logo {
  width: 25px;
  height: 25px;
  margin-left: 10px; /* Adjust spacing as needed */
}

.login-button .actual-button .button-text {
  font-size: 25px; /* Same size as the header text */
  margin-left: 10px;
}

.login-button .actual-button:hover {
  cursor: pointer;
  user-select: none;
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
}
</style>
