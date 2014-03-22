package com.theaigames.warlight.map;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class RegionTest {
    private int superRegionId = 0;
    private int armiesReward = 9;

    private int regionId = 7;
    private String owner = "fooplayer";

    private SuperRegion superRegion;
    private Region region;

    @Before
    public void setUp() {
        superRegion = new SuperRegion(superRegionId, armiesReward);
        region = new Region(regionId, superRegion);
    }

    @Test
    public void testSetArmies() {
        int armies = 11;
        region.setArmies(armies);
        assertEquals(armies, region.getArmies());
    }

    @Test
    public void testGetPlayerName() {
    }

    @Test
    public void testSetPlayerName() {
        region.setOwner(owner);
        assertEquals(owner, region.getOwner());
    }

    @Test
    public void testOwnedByPlayer_NotSet_ReturnsFalse() {
        assertFalse(region.ownedByPlayer(owner));
    }

    @Test
    public void testOwnedByPlayer_ReturnsTrue() {
        region.setOwner(owner);
        assertTrue(region.ownedByPlayer(owner));
    }

    @Test
    public void testGetId() {
        assertEquals(regionId, region.getId());
    }

    @Test
    public void testAddNeighbor() {
        Region neighbor = new Region(regionId + 1, superRegion);
        region.addNeighbor(neighbor);

        LinkedList<Region> neighbors = region.getNeighbors();
        assertEquals(1, neighbors.size());
        assertTrue(neighbors.contains(neighbor));
    }

    @Test
    public void testIsNeighbor() {
        Region neighbor = new Region(regionId + 1, superRegion);
        region.addNeighbor(neighbor);

        assertTrue(region.isNeighbor(neighbor));
    }

    @Test
    public void testGetNeighbors() {
        Region neighbor = new Region(regionId + 1, superRegion);
        region.addNeighbor(neighbor);

        Region anotherNeighbor = new Region(regionId + 1, superRegion);
        region.addNeighbor(anotherNeighbor);

        LinkedList<Region> neighbors = region.getNeighbors();
        assertEquals(2, neighbors.size());
        assertTrue(neighbors.contains(neighbor));
        assertTrue(neighbors.contains(anotherNeighbor));
    }

    @Test
    public void testGetNeutralNeighbors() {
        Region neutralNeighbor = new Region(regionId + 1, superRegion, "neutral", 2);
        region.addNeighbor(neutralNeighbor);

        Region ownedNeighbor = new Region(regionId + 1, superRegion, "opponent", 5);
        region.addNeighbor(ownedNeighbor);

        LinkedList<Region> neighbors = region.getNeutralNeighbors();
        assertEquals(1, neighbors.size());
        assertTrue(neighbors.contains(neutralNeighbor));
        assertFalse(neighbors.contains(ownedNeighbor));
    }

    @Test
    public void testGetSuperRegion() {
        assertEquals(superRegion, region.getSuperRegion());
    }
}
