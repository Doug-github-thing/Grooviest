package com.bot;

/**
 * Holds information pertaining to a single song as it sits in the database.
 * Songs are stored in the database as:
 * songs
 * __|-- # // Position in queue, starting from 0
 * __|___|-- name: <String name> // Human readable identification
 * __|___|-- url: <String url> // Youtube ID of song, ie "q4vy7BcAVJg"
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
     * Initializes a new song with a position in queue, name, and url.
     * 
     * @param position Position in queue for this new song, ie 3
     * @param name     Name of this new song, ie "10 hours of Without Cosby"
     * @param url      Youtube ID of this new song, ie "q4vy7BcAVJg"
     */
    public Song(int position, String name, String url) {
        this.position = position;
        this.name = name;
        this.url = url;
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
     * Changes song's position to the indicated new position.
     * 
     * @param position
     */
    public void changePosition(int position) {
        this.position = position;
    }

    /**
     * Moves the song's position in the queue by 1 (decrements position).
     */
    public void advance() {
        this.position--;
    }

    /**
     * Structures a Song as a string to print to stdout.
     */
    public String toString() {
        return "Song object: {Position "
                + this.position + ", " + this.name + ": " + this.url + "}";
    }
}
