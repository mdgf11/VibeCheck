import "./Hero.css"
import Carousel from "react-material-ui-carousel";
import { Paper } from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCirclePlay } from "@fortawesome/free-solid-svg-icons";
import { Link } from "react-router-dom";

const Hero = ({songs}) => {
    return (
        <div className="song-carousel-container">
            <Carousel>
                {
                    songs?.map((song) =>{
                        return(
                            <Paper>
                                <div className = 'song-card-container'>
                                    <div className="song-card" style={{"--img": `url(${song.backdrops[0]})`}}>
                                        <div className="song-detail">
                                            <div className="song-poster">
                                                <img src={song.poster} alt="" />
                                            </div>
                                            <div className="song-title">
                                                <h4>{song.title}</h4>
                                            </div>
                                            <div className="song-buttons-container">
                                                <Link to={`/Trailer/${song.trailerLink.substring(song.trailerLink.length - 11)}`}>
                                                    <div className="play-button-icon-container">
                                                        <FontAwesomeIcon className="play-button-icon"
                                                            icon = {faCirclePlay}
                                                        />
                                                    </div>
                                                </Link>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </Paper>
                        )
                    })
                }
            </Carousel>
        </div>
    )
}   

export default Hero