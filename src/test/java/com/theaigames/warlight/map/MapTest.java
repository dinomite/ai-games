package com.theaigames.warlight.map;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class MapTest {
    private int superRegionId = 0;
    private int armiesReward = 9;

    private int regionId = 7;
    private String owner = "fooplayer";

    private Map map;
    private SuperRegion superRegion;
    private Region region;

    @Before
    public void setUp() {
        map = new Map();

        superRegion = new SuperRegion(superRegionId, armiesReward);
        region = new Region(regionId, superRegion);

        map.add(region);
        map.add(superRegion);
    }

    @Test
    public void testAddRegion() {
        map.add(new Region(1, superRegion));
        assertEquals(2, map.getRegions().size());
    }

    @Test
    public void testAddSuperRegion() {
        map.add(new SuperRegion(1, 4));
        assertEquals(2, map.getSuperRegions().size());
    }

    @Test
    public void testGetMapCopy() {
        map.add(new Region(1, superRegion));
        map.add(new SuperRegion(1, 4));

        Map mapCopy = map.getMapCopy();
        assertEquals(map.getRegions().size(), mapCopy.getRegions().size());
        assertEquals(map.getSuperRegions().size(), mapCopy.getSuperRegions().size());
    }

    @Test
    public void testGetRegions() {
        LinkedList<Region> regions = map.getRegions();
        assertEquals(1, regions.size());
        assertTrue(regions.contains(region));
    }

    @Test
    public void testGetSuperRegions() {
        LinkedList<SuperRegion> superRegions = map.getSuperRegions();
        assertEquals(1, superRegions.size());
        assertTrue(superRegions.contains(superRegion));
    }

    @Test
    public void testGetOwnedRegions() {
        Region anotherRegion = new Region(1, superRegion);
        map.add(anotherRegion);

        region.setOwner(owner);

        LinkedList<Region> ownedRegions = map.getOwnedRegions(owner);
        assertEquals(1, ownedRegions.size());
        assertTrue(ownedRegions.contains(region));
        assertFalse(ownedRegions.contains(anotherRegion));
    }

    @Test
    public void testGetRegion() {
        Region retrieved = map.getRegion(regionId);
        assertEquals(region.getId(), retrieved.getId());
    }

    @Test
    public void testGetSuperRegion() {
        SuperRegion retrieved = map.getSuperRegion(superRegionId);
        assertEquals(superRegion.getId(), retrieved.getId());
    }

    @Test
    public void testGetMapString() {
        String mapString = map.getMapString();
        assertEquals(regionId + ";" + region.getOwner() + ";" + region.getArmies() + " ", mapString);
    }
}
