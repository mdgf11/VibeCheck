import { ref } from "vue"

const PLAYLIST_API_BASE_URL = "http://localhost:8080/playlist"

class PlaylistService {
    async getNewPlaylist(name: string) {
        const url = PLAYLIST_API_BASE_URL + '/new';
        console.log(url)
        const jsonName = {'name': name }
        const response = await fetch(url, {
                method: "Post", 
                body: JSON.stringify(jsonName)
            }) as Response;
            console.log(response);

          return response.json(); 
    }

}

export default new PlaylistService();
