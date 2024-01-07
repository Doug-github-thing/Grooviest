package com.bot;

/**
 * Holds information pertaining to a single song as it sits in the database.
 * Songs are stored in the database as:
 * "songs"
 * _ |-- "#" // Position in queue, starting from 0
 * _ | _ |-- "position": <int position> // Same as the name of the object above
 * _ | _ |-- "name": <String name> // Human readable identification
 * _ | _ |-- "url": <String url> // Youtube ID of song, ie "q4vy7BcAVJg"
 * _ | _ |-- "channel": <String channelName> // Channel name of uploader
 * _ | _ |-- "thumnail": <String url> // URL link to video thumbnail
 */
public class Song {

    /**
     * Position in queue. Count starts from 0.
     */
    private int position;

    /**
     * Human readable identification of song, ie title of Youtube video.
     */
    private String name;

    /**
     * Youtube ID of song, ie "q4vy7BcAVJg"
     */
    private String url;

    /**
     * @return Name of the channel which uploaded the video.
     */
    public String channel;

    /**
     * URL link to thumbnail image
     */
    private String thumbnail;

    /**
     * Initializes a new song with a position in queue, name, and url.
     * 
     * @param position Position in queue for this new song, ie 3
     * @param name     Name of this new song, ie "10 hours of Without Cosby"
     * @param url      Youtube ID of this new song, ie "q4vy7BcAVJg"
     */
    public Song(int position, String name, String channel, String url) {
        this.position = position;
        this.name = name;
        this.url = url;
        this.channel = channel;
        this.thumbnail = "https://i.ytimg.com/vi/" + url + "/default.jpg";
    }

    /**
     * Default constructor that takes no arguments is required by Firebase rtdb
     * for adding a custom class to the database.
     */
    public Song() {
        this.position = -1;
        this.name = null;
        this.url = null;
        this.channel = null;
        this.thumbnail = null;
    }

    /**
     * @return This song's position in the queue.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * @return This song's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return This song's youtube ID.
     */
    public String getURL() {
        return this.url;
    }

    /**
     * @return Name of the channel which uploaded the video.
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * @return URL link to the video's thumbnail.
     */
    public String getThumbnail() {
        return this.thumbnail;
    }

    /**
     * Sets the value for Position to the inidicated position.
     * 
     * @param newPosition The new position to set.
     */
    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    /**
     * Increases the position of this song by 1.
     */
    public void increasePosition() {
        this.position++;
    }

    /**
     * Decreases the position of this song by 1.
     */
    public void decreasePosition() {
        this.position--;
    }

    /**
     * Structures a Song as a string to print to stdout.
     */
    public String toString() {
        return "[<"
                + this.position + ": "
                + this.name + ", "
                + this.channel + ". "
                + this.url + ", "
                + this.thumbnail
                + ">]";
    }
}
