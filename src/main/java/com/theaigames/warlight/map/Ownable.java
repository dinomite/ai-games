package com.theaigames.warlight.map;

import java.util.LinkedList;

abstract class Ownable {
    /**
     * Get the ownership share of this SuperRegion
     *
     * @param player The player to check ownership for
     * @return A float between 0 & 1 representing the share of ownership the given player has over this SuperRegion
     */
    public float ownershipShare(String player) {
        float ownedRegions = 0.0f;

        for (Region region : getRegions()) {
            if (player.equals(region.getOwner())) {
                ownedRegions++;
            }
        }

        return ownedRegions / getRegions().size();
    }

    /**
     * Get all of the {@link Region}s owned by the given player.
     *
     * @param player The player to find {@link Region}s for
     * @return All of the {@link Region}s owned by the given player
     */
    public LinkedList<Region> getOwnedRegions(String player) {
        LinkedList<Region> ownedRegions = new LinkedList<>();

        for (Region region : getRegions()) {
            if (region.ownedByPlayer(player)) {
                ownedRegions.add(region);
            }
        }

        return ownedRegions;
    }

    public abstract LinkedList<Region> getRegions();
}
