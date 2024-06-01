<template>
  <div class="header">
    <h3>VibeCheck</h3>
    <ToggleButton class="login-button" />

  </div>
</template>


<script lang="ts">
    import { defineComponent } from "vue";
    import spotifyLogo from "@/assets/img/spotify-logo.png"
    import ToggleButton from "./LoginButton.vue"

    export default defineComponent({
        name: 'HeaderComponent',
        components: { ToggleButton  },
        data() {
            
            return {  spotifyLogo }
        }
        
    });
</script>

<style>
    
    .header {
        /** Position **/
        padding-top:10px;
        position: relative;
        display: block;

        /** Size **/
        height: 70px;

        /** Color **/
        background: #E49273;
        background-image: linear-gradient(to left, #2B4570 , #A37A74);
        color: #cacbcf;


        /** Other **/
    }

    .header h3 {
        /** Position **/
        padding-left: 15px;
        padding-top: 10px;
        display: inline-block;

        /** Size **/
        font-size: 20px;

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