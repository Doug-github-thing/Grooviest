import './App.css';
import './api/Database.js';
import Header from './components/Header';
import Queue from './components/Queue';
import SearchBar from './components/SearchBar.js';

const App = () => {

    return (
        <div className="app">

            <Header title="Groovy Web App" />
                
            <div>
                <SearchBar className="column" />
                <Queue className="column" />
            </div>
      
        </div>
    );
}

export default App;
