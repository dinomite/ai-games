package net.dinomite.theaigames.warlight;

import com.theaigames.warlight.map.SuperRegion;

import java.util.Comparator;

/**
 * Compare {@link SuperRegion}s with preference for the ones that the given player
 * controls more of
 */
public final class SuperRegionOwnershipShareComparator implements Comparator<SuperRegion> {
    private final String player;

    public SuperRegionOwnershipShareComparator(String player) {
        this.player = player;
    }

    @Override
    public int compare(SuperRegion one, SuperRegion two) {
        float oneShare = one.ownershipShare(player);
        float twoShare = two.ownershipShare(player);

        if (oneShare > twoShare) {
            return 1;
        } else if (oneShare < twoShare) {
            return -1;
        }

        return 0;
    }
}
