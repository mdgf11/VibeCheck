<template>
    <div class="login-button">
        <div class="actual-button" @click="getProfile()" > 
          <h3 class="button-text">{{ text }}</h3>
          <span class="logos">
            <img class="login-logo" :src="loginLogo" />
            <span class="icon-seperator">/</span>
            <img class="spotify-logo" :src="spotifyLogo" />
          </span>
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import spotifyLogo from "@/assets/img/spotify-logo.png"
import loginLogo from "@/assets/img/login-logo.png"
import { getProfile } from "@/services/SpotifyAPIController"

export default defineComponent({
    data() {
        var text = "Log In"
        return { text, spotifyLogo, loginLogo, getProfile}
    },
    methods: {
    }
    
})

</script>

<style>

  .login-button {
    /** Position **/
    display: relative;
    user-select: none;
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
    padding-top:3px;
  }

  .login-button .actual-button {
    /** Position **/
    position: relative;
    
    /** Size **/
    width: 125px;
    height: 40px;
    border-radius: 999px;
  }

  .actual-button .login-logo {
    /** Position **/
    position: relative;
    right: 4px;
    bottom: 8px;
    /** Size **/
    width: 25px;

    /** Other **/
    
  }
  .actual-button .icon-seperator {
    position: relative;
    font-size: 25px;
    right:13px;
    bottom:11px;
  }
  .actual-button .spotify-logo {
    /** Position **/
    position: relative;
    right: 15px;
    /** Size **/
    width: 17px;
    bottom:10px;

    /** Other **/
    
  }

  .login-button .actual-button .button-text {
    position: relative;
    font-size: 18px;
    bottom: 13px;
  }

  .login-button .actual-button:hover {
    cursor: pointer;
    user-select: none;
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
  }

</style>