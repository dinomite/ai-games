package bot;

import com.theaigames.warlight.bot.Bot;
import com.theaigames.warlight.bot.BotParser;
import com.theaigames.warlight.bot.BotState;
import com.theaigames.warlight.map.Region;
import com.theaigames.warlight.map.SuperRegion;
import com.theaigames.warlight.move.AttackTransferMove;
import com.theaigames.warlight.move.PlaceArmiesMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

public class BotStarter implements Bot {
    private final float MIN_ATTACK_RATIO = 3;
    private final int STARTING_REGIONS = 6;

    public BotStarter() {
    }

    @Override
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut) {
        ArrayList<Region> preferredStartingRegions = new ArrayList<>();
        for (int i = 0; i < STARTING_REGIONS; i++) {
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

        // Sort the regions by the one we have the largest ownership share of (i.e. easiest to capture, in a naive sense)
//        LinkedList<SuperRegion> superRegions = state.getVisibleMap().getSuperRegions();
//        Collections.sort(superRegions, new SuperRegionOwnershipShareComparator(myName));

        for (SuperRegion superRegion : getSuperRegionsByHighestOwnership(state, myName)) {
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

        System.err.println("=> Round " + state.getRoundNumber() + ": " + placeArmiesMoves.size() + " placement moves");
        return placeArmiesMoves;
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<>();
        String myName = state.getMyPlayerName();

        for (SuperRegion superRegion : getSuperRegionsByHighestOwnership(state, myName)) {
            // Skip regions we already own
            if (superRegion.ownedByPlayer(myName)) {
                continue;
            }

            for (Region myRegion : superRegion.getOwnedRegions(myName)) {
                int commitedArmies = 0;

                // Regions with neutral neighbors
                LinkedList<Region> unownedNeighbors = myRegion.getNeighborsNotOwned(myName);
                for (Region regionToAttack : unownedNeighbors) {
                    int availableArmies = myRegion.getArmies() - commitedArmies - 1;
                    int neededToAttack = (int) Math.ceil(regionToAttack.getArmies() * MIN_ATTACK_RATIO);
                    if (availableArmies >= neededToAttack) {
                        attackTransferMoves.add(new AttackTransferMove(myName, myRegion, regionToAttack, neededToAttack));
                    }
                }
            }
        }
        // TODO move armies that are in regions surrounded by owned regions

        // TODO attack opponents, not just neutrals

        return attackTransferMoves;
    }

    /**
     * Get the super regions we have a presence in, ordered by the most ownership to least
     *
     * @param state
     * @param myName
     * @return
     */
    private ArrayList<SuperRegion> getSuperRegionsByHighestOwnership(BotState state, String myName) {
        // Make a map of SuperRegions we have a presence in, but don't own
        SortedMap<Float, SuperRegion> superRegionsByOwnership = new TreeMap<>();
        for (SuperRegion superRegion : state.getVisibleMap().getSuperRegions()) {
            float ownershipShare = superRegion.ownershipShare(myName);
            System.err.println("SuperRegion " + superRegion.getId() + " ownership share: " + ownershipShare);
            if (!superRegion.ownedByPlayer(myName)) {
                superRegionsByOwnership.put(ownershipShare, superRegion);
            }
        }

        // Sort from highest owned to lowest
        ArrayList<SuperRegion> superRegions = new ArrayList<>(superRegionsByOwnership.values());
        Collections.reverse(superRegions);
        return superRegions;
    }

    private int armiesNeeded(Region region) {
        return (int) Math.ceil((MIN_ATTACK_RATIO * region.getArmies()));
    }

    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter(), new Scanner(System.in), new BotState());
        parser.run();
    }
}
