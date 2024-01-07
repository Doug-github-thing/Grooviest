import "./App.css";
import "./api/Database.js";
import "./Scrollbar.css";

import Header from "./components/Header";
import Queue from "./components/Queue";
import SearchBar from "./components/SearchBar.js";
import Player from "./components/Player.js";

const App = () => {

    return (
        <div className="app">

            <div id="top">
                <h1>Whigg</h1>
                {/* <Header title="Grooviest Web App" /> */}
            </div>

            <div id="middle">
                <div className="player"><Player /></div>
            </div>

            <div id="bottom">
                <div className="columns" style={{outline:"ActiveBorder"}}>
                    <div className="column"><SearchBar label="" /></div>
                    <div className="column"><Queue /></div>
                </div>
            </div>

        </div>
    );
}

export default App;
