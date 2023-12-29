import './App.css';
import './api/Database.js';
import Header from './components/Header';
import SearchBar from './components/SearchBar.js';
import Queue from './components/Queue';

const App = () => {

    return (
        <div className="app">

            <Header title="Groovy Web App" />

            <SearchBar className="column" label="Add song"/>
            
            <div>
                {/* <Soundboard className="column" /> */}
                <Queue className="column" />
            </div>
      
        </div>
    );
}

export default App;
