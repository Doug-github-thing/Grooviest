import './App.css';
import CommandButton from "./components/CommandButton";

const App = () => {
  return (
    <div className="app">

      <header className="app-header">
        <h1>Groovy Web app</h1>
        <div className="control-buttons">
          <CommandButton text="Join" command="join" />
          <CommandButton text="Leave" command="leave" />
        </div>
      </header>

      <body>

        <div className="column left">
          <h2>Soundbar</h2>
          <CommandButton text="a" command="file/a.mp3" />  
        </div>

        <div className="column right">
          <h2>Database:</h2>
          <div>Once connected, the contents of the database will show here</div>
          <div>and also here</div>
        </div>

      {/* End Column Scaffold */}
      </body>
    
    {/* End app div */}
    </div>
  );
}

export default App;
