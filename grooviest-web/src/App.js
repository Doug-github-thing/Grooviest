import './App.css';
import './api/Database.js';
import Header from './components/Header';
import Soundboard from './components/Soundboard';
import Queue from './components/Queue';

const App = () => {
  return (
    <div className="app">

      <Header title={"Groovy Web App"} />

      <div>
        <Soundboard className="column" />
        <Queue className="column" />
      </div>
    
    </div>
  );
}

export default App;
