<template>
  <div class="header">
    <h3>VibeCheck</h3>
    <LoginButton class="login-button" :isLoggedIn="isLoggedIn" />
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, onBeforeMount } from "vue";
import LoginButton from "./LoginButton.vue";
import useUserStore from "@/stores/userStore";

export default defineComponent({
  name: 'HeaderComponent',
  components: { LoginButton },
  setup() {
    const userStore = useUserStore();

    onBeforeMount(() => {
      userStore.loadCodeFromSessionStorage();
      console.log(userStore.isLoggedIn);
    });

    const isLoggedIn = computed(() => userStore.isLoggedIn);

    return { isLoggedIn };
  }
});
</script>

<style scoped>
.header {
  /** Position **/
  padding-top: 10px;
  position: relative;
  display: block;

  /** Size **/
  height: 70px;

  /** Color **/
  background: #E49273;
  background-image: linear-gradient(to left, #2B4570, #A37A74);
  color: #cacbcf;

  /** Other **/
}

.header h3 {
  /** Position **/
  padding-left: 15px;
  padding-top: 10px;
  display: inline-block;

  /** Size **/
  font-size: 35px;

  /** Color **/
  color: #cacbcf;
  cursor: pointer;
  user-select: none;
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
}

.header .login-button {
  /** Position **/
  display: block;
  float: right;
  margin-right: 15px;
  margin-top: 6px;

  /** Size **/
  /** Color **/
}
</style>
