import CommandButton from "./CommandButton";

const Header = ({ title }) => {
    return (
        <header className="app-header">
            <h1>{title}</h1>
            <div className="control-buttons">
            <CommandButton text="Join" command="join" />
            <CommandButton text="Leave" command="leave" />
            </div>
        </header>
    );
}

export default Header;