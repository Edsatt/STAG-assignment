package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BasicCommandTests {
    GameServer testServer;
    Map map;

    public BasicCommandTests(){}
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "test1-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "test1-actions.xml").toAbsolutePath().toFile();
        testServer = new GameServer(entitiesFile, actionsFile);
        map = testServer.getMap();
    }

    @Test
    void testInventoryCommand(){
        assertEquals(testServer.handleCommand("ed: inventory"), "Your inventory is empty");
        PlayerCharacter ed = map.getCurrentPlayer();
        assertTrue(ed.getInventory().isEmpty());
        assertEquals(testServer.handleCommand("ed: get axe"), "You pick up the Shiny axe");
        assertTrue(ed.getInventory().containsKey("axe"));
        assertFalse(map.getCurrentLocation().getArtefacts().containsKey("axe"));
        String response1 = testServer.handleCommand("ed: inventory potion");
        assertTrue(response1.contains("Error"));
        String response2 = testServer.handleCommand("ed: inventory please");
        assertTrue(response2.contains("You are holding the following items:"));
    }

    @Test
    void testLookCommand(){
        String response1 = testServer.handleCommand("ed: look");
        assertTrue(response1.contains("cabin"));
        String response2 = testServer.handleCommand("ed: look forest");
        assertTrue(response2.contains("Error"));
        String response3 = testServer.handleCommand("ed: look around");
        assertTrue(response3.contains("cabin"));
        testServer.handleCommand("ed: goto forest");
        String response4 = testServer.handleCommand("ed: look");
        assertTrue(response4.contains("forest"));
    }

    @Test
    void testGetCommand(){
        testServer.handleCommand("ed: inv");
        PlayerCharacter ed = map.getCurrentPlayer();
        assertTrue(ed.getInventory().isEmpty());
        testServer.handleCommand("ed: get axe");
        assertTrue(ed.getInventory().containsKey("axe"));
        assertFalse(map.getCurrentLocation().getArtefacts().containsKey("axe"));
        assertEquals(testServer.handleCommand("ed: get"),"Error: Get must be followed by an item in the current location");
        assertEquals(testServer.handleCommand("ed: get key"),"Error: Get must be followed by an item in the current location");
    }

    @Test
    void testDropCommand(){
        testServer.handleCommand("ed: look");
        PlayerCharacter ed = map.getCurrentPlayer();
        testServer.handleCommand("ed: get axe");
        assertTrue(ed.getInventory().containsKey("axe"));
        testServer.handleCommand("ed: goto forest");
        testServer.handleCommand("ed: look");
        assertEquals(map.getCurrentLocation().getId(),"forest");
        assertFalse(map.getCurrentLocation().getArtefacts().containsKey("axe"));
        assertEquals(testServer.handleCommand("ed: drop axe"),"You drop the Shiny axe");
        assertFalse(ed.getInventory().containsKey("axe"));
        assertTrue(map.getCurrentLocation().getArtefacts().containsKey("axe"));
    }

    @Test
    void testDropCommand2(){
        assertTrue(testServer.handleCommand("ed: drop axe").contains("Error"), "axe not in inventory");
    }

    @Test
    void testGotoCommand(){
        testServer.handleCommand("ed: look");
        String response1 = testServer.handleCommand("ed: goto forest");
        assertTrue(response1.contains("forest"));
        String response2 = testServer.handleCommand("ed: goto cellar");
        assertTrue(response2.contains("Error"));
        String response3 = testServer.handleCommand("ed: goto cabin axe");
        assertTrue(response3.contains("Error"));
    }

    @Test
    void testHealthCommand(){
        String response1 = testServer.handleCommand("ed: health");
        assertTrue(response1.contains("3/3"), "health should be full");
        String response2 = testServer.handleCommand("ed: health axe");
        assertTrue(response2.contains("Error"), "should result in error");
    }
}
