package bot;

import com.theaigames.warlight.bot.Bot;
import com.theaigames.warlight.bot.BotParser;
import com.theaigames.warlight.bot.BotState;
import com.theaigames.warlight.map.Region;
import com.theaigames.warlight.map.SuperRegion;
import com.theaigames.warlight.move.AttackTransferMove;
import com.theaigames.warlight.move.PlaceArmiesMove;
import net.dinomite.theaigames.warlight.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BotStarter implements Bot {
    private final int MIN_ATTACK_ARMIES = 2;
    private final float MIN_ATTACK_RATIO = 2;
    private final int NUM_STARTING_REGIONS = 6;

    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter(), new Scanner(System.in), new BotState());
        parser.run();
    }

    @Override
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut) {
        ArrayList<Region> preferredStartingRegions = new ArrayList<>();
        for (int i = 0; i < NUM_STARTING_REGIONS; i++) {
            double rand = Math.random();
            int r = (int) (rand * state.getPickableStartingRegions().size());
            int regionId = state.getPickableStartingRegions().get(r).getId();
            Region region = state.getFullMap().getRegion(regionId);

            if (!preferredStartingRegions.contains(region)) {
                preferredStartingRegions.add(region);
            } else {
                i--;
            }
        }

        return preferredStartingRegions;
    }

    @Override
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {
        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<>();
        String myName = state.getMyPlayerName();
        int armiesLeft = state.getStartingArmies();

        // Place armies in unowned SuperRegions to take them over
        for (SuperRegion superRegion : getSuperRegionsByHighestOwnership(state, myName)) {
            if (superRegion.ownedByPlayer(myName)) {
                continue;
            }

            for (Region region : superRegion.getOwnedRegions(myName)) {
                if (armiesLeft <= 0) {
                    break;
                }

                // Regions with neutral neighbors
                LinkedList<Region> neutralNeighbors = region.getNeutralNeighbors();
                if (neutralNeighbors.size() != 0) {
                    // Give the region enough armies to attack a neutral neighbor
                    int armiesToPlace = armiesNeeded(neutralNeighbors.getFirst()) - region.getArmies() + 1;

                    // Don't place zero or negative armies
                    if (armiesToPlace <= 0) {
                        continue;
                    }

                    // Only place available armies
                    if (armiesToPlace > armiesLeft) {
                        armiesToPlace = armiesLeft;
                    }

                    System.err.println("Placing " + armiesToPlace + " in region " + region.getId() + " within super region " + superRegion.getId());
                    placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armiesToPlace));
                    armiesLeft = armiesLeft - armiesToPlace;
                }
            }
        }

        // Place remaining armies on edges of SuperRegions we own
        // TODO distribute armies to edges of owned regions
        if (armiesLeft > 0) {
            for (Region region : state.getVisibleMap().getOwnedRegions(myName)) {
                // We own all of this region's neighbors
                Set<Region> neighorsOwnedByOpponent = region.getNeighorsOwnedBy(state.getOpponentPlayerName());
                if (neighorsOwnedByOpponent.size() > 0) {
                    placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armiesLeft));
                }
            }
        }

        System.err.println("=> Round " + state.getRoundNumber() + ": " + placeArmiesMoves.size() + " placement moves");
        return placeArmiesMoves;
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<>();
        String myName = state.getMyPlayerName();
        int commitedArmies = 0;

        // Attack neutral regions
        for (SuperRegion superRegion : getSuperRegionsByHighestOwnership(state, myName)) {
            // Skip regions we already own
            if (superRegion.ownedByPlayer(myName)) {
                continue;
            }

            for (Region myRegion : superRegion.getOwnedRegions(myName)) {
                // Regions with neutral neighbors
                LinkedList<Region> unownedNeighbors = myRegion.getNeighborsNotOwned(myName);
                for (Region regionToAttack : unownedNeighbors) {
                    int availableArmies = myRegion.getArmies() - commitedArmies - 1;
                    int neededToAttack = (int) Math.ceil(regionToAttack.getArmies() * MIN_ATTACK_RATIO);
                    if (availableArmies < MIN_ATTACK_ARMIES || availableArmies < neededToAttack) {
                        break;
                    }

                    System.err.println("Attacking " + regionToAttack.getId() + " from " + myRegion + " with " + neededToAttack + " armies");
                    attackTransferMoves.add(new AttackTransferMove(myName, myRegion, regionToAttack, neededToAttack));
                    commitedArmies += neededToAttack;
                }
            }
        }

        // Attack opponents
        for (Region myRegion : state.getVisibleMap().getOwnedRegions(myName)) {
            Set<Region> opponentNeighbors = myRegion.getNeighorsOwnedBy(state.getOpponentPlayerName());
            for (Region regionToAttack : opponentNeighbors) {
                int availableArmies = myRegion.getArmies() - commitedArmies - 1;
                int neededToAttack = (int) Math.ceil(regionToAttack.getArmies() * MIN_ATTACK_RATIO);
                if (availableArmies >= neededToAttack) {
                    System.err.println("Attacking " + regionToAttack.getId() + " from " + myRegion + " with " + neededToAttack + " armies");
                    attackTransferMoves.add(new AttackTransferMove(myName, myRegion, regionToAttack, neededToAttack));
                    commitedArmies += neededToAttack;
                }
            }

        }

        // Move armies that are in regions surrounded by owned regions
        for (Region region : state.getVisibleMap().getOwnedRegions(myName)) {
            // We own all of this region's neighbors
            if (region.getNeighborsNotOwned(myName).size() == 0) {
                // Find the neighboring region with the most unowned neighbors
                Map<Region, Integer> numUnownedNeighborsOfNeighbors = new HashMap<>();
                for (Region neighbor : region.getNeighbors()) {
                    int numUnownedNeighbors = neighbor.getNeighborsNotOwned(myName).size();
                    numUnownedNeighborsOfNeighbors.put(neighbor, numUnownedNeighbors);
                }
                // Our neighbors, sorted by the number of neighbors they have which we don't own
                Set<Region> neighborsUnownedNeighbors = MapUtil.sortByValue(numUnownedNeighborsOfNeighbors).keySet();

                attackTransferMoves.add(new AttackTransferMove(myName, region, neighborsUnownedNeighbors.iterator().next(), region.getArmies() - 1));
            }
        }

        System.err.println("===> Round " + state.getRoundNumber() + ": " + attackTransferMoves.size() + " attack/transfer moves");
        return attackTransferMoves;
    }

    /**
     * Get the super regions we have a presence in, ordered by the most ownership to least
     *
     * @param state The current {@link BotState}
     * @param myName A player name to get the super region ownership share for
     * @return The super regions, sorted by the given player's ownership share
     */
    private Set<SuperRegion> getSuperRegionsByHighestOwnership(BotState state, String myName) {
        Map<SuperRegion, Float> superRegionOwnershipShares = new HashMap<>();
        for (SuperRegion superRegion : state.getVisibleMap().getSuperRegions()) {
            float ownershipShare = superRegion.ownershipShare(myName);
            superRegionOwnershipShares.put(superRegion, ownershipShare);
        }

        return MapUtil.sortByValue(superRegionOwnershipShares).keySet();
    }

    /**
     * Get the number of armies needed to take a region
     *
     * @param region The region to attack
     * @return The number of armies needed to takt eh given region
     */
    private int armiesNeeded(Region region) {
        return (int) Math.ceil((MIN_ATTACK_RATIO * region.getArmies()));
    }
}
