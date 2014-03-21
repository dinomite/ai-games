package com.theaigames.warlight.map;

import java.util.LinkedList;

/**
 * A collection of Regions on the map that provides a bonus.  In the standard world map these are:
 *  - Africa
 *  - Asia
 *  - Australia
 *  - Europe
 *  - North America
 *  - South America
 */
public class SuperRegion {

    private int id;
    private int armiesReward;
    private LinkedList<Region> subRegions;

    public SuperRegion(int id, int armiesReward) {
        this.id = id;
        this.armiesReward = armiesReward;
        subRegions = new LinkedList<Region>();
    }

    public void addSubRegion(Region subRegion) {
        if (!subRegions.contains(subRegion)) {
            subRegions.add(subRegion);
        }
    }

    /**
     * @return A string with the name of the player that fully owns this SuperRegion
     */
    public boolean ownedByPlayer(String player) {
        for (Region region : subRegions) {
            if (!player.equals(region.getPlayerName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the ownership share of this SuperRegion
     *
     * @param player The player to check ownership for
     * @return A float between 0 & 1 representing the share of ownership the given player has over this SuperRegion
     */
    public float ownershipShare(String player) {
        int ownedRegions = 0;

        for (Region region : subRegions) {
            if (!player.equals(region.getPlayerName())) {
                ownedRegions++;
            }
        }

        return ownedRegions / subRegions.size();
    }

    /**
     * @return The id of this SuperRegion
     */
    public int getId() {
        return id;
    }

    /**
     * @return The number of armies a Player is rewarded when he fully owns this SuperRegion
     */
    public int getArmiesReward() {
        return armiesReward;
    }

    /**
     * @return A list with the Regions that are part of this SuperRegion
     */
    public LinkedList<Region> getSubRegions() {
        return subRegions;
    }
}
