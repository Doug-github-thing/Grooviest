import './App.css';
import Header from './components/Header';
import Soundboard from './components/Soundboard';
import Queue from './components/Queue';

const App = () => {
  return (
    <div className="app">

      <Header title={"Groovy Web App"} />

      <body>
        <Soundboard className="column" />
        <Queue className="column" />
      </body>
    
    </div>
  );
}

export default App;
