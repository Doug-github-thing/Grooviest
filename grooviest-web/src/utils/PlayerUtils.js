export default class PlayerUtils {
    /**
     * Parses minutes and seconds from a given millisecond representation of time.
     * @param {Number} ms A millisecond represenation of time.
     * @returns An array containing [minutes, seconds]
     */
    static getMinutesSeconds = (ms) => {

        const minutes = Math.floor(ms / (60 * 1000));
        const remainingSeconds = (ms / 1000) - (minutes * 60);
        const seconds = Math.floor(remainingSeconds);

        const egg = [minutes.toString(), seconds.toString()];
        return egg;
    }

    /**
     * @param {Number} ms A millisecond representation of time.
     * @returns A formatted string as either "MM:SS" or "H:MM:SS" if H > 0
     */
    static formatTime = (ms) => {

        // Prepend the hour if it's at least 1
        if (ms > (60 * 60 * 1000)) {
            const hours = Math.floor(ms / (60 * 60 * 1000));
            const remainingMS = (ms - (hours * 60 * 60 * 1000));
            const minutesSeconds = this.getMinutesSeconds(remainingMS);
            return `${hours}:${minutesSeconds[0].padStart(2, 0)}:${minutesSeconds[1].padStart(2, 0)}`;
        }

        const minutesSeconds = this.getMinutesSeconds(ms);
        // console.log(minutesSeconds);
        return `${minutesSeconds[0]}:${minutesSeconds[1].padStart(2, 0)}`; 
    };
}
