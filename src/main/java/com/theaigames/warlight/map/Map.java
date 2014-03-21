package com.theaigames.warlight.map;

import java.util.LinkedList;

/**
 * The entire game Map, which contains many Regions, each of which is part of a bonus-providing SuperRegion.
 */
public class Map extends Ownable {

    public LinkedList<Region> regions;
    public LinkedList<SuperRegion> superRegions;

    public Map() {
        this.regions = new LinkedList<Region>();
        this.superRegions = new LinkedList<SuperRegion>();
    }

    public Map(LinkedList<Region> regions, LinkedList<SuperRegion> superRegions) {
        this.regions = regions;
        this.superRegions = superRegions;
    }

    /**
     * add a Region to the map
     *
     * @param region : Region to be added
     */
    public void add(Region region) {
        for (Region r : regions) {
            if (r.getId() == region.getId()) {
                System.err.println("Region cannot be added: id already exists.");
                return;
            }
        }

        regions.add(region);
    }

    /**
     * add a SuperRegion to the map
     *
     * @param superRegion : SuperRegion to be added
     */
    public void add(SuperRegion superRegion) {
        for (SuperRegion s : superRegions) {
            if (s.getId() == superRegion.getId()) {
                System.err.println("SuperRegion cannot be added: id already exists.");
                return;
            }
        }

        superRegions.add(superRegion);
    }

    /**
     * @return : a new Map object exactly the same as this one
     */
    public Map getMapCopy() {
        Map newMap = new Map();
        for (SuperRegion sr : superRegions)  {
            SuperRegion newSuperRegion = new SuperRegion(sr.getId(), sr.getArmiesReward());
            newMap.add(newSuperRegion);
        }

        for (Region r : regions) {
            Region newRegion =
                new Region(r.getId(), newMap.getSuperRegion(r.getSuperRegion().getId()), r.getOwner(),
                    r.getArmies());
            newMap.add(newRegion);
        }

        for (Region r : regions) {
            Region newRegion = newMap.getRegion(r.getId());
            for (Region neighbor : r.getNeighbors()) {
                newRegion.addNeighbor(newMap.getRegion(neighbor.getId()));
            }
        }

        return newMap;
    }

    /**
     * @return : the list of all Regions in this map
     */
    public LinkedList<Region> getRegions() {
        return regions;
    }

    /**
     * @return : the list of all SuperRegions in this map
     */
    public LinkedList<SuperRegion> getSuperRegions() {
        return superRegions;
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

    /**
     * @param id : a Region id number
     * @return : the matching Region object
     */
    public Region getRegion(int id) {
        for (Region region : regions)
            if (region.getId() == id) {
                return region;
            }
        return null;
    }

    /**
     * @param id : a SuperRegion id number
     * @return : the matching SuperRegion object
     */
    public SuperRegion getSuperRegion(int id) {
        for (SuperRegion superRegion : superRegions)
            if (superRegion.getId() == id) {
                return superRegion;
            }
        return null;
    }

    public String getMapString() {
        String mapString = "";
        for (Region region : regions) {
            mapString =
                mapString.concat(region.getId() + ";" + region.getOwner() + ";" + region.getArmies() + " ");
        }
        return mapString;
    }
}
