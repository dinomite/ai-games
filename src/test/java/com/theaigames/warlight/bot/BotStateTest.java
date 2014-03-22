package com.theaigames.warlight.bot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class BotStateTest {
    BotState botState;

    @Before
    public void setUp() {
        botState = new BotState();
    }

    @Test
    public void testUpdateSettings_YourBot() {
        String botName = "foobar";
        botState.updateSettings("your_bot", botName);

        assertEquals(botName, botState.getMyPlayerName());
    }

    @Test
    public void testUpdateSettings_OpponentBot() {
        String botName = "barbaz";
        botState.updateSettings("opponent_bot", botName);

        assertEquals(botName, botState.getOpponentPlayerName());
    }
}
