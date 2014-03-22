package com.theaigames.warlight.bot;

import bot.BotStarter;
import com.theaigames.warlight.map.Map;
import org.junit.Before;
import org.junit.Test;

import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public final class BotParserTest {
    private final String YOUR_BOT = "settings your_bot player2";
    private final String OPPONENTS_BOT = "settings opponent_bot player1";
    private final String SUPER_REGIONS = "setup_map super_regions 1 5 2 2 3 5 4 3 5 7 6 2";
    private final String REGIONS = "setup_map regions 1 1 2 1 3 1 4 1 5 1 6 1 7 1 8 1 9 1 10 2 11 2 12 2 13 2 14 3 15 3 16 3 17 3 18 3 19 3 20 3 21 4 22 4 23 4 24 4 25 4 26 4 27 5 28 5 29 5 30 5 31 5 32 5 33 5 34 5 35 5 36 5 37 5 38 5 39 6 40 6 41 6 42 6";
    private final String NEIGHBORS = "setup_map neighbors 1 2,4,30 2 4,3,5 3 5,6,14 4 5,7 5 6,7,8 6 8 7 8,9 8 9 9 10 10 11,12 11 12,13 12 13,21 14 15,16 15 16,18,19 16 17 17 19,20,27,32,36 18 19,20,21 19 20 20 21,22,36 21 22,23,24 22 23,36 23 24,25,26,36 24 25 25 26 27 28,32,33 28 29,31,33,34 29 30,31 30 31,34,35 31 34 32 33,36,37 33 34,37,38 34 35 36 37 37 38 38 39 39 40,41 40 41,42 41 42";
    private final String PICK_STARTING_REGIONS = "pick_starting_regions 2000 5 9 12 13 20 16 26 25 38 31 39 42";
    private final String STARTING_ARMIES = "settings starting_armies 5";
    private final String UPDATE_MAP = "update_map 20 player2 2 31 player2 2 38 player2 2 17 neutral 2 18 neutral 2 19 neutral 2 21 neutral 2 22 neutral 2 36 neutral 2 28 neutral 2 29 neutral 2 30 neutral 2 34 neutral 2 33 neutral 2 37 neutral 2 39 neutral 2";

    private BotParser botParser;
    private BotStarter bot;
    private BotState botState;

    @Before
    public void setUp() {
        bot = new BotStarter();
        botState = new BotState();
    }

    @Test
    public void testBotNames() {
        Scanner scanner = new Scanner(YOUR_BOT + "\n" + OPPONENTS_BOT);
        botParser = new BotParser(bot, scanner, botState);
        botParser.run();

        assertEquals("player2", botState.getMyPlayerName());
        assertEquals("player1", botState.getOpponentPlayerName());
    }

    @Test
    public void testSuperRegions() {
        Scanner scanner = new Scanner(YOUR_BOT + "\n" + OPPONENTS_BOT + "\n" + SUPER_REGIONS);
        botParser = new BotParser(bot, scanner, botState);
        botParser.run();

        Map fullMap = botState.getFullMap();
        assertEquals(5, fullMap.getSuperRegion(1).getArmiesReward());
        assertEquals(2, fullMap.getSuperRegion(2).getArmiesReward());
        assertEquals(5, fullMap.getSuperRegion(3).getArmiesReward());
        assertEquals(3, fullMap.getSuperRegion(4).getArmiesReward());
        assertEquals(7, fullMap.getSuperRegion(5).getArmiesReward());
        assertEquals(2, fullMap.getSuperRegion(6).getArmiesReward());
    }
}
