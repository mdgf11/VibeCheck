<template>
  <div class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <div v-if="isLogin">
        <h2>Login</h2>
        <input type="email" placeholder="Email" v-model="email">
        <input type="password" placeholder="Password" v-model="password">
        <button @click="login">Login</button>
        <p>or continue with</p>
        <button @click="loginWithSpotify" class="spotify-button">Spotify</button>
        <p>Don't have an account? <span @click="toggleForm">Register</span></p>
      </div>
      <div v-else>
        <h2>Register</h2>
        <input type="text" placeholder="Username" v-model="username">
        <input type="password" placeholder="Password" v-model="password">
        <input type="email" placeholder="Email" v-model="email">
        <button @click="register">Register</button>
        <p>or continue with</p>
        <button @click="loginWithSpotify" class="spotify-button">Spotify</button>
        <p>Already have an account? <span @click="toggleForm">Login</span></p>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { redirectToSpotify } from "@/services/spotifyHandler";
import useUserStore from "@/stores/userStore";

export default defineComponent({
  name: 'AuthModal',
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  setup(props, { emit }) {
    const userStore = useUserStore();
    const username = ref('');
    const password = ref('');
    const email = ref('');
    const isLogin = ref(true);

    const closeModal = () => {
      emit('close');
    };

    const toggleForm = () => {
      isLogin.value = !isLogin.value;
    };

    const login = async () => {
      try {
        const response = await fetch(import.meta.env.VITE_APP_BACKEND_URL + '/user/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            email: email.value,
            password: password.value
          })
        });

        if (!response.ok) {
          throw new Error('Login failed');
        }

        const data = await response.json();
        userStore.setTokenAndUser(data);
        closeModal();
      } catch (error) {
        console.error('Login failed:', error);
      }
    };

    const register = async () => {
      try {
        const response = await fetch(import.meta.env.VITE_APP_BACKEND_URL + '/user/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            email: email.value,
            username: username.value,
            password: password.value
          })
        });

        if (!response.ok) {
          throw new Error('Registration failed');
        }

        const data = await response.json();
        userStore.setTokenAndUser(data);
        closeModal();
      } catch (error) {
        console.error('Registration failed:', error);
      }
    };

    const loginWithSpotify = () => {
      redirectToSpotify();
    };

    return {
      username,
      password,
      email,
      isLogin,
      closeModal,
      toggleForm,
      login,
      register,
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

span {
  color: blue;
  cursor: pointer;
  text-decoration: underline;
}
</style>
