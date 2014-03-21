package com.theaigames.warlight.map;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class SuperRegionTest {
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
    public void testAddSubRegion() {
        int anotherId = 9;
        Region anotherRegion = new Region(anotherId, superRegion);
        superRegion.addSubRegion(anotherRegion);
        assertTrue(superRegion.getSubRegions().contains(anotherRegion));
    }

    @Test
    public void testOwnedByPlayer_NoOwner_ReturnsFalse() {
        assertFalse(superRegion.ownedByPlayer(owner));
    }

    @Test
    public void testOwnedByPlayer_ReturnsTrue() {
        region.setOwner(owner);
        assertTrue(superRegion.ownedByPlayer(owner));
    }

    @Test
    public void testOwnershipShare() {
        region.setOwner(owner);
        assertEquals(1.0f, superRegion.ownershipShare(owner), 0);
    }

    @Test
    public void testGetId() {
        assertEquals(superRegionId, superRegion.getId());
    }

    @Test
    public void testGetArmiesReward() {
        assertEquals(armiesReward, superRegion.getArmiesReward());
    }

    @Test
    public void testGetSubRegions() {
        LinkedList<Region> subRegions = superRegion.getSubRegions();
        assertEquals(1, subRegions.size());
        assertTrue(subRegions.contains(region));
    }
}
