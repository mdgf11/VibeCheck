<template>
  <div class="search-view">
    <HeaderComponent />
    <div class="text-container">
      <p class="slogan">
        Make Your Own Sound with
      </p>
      <h2 class="title">
        Vibecheck
      </h2>
    </div>
    <div class="container">
      <SearchInputComponent />
    </div>
    <div class="help">Search for any artist, song, genre or vibe and get the playlist you want</div>
    
    <!-- Admin-specific content -->
    <div v-if="isAdmin" class="admin-section">
      <div class="admin-search">
        <input type="text" v-model="adminSearchQuery" placeholder="Admin Search" />
        <button @click="handleAdminSearch">Search</button>
      </div>
      <button @click="handleAdminDiscover">Go to Discover</button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, ref } from "vue";
import useUserStore from "@/stores/userStore";
import HeaderComponent from "@/components/HeaderComponent.vue";
import SearchInputComponent from "@/components/SearchInputComponent.vue";

export default defineComponent({
  name: 'SearchView',
  components: { HeaderComponent, SearchInputComponent },
  setup() {
    const userStore = useUserStore();
    const adminSearchQuery = ref<string>('');

    const isAdmin = computed(() => userStore.user?.admin);
    const token = computed(() => userStore.token);

    const handleAdminSearch = async () => {
      try {
        const data = await userStore.fetchAdminSearchResults(adminSearchQuery.value);
        console.log('Admin search result:', data);
      } catch (error) {
        console.error("Failed to fetch admin search results:", error);
      }
    };

    const handleAdminDiscover = async () => {
      try {
        const data = await userStore.adminDiscover();
        console.log('Admin discover result:', data);
      } catch (error) {
        console.error("Failed to fetch admin search results:", error);
      }
    };

    return { isAdmin, adminSearchQuery, handleAdminSearch, handleAdminDiscover };
  }
});
</script>

<style scoped>
.search-view {
  /** Position **/
  position: relative;
  /** Size **/
  width: 100vw;
  height: 100vh;
  /** Color **/
  background-color: #bfbebe;
  background-image: linear-gradient(to left, #2B4570 , #A37A74);
  background-size: cover;
  background-position: center;
  color: white;
  cursor: default;
  /** Other **/
  overflow: hidden;
}

.text-container {
  margin-top: 60px;
  display: block;
  text-align: center;
}

.text-container .slogan {
  font-size: 30px;
}

.text-container .title {
  font-size: 30px;
}

.help {
  display: block;
  margin-top: 20px;
  margin-left: auto;
  margin-right: auto;
  text-align: center;
  word-wrap: break-word;
  width: 80%;
  max-width: 500px;
}

.admin-section {
  margin-top: 20px;
  text-align: center;
}

.admin-search {
  margin-bottom: 10px;
}

.admin-search input {
  padding: 10px;
  font-size: 16px;
  width: 60%;
}

.admin-search button {
  padding: 10px;
  font-size: 16px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}

/* Media queries for SearchView based on width */
@media (max-width: 1200px) {
  .text-container {
    margin-top: 50px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 28px;
  }

  .help {
    width: 58%;
    max-width: 58%;
    margin-left: auto;
    margin-right: auto;
  }
}

@media (max-width: 992px) {
  .text-container {
    margin-top: 40px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 26px;
  }

  .help {
    width: 68%;
    max-width: 68%;
    margin-left: auto;
    margin-right: auto;
  }
}

@media (max-width: 768px) {
  .text-container {
    margin-top: 30px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 24px;
  }

  .help {
    width: 78%;
    max-width: 78%;
    margin-left: auto;
    margin-right: auto;
  }
}

@media (max-width: 576px) {
  .text-container {
    margin-top: 20px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 22px;
  }

  .help {
    width: 83%;
    max-width: 83%;
    margin-left: auto;
    margin-right: auto;
  }
}

@media (max-width: 480px) {
  .text-container {
    margin-top: 10px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 20px;
  }

  .help {
    width: 88%;
    max-width: 88%;
    margin-left: auto;
    margin-right: auto;
  }
}

/* Media queries for SearchView based on height */
@media (max-height: 800px) {
  .text-container {
    margin-top: 40px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 28px;
  }

  .help {
    margin-top: 15px;
  }
}

@media (max-height: 600px) {
  .text-container {
    margin-top: 30px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 26px;
  }

  .help {
    margin-top: 10px;
  }
}

@media (max-height: 400px) {
  .text-container {
    margin-top: 20px;
  }

  .text-container .slogan,
  .text-container .title {
    font-size: 24px;
  }

  .help {
    margin-top: 5px;
  }
}

/* Media queries for SearchInputComponent based on width */
@media (max-width: 1200px) {
  input {
    height: 50px;
    width: 60%;
  }

  .suggestions {
    max-width: 60%;
  }

  .category-header h4, .suggestion {
    font-size: 13px;
  }
}

@media (max-width: 992px) {
  input {
    height: 45px;
    width: 70%;
  }

  .suggestions {
    max-width: 70%;
  }

  .category-header h4, .suggestion {
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  input {
    height: 40px;
    width: 80%;
  }

  .suggestions {
    max-width: 80%;
    max-height: 30vh;
  }

  .category-header h4, .suggestion {
    font-size: 11px;
  }
}

@media (max-width: 576px) {
  input {
    height: 35px;
    width: 85%;
  }

  .suggestions {
    max-width: 85%;
    max-height: 20vh;
  }

  .category-header h4, .suggestion {
    font-size: 10px;
  }
}

@media (max-width: 480px) {
  input {
    height: 30px;
    width: 90%;
  }

  .suggestions {
    max-width: 90%;
    max-height: 15vh;
  }

  .category-header h4, .suggestion {
    font-size: 9px;
  }
}

/* Media queries for SearchInputComponent based on height */
@media (max-height: 800px) {
  input {
    height: 45px;
  }

  .suggestions {
    max-height: 25vh;
  }
}

@media (max-height: 600px) {
  input {
    height: 40px;
  }

  .suggestions {
    max-height: 20vh;
  }
}

@media (max-height: 400px) {
  input {
    height: 35px;
  }

  .suggestions {
    max-height: 15vh;
  }
}
</style>
