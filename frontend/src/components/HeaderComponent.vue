<template>
  <div class="header">
    <h3 @click="redirectToHome">VibeCheck</h3>
    <LoginButton class="login-button" />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import LoginButton from "./LoginButton.vue";

export default defineComponent({
  name: 'HeaderComponent',
  components: { LoginButton },
  setup() {
    const router = useRouter();
    
    const redirectToHome = () => {
      router.push('/');
    };

    return { redirectToHome };
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
