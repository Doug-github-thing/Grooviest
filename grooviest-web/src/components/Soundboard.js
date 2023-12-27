import CommandButton from "./CommandButton";

const Soundboard = ({ className }) => {
    return (
        <div className={className}>
            <h2>Soundboard</h2>
            <CommandButton text="a" command="url/Ku6nJjmEeaw" />
            <CommandButton text="Add mra" command="add/Mjww0roHDfs" />
        </div>
    );
}

export default Soundboard;