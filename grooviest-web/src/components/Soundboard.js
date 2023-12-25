import CommandButton from "./CommandButton";

const Soundboard = ({ className }) => {
    return (
        <div className={className}>
            <h2>Soundboard</h2>
            <CommandButton text="a" command="file/a.mp3" />
        </div>
    );
}

export default Soundboard;