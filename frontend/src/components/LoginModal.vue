<template>
  <div class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <h2>Login</h2>
      <input type="text" placeholder="Username" v-model="username">
      <input type="password" placeholder="Password" v-model="password">
      <button @click="login">Login</button>
      <p>or continue with</p>
      <button @click="loginWithSpotify" class="spotify-button">Spotify</button>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import useUserStore from "@/stores/userStore";
import {getProfile} from "@/services/SpotifyAPIController"

export default defineComponent({
  name: 'LoginModal',
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  setup(props, { emit }) {
    const username = ref('');
    const password = ref('');

    const closeModal = () => {
      emit('close');
    };

    const login = () => {
      // Logic for logging in with username and password
      console.log('Logging in with username:', username.value);
      console.log('Logging in with password:', password.value);
    };

    const loginWithSpotify = () => {
      // Logic for logging in with Spotify
      const path = window.location.pathname;
      getProfile(path);
    };

    return {
      username,
      password,
      closeModal,
      login,
      loginWithSpotify
    };
  }
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  width: 300px;
}

input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  background-color: black;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button.spotify-button {
  background-color: #1DB954; /* Spotify green */
  color: white;
}
</style>
