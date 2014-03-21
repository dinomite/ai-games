package com.theaigames.warlight.bot;

import java.util.ArrayList;
import java.util.Scanner;

import com.theaigames.warlight.map.Region;
import com.theaigames.warlight.move.PlaceArmiesMove;
import com.theaigames.warlight.move.AttackTransferMove;

public class BotParser {
    private final Scanner scan;
    private final Bot bot;
    private final BotState currentState;

    public BotParser(Bot bot) {
        this.bot = bot;

        this.scan = new Scanner(System.in);
        this.currentState = new BotState();
    }

    public void run() {
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.length() == 0) {
                continue;
            }

            String[] parts = line.split(" ");
            if (parts[0].equals("pick_starting_regions")) {
                pickStartingRegions(parts);
            } else if (parts.length == 3 && parts[0].equals("go")) {
                //we need to do a move
                String output = "";
                if (parts[1].equals("place_armies")) {
                    //place armies
                    ArrayList<PlaceArmiesMove> placeArmiesMoves =
                        bot.getPlaceArmiesMoves(currentState, Long.valueOf(parts[2]));
                    for (PlaceArmiesMove move : placeArmiesMoves)
                        output = output.concat(move.getString() + ",");
                } else if (parts[1].equals("attack/transfer")) {
                    //attack/transfer
                    ArrayList<AttackTransferMove> attackTransferMoves =
                        bot.getAttackTransferMoves(currentState, Long.valueOf(parts[2]));
                    for (AttackTransferMove move : attackTransferMoves)
                        output = output.concat(move.getString() + ",");
                }
                if (output.length() > 0) {
                    System.out.println(output);
                } else {
                    System.out.println("No moves");
                }
            } else if (parts.length == 3 && parts[0].equals("settings")) {
                currentState.updateSettings(parts[1], parts[2]);
            } else if (parts[0].equals("setup_map")) {
                currentState.setupMap(parts);
            } else if (parts[0].equals("update_map")) {
                currentState.updateMap(parts);
            } else if (parts[0].equals("opponent_moves")) {
                currentState.readOpponentMoves(parts);
            } else {
                System.err.printf("Unable to parse line \"%s\"\n", line);
            }
        }
    }

    private void pickStartingRegions(String[] parts) {
        //pick which regions you want to start with
        currentState.setPickableStartingRegions(parts);
        ArrayList<Region> preferredStartingRegions =
            bot.getPreferredStartingRegions(currentState, Long.valueOf(parts[1]));
        String output = "";
        for (Region region : preferredStartingRegions)
            output = output.concat(region.getId() + " ");

        System.out.println(output);
    }
}
