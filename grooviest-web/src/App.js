import './App.css';
import './api/Database.js';
import Header from './components/Header';
import Queue from './components/Queue';
import SearchBar from './components/SearchBar.js';
import Player from './components/Player.js';

const App = () => {

    return (
        <div className="app">

            <Header title="Groovy Web App" />
                
            <div>
                <SearchBar className="column" label="Search a song to add"/>
                <Queue className="column" />
            </div>

            <div className="bottom">
                <Player />
            </div>

        </div>
    );
}

export default App;
