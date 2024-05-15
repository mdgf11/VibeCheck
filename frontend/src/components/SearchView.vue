<template>
    <div class="search-view">
        <HeaderView />
        <div class="text-container">
            <p class="slogan">
                Make Your Own Sound with
            </p>
            <h2 class="title">
                Vibecheck
            </h2>
        </div>
        <div class="container">
            <input type="text" v-model="info.message" :placeholder="defaultSearch" >
        </div>

        <div class="help">Write any (or multiple) artist, song, genre or vibe and we will generate the playlist you want</div>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import HeaderView from "./HeaderView.vue"

export default defineComponent({
    name: 'SearchView',
    components: { HeaderView },
    setup() {
        const info = ref({message: ""});
        const defaultSearch = ref("Search for anything!")
        const updatePlaylistName = (name: string) => {
            info.value.message = name
        }
        return { info, defaultSearch, updatePlaylistName}
    }
})
</script>>

<style>
    .search-view {
        /** Position **/
        position: relative;
        /** Size **/
        background-size: cover;
        width: 100%;
        height: 100vh;
        width: 100vw;
        /** Color **/
        background-color: #bfbebe;
        background-position: center;
        background: rgb(0, 255, 166);
        background-image: linear-gradient(to left, #2B4570 , #A37A74);
        color: white;
        
        cursor:default;
        /** Other **/
        overflow: hidden;
    }
    
    .text-container {
        display: block;
        text-align: center;
    }

    .text-container .slogan {
        font-size: min(5vw, 30px);
    }

    .text-container .title {
        font-size: min(5vw, 30px);
    }

    .search-view input {
        /** Position **/
        display: block;
        margin-left: auto;
        margin-right: auto;
        margin-top: 10px;
        
        /** Size **/
        height: 50px;
        width: min(50%, 1000px);
        border-radius: 30px;
        text-align: center; 

    }

    .search-view input:hover {
        box-shadow: outset 0 0 5px 5px #888;
        background: #fff;
    }

    .search-view .help {
        display: block;
        margin-top: 10px;
        margin-left: auto;
        margin-right: auto;
        text-align: center;
        word-wrap: break-word;
        width: 500px;
    }

</style>