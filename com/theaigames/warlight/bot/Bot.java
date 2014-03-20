package com.theaigames.warlight.bot;

import java.util.ArrayList;

import com.theaigames.warlight.map.Region;
import com.theaigames.warlight.move.AttackTransferMove;
import com.theaigames.warlight.move.PlaceArmiesMove;

public interface Bot {
    
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut);
    
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut);
    
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut);

}
