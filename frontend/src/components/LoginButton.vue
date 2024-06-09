<template>
  <div class="header-container">
    <div class="login-button">
      <div class="actual-button" @click="handleClick">
        <h3 class="button-text">{{ buttonText }}</h3>
        <span class="logos">
          <img class="login-logo" :src="loginLogo" v-if="!isLoggedIn" />
          <div class="hamburger" :class="{ active: isDropdownOpen }" v-else>
            <div class="line"></div>
            <div class="line"></div>
            <div class="line"></div>
          </div>
        </span>
      </div>
      <transition name="dropdown">
        <div v-if="isDropdownOpen && isLoggedIn" class="dropdown-menu">
          <button class="dropdown-item" @click="goToProfile">My Profile</button>
          <button class="dropdown-item" @click="viewHistory">History</button>
          <button class="dropdown-item" @click="logout">Logout</button>
        </div>
      </transition>
    </div>
    <AuthModal v-if="showLoginModal" :show="showLoginModal" @close="showLoginModal = false" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import AuthModal from "@/components/AuthModal.vue";
import loginLogo from "@/assets/img/login-logo.png";
import useUserStore from "@/stores/userStore";

export default defineComponent({
  name: 'LoginButton',
  components: {
    AuthModal
  },
  setup() {
    const userStore = useUserStore();
    const isDropdownOpen = ref(false);
    const showLoginModal = ref(false);

    const isLoggedIn = computed(() => userStore.getIsLoggedIn);

    const buttonText = computed(() => {
      return isLoggedIn.value ? "Log Out" : "Log In";
    });

    const handleClick = async () => {
      if (!isLoggedIn.value) {
        showLoginModal.value = true;
      } else {
        toggleDropdown();
      }
    };

    const toggleDropdown = () => {
      isDropdownOpen.value = !isDropdownOpen.value;
    };

    const goToProfile = () => {
      console.log('Navigating to profile');
      isDropdownOpen.value = false;
    };

    const viewHistory = () => {
      console.log('Viewing history');
      isDropdownOpen.value = false;
    };

    const logout = () => {
      userStore.logout();
      isDropdownOpen.value = false;
    };

    return {
      isLoggedIn,
      buttonText,
      handleClick,
      toggleDropdown,
      isDropdownOpen,
      showLoginModal,
      goToProfile,
      viewHistory,
      logout,
      loginLogo
    };
  }
});
</script>


<style scoped>
.header-container {
  display: flex;
  justify-content: flex-end;
  padding: 10px;
}

.actual-button .logos {
  display: flex;
  align-items: center;
  user-select: none;
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
}

.actual-button .login-logo {
  width: 30px;
  height: 30px;
  vertical-align: middle; /* Ensure the logo aligns vertically with the text */
}

.hamburger {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  cursor: pointer;
  width: 24px;
  height: 24px;
  margin-left: 8px; /* Adjust the gap between the text and icon */
}

.line {
  width: 100%;
  height: 3.1px; /* Adjust to fit within the new size */
  background-color: #cacbcf;
  transition: all 0.3s ease;
  border-radius: 2px; /* Add this line to make the ends round */
}

.hamburger.active .line:nth-child(1) {
  transform: rotate(45deg) translate(6px, 5px);
}

.hamburger.active .line:nth-child(2) {
  opacity: 0;
}

.hamburger.active .line:nth-child(3) {
  transform: rotate(-45deg) translate(5px, -5px);
}

.login-button .actual-button {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px; /* Match font-size with nav-tab */
  font-weight: bold;
  line-height: 1; /* Ensure line-height is consistent */
  color: #cacbcf;
  cursor: pointer;
  text-decoration: none;
  padding: 10px 15px; /* Match padding with nav-tab */
  border-radius: 5px;
  background-color: rgba(202, 203, 207, 0.2);
  transition: background-color 0.3s, color 0.3s;
}

.login-button .actual-button:hover {
  background-color: rgba(202, 203, 207, 0.4); /* Slightly stronger background hover color */
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 5px);
  right: 0;
  background-color: #5A6082;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  z-index: 1;
  border-radius: 4px;
  overflow: hidden;
}

.dropdown-item {
  display: block;
  padding: 8px 16px;
  text-align: left;
  width: 100%;
  border: none;
  background: none;
  cursor: pointer;
  color: #cacbcf;
}

.dropdown-item:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.dropdown-enter-active, .dropdown-leave-active {
  transition: all 0.3s ease;
}

.dropdown-enter-from, .dropdown-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

.login-button {
  display: inline-block;
  padding-top: 3px;
  position: relative;
}

.actual-button .button-text {
  font-size: 20px; /* Match font-size with nav-tab */
  line-height: 1; /* Ensure line-height is consistent */
}

/* Media Queries for Small Screens */
@media (max-width: 600px) {
  .actual-button .login-logo {
    width: 24px;
    height: 24px;
  }

  .hamburger {
    width: 20px;
    height: 20px;
  }

  .line {
    height: 2.5px;
  }

  .login-button .actual-button {
    font-size: 16px;
    padding: 8px 10px;
  }

  .actual-button .button-text {
    font-size: 16px;
  }
}
</style>
