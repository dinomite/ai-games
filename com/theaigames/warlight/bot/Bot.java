package com.theaigames.warlight.bot;

import java.util.ArrayList;

import com.theaigames.warlight.map.Region;
import com.theaigames.warlight.move.AttackTransferMove;
import com.theaigames.warlight.move.PlaceArmiesMove;

public interface Bot {
    /**
     * A method used at the start of the game to decide which player start with what Regions. 6 Regions are required to
     * be returned.
     *
     * @return A list of m (m=6) {@link Region}s starting with the most preferred Region and ending with the least preferred
     *         Region to start with
     */
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut);

    /**
     * This method is called for at first part of each round.
     *
     * @return The list of {@link PlaceArmiesMove}s for one round
     */
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut);

    /**
     * This method is called for at the second part of each round.
     *
     * @return The list of {@link AttackTransferMove}s for one round
     */
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut);
}
