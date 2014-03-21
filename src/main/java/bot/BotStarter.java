package bot;

import com.theaigames.warlight.bot.Bot;
import com.theaigames.warlight.bot.BotParser;
import com.theaigames.warlight.bot.BotState;
import com.theaigames.warlight.map.Region;
import com.theaigames.warlight.map.SuperRegion;
import com.theaigames.warlight.move.AttackTransferMove;
import com.theaigames.warlight.move.PlaceArmiesMove;
import net.dinomite.theaigames.warlight.SuperRegionOwnershipShareComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class BotStarter implements Bot {
    private final float MIN_ATTACK_RATIO = 3;

    private BotStarter() {
    }

    @Override
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut) {
        int m = 6;
        ArrayList<Region> preferredStartingRegions = new ArrayList<Region>();
        for (int i = 0; i < m; i++) {
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
        LinkedList<SuperRegion> superRegions = state.getVisibleMap().getSuperRegions();
        Collections.sort(superRegions, new SuperRegionOwnershipShareComparator(myName));

        while (armiesLeft > 0) {
            for (SuperRegion superRegion : superRegions) {
                // Find regions we own
                if (superRegion.ownedByPlayer(myName)) {
                    continue;
                }

                // That have neutral neighbors
                for (Region region : superRegion.getSubRegions()) {
                    LinkedList<Region> neutralNeighbors = region.getNeutralNeighbors();
                    if (region.ownedByPlayer(myName) && neutralNeighbors.size() != 0) {
                        // Give the region enough armies to attack a neutral neighbor
                        int armiesToPlace = armiesNeeded(neutralNeighbors.getFirst()) - region.getArmies() + 1;
                        placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armiesToPlace));
                    }
                }
            }
        }

        return placeArmiesMoves;
    }

    private int armiesNeeded(Region region) {
        return (int) Math.ceil((MIN_ATTACK_RATIO * region.getArmies()));
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<>();
        String myName = state.getMyPlayerName();
        int armies = 5;

        for (Region fromRegion : state.getVisibleMap().getRegions()) {
            if (fromRegion.ownedByPlayer(myName)) //do an attack
            {
                ArrayList<Region> possibleToRegions = new ArrayList<>();
                possibleToRegions.addAll(fromRegion.getNeighbors());

                while (!possibleToRegions.isEmpty()) {
                    double rand = Math.random();
                    int r = (int) (rand * possibleToRegions.size());
                    Region toRegion = possibleToRegions.get(r);

                    if (!toRegion.getOwner().equals(myName) && fromRegion.getArmies() > 6) //do an attack
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    } else if (toRegion.getOwner().equals(myName) && fromRegion.getArmies() > 1) //do a transfer
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    } else {
                        possibleToRegions.remove(toRegion);
                    }
                }
            }
        }

        return attackTransferMoves;
    }

    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }
}
