package bot;

import java.util.ArrayList;
import java.util.LinkedList;

import com.theaigames.warlight.bot.Bot;
import com.theaigames.warlight.bot.BotParser;
import com.theaigames.warlight.bot.BotState;
import com.theaigames.warlight.map.Region;
import com.theaigames.warlight.move.AttackTransferMove;
import com.theaigames.warlight.move.PlaceArmiesMove;

public class BotStarter implements Bot {
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

        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
        String myName = state.getMyPlayerName();
        int armies = 2;
        int armiesLeft = state.getStartingArmies();
        LinkedList<Region> visibleRegions = state.getVisibleMap().getRegions();

        while (armiesLeft > 0) {
            double rand = Math.random();
            int r = (int) (rand * visibleRegions.size());
            Region region = visibleRegions.get(r);

            if (region.ownedByPlayer(myName)) {
                placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armies));
                armiesLeft -= armies;
            }
        }

        return placeArmiesMoves;
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        String myName = state.getMyPlayerName();
        int armies = 5;

        for (Region fromRegion : state.getVisibleMap().getRegions()) {
            if (fromRegion.ownedByPlayer(myName)) //do an attack
            {
                ArrayList<Region> possibleToRegions = new ArrayList<Region>();
                possibleToRegions.addAll(fromRegion.getNeighbors());

                while (!possibleToRegions.isEmpty()) {
                    double rand = Math.random();
                    int r = (int) (rand * possibleToRegions.size());
                    Region toRegion = possibleToRegions.get(r);

                    if (!toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 6) //do an attack
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    } else if (toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 1) //do a transfer
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
