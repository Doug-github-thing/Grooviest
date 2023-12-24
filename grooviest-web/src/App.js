import './App.css';
import CommandButton from "./components/CommandButton";

const App = () => {
  return (
    <div className="App">
      <header className="App-header">
        <h>Groovy Web app</h>
        <CommandButton text="Join" command="join" />
        <CommandButton text="Leave" command="leave" />
        <CommandButton text="a" command="file/a.mp3" />
      </header>
    </div>
  );
}

export default App;
