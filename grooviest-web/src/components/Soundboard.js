import CommandButton from "./CommandButton";

const Soundboard = ({ className }) => {
    return (
        <div className={className}>
            <h2>Soundboard</h2>
            <CommandButton text="a" command="file/a.mp3" />
            <CommandButton text="Add mra" command="add/Mjww0roHDfs" />
        </div>
    );
}

export default Soundboard;