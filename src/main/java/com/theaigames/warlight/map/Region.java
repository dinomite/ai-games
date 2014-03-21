package com.theaigames.warlight.map;

import java.util.LinkedList;

/**
 * A Region on the map
 */
public class Region {
    // TODO neutral vs. unknown
    public static final String NEUTRAL = "neutral";
    public static final String UNKNOWN = "unknown";

    private int id;
    private LinkedList<Region> neighbors;
    private SuperRegion superRegion;
    private int armies;
    private String owner;

    public Region(int id, SuperRegion superRegion) {
        this.id = id;
        this.superRegion = superRegion;
        this.neighbors = new LinkedList<Region>();
        this.owner = UNKNOWN;
        this.armies = 0;

        superRegion.addSubRegion(this);
    }

    public Region(int id, SuperRegion superRegion, String owner, int armies) {
        this.id = id;
        this.superRegion = superRegion;
        this.neighbors = new LinkedList<Region>();
        this.owner = owner;
        this.armies = armies;

        superRegion.addSubRegion(this);
    }

    /**
     * @return The id of this Region
     */
    public int getId() {
        return id;
    }

    public void addNeighbor(Region neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
            neighbor.addNeighbor(this);
        }
    }

    /**
     * @param region a Region object
     * @return True if this Region is a neighbor of given Region, false otherwise
     */
    public boolean isNeighbor(Region region) {
        return neighbors.contains(region);
    }

    /**
     * @return The number of armies on this region
     */
    public int getArmies() {
        return armies;
    }

    /**
     * @param armies Sets the number of armies that are on this Region
     */
    public void setArmies(int armies) {
        this.armies = armies;
    }

    /**
     * @return A string with the name of the player that owns this region
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner Sets the Name of the player that this Region belongs to
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return True if this territory's owner is the neutral player
     */
    public boolean isNeutral() {
        return ownedByPlayer(UNKNOWN);
    }

    /**
     * @param playerName A string with a player's name
     * @return True if this region is owned by given owner, false otherwise
     */
    public boolean ownedByPlayer(String playerName) {
        return playerName.equals(this.owner);
    }

    /**
     * @return A list of this Region's neighboring Regions
     */
    public LinkedList<Region> getNeighbors() {
        return neighbors;
    }

    /**
     * @return A list of this Region's neighboring Regions
     */
    public LinkedList<Region> getNeutralNeighbors() {
        LinkedList<Region> neutralNeighbors = new LinkedList<>();

        for (Region region : neighbors) {
            if (region.getOwner().equals(UNKNOWN)) {
                neutralNeighbors.add(region);
            }
        }

        return neutralNeighbors;
    }

    /**
     * @return The SuperRegion this Region is part of
     */
    public SuperRegion getSuperRegion() {
        return superRegion;
    }
}
