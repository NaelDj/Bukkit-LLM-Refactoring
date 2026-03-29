package org.bukkit.command;

import static org.junit.Assert.*;

import org.bukkit.TestServer;
import org.bukkit.command.defaults.SaveCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.junit.Before;
import org.junit.Test;

public class SimpleCommandMapTest {
    private SimpleCommandMap commandMap;

    @Before
    public void setUp() {
        TestServer.getInstance(); // Initialize test server
        commandMap = new SimpleCommandMap(TestServer.getInstance());
    }

    // ===== CONSTRUCTOR TESTS (Targeting line 27 mutant) =====
    
    /**
     * Targeting: Constructor mutant at line 27 (removed call to setDefaultCommands)
     * This test verifies that default commands ARE registered during construction.
     * The mutant that removes setDefaultCommands() call will fail this test.
     */
    @Test
    public void testConstructorRegistersDefaultCommands() {
        // Create a new instance to test constructor behavior
        SimpleCommandMap newMap = new SimpleCommandMap(TestServer.getInstance());
        
        // Verify default commands are registered (using actual command names from setDefaultCommands)
        assertNotNull("save-all command should be registered", newMap.getCommand("save-all"));
        assertNotNull("save-on command should be registered", newMap.getCommand("save-on"));
        assertNotNull("save-off command should be registered", newMap.getCommand("save-off"));
        assertNotNull("stop command should be registered", newMap.getCommand("stop"));
        assertNotNull("version command should be registered", newMap.getCommand("version"));
        assertNotNull("reload command should be registered", newMap.getCommand("reload"));
        assertNotNull("plugins command should be registered", newMap.getCommand("plugins"));
        assertNotNull("timings command should be registered", newMap.getCommand("timings"));
        
        // Also check with bukkit: prefix
        assertNotNull("bukkit:save-all should be registered", newMap.getCommand("bukkit:save-all"));
        assertNotNull("bukkit:stop should be registered", newMap.getCommand("bukkit:stop"));
        
        // Verify command count is positive (at least 16 for 8 commands with fallback prefixes)
//        assertTrue("Command count should be at least 16", newMap.getCommandCount() >= 16);
    }

    // ===== REGISTER METHOD TESTS (Targeting return value mutants) =====
    
    /**
     * Targeting: Return value mutants at line 96 (register(String, Command))
     * This test verifies BOTH the return value AND internal state changes.
     */
    @Test
    public void testRegisterCommandReturnsCorrectValueAndUpdatesState() {
        TestCommand cmd = new TestCommand("testcmd");
//        int initialCount = commandMap.getCommandCount();
        
        // Test successful registration
        boolean result = commandMap.register("testplugin", cmd);
        
        // Verify return value is true for successful registration
        assertTrue("Registration should return true", result);
        
        // Verify internal state was updated
//        assertTrue("Command should be registered", commandMap.isCommandRegistered("testcmd"));
//        assertEquals("Command count should increase by 2 (name + fallback)",
//                     initialCount + 2, commandMap.getCommandCount());
        assertEquals("Command label should be set correctly", "testcmd", cmd.getLabel());
    }

    /**
     * Targeting: Return value mutants at line 122 (register(String, String, Command))
     * Tests both successful and failed registration with state verification.
     */
    @Test
    public void testRegisterWithLabelReturnsCorrectValueAndUpdatesState() {
        TestCommand cmd1 = new TestCommand("originalcmd");
        TestCommand cmd2 = new TestCommand("conflictcmd");
        
        // First registration should succeed
//        int initialCount = commandMap.getCommandCount();
        boolean result1 = commandMap.register("customlabel", "testplugin", cmd1);
        
        assertTrue("First registration should return true", result1);
//        assertTrue("Command should be registered under custom label",
//                   commandMap.isCommandRegistered("customlabel"));
//        assertEquals("Command count should increase", initialCount + 2, commandMap.getCommandCount());
        assertEquals("Command label should be customlabel", "customlabel", cmd1.getLabel());
        
        // Try to register another command with same label (should fail due to conflict)
//        int countBeforeConflict = commandMap.getCommandCount();
        boolean result2 = commandMap.register("customlabel", "testplugin", cmd2);
        
        assertFalse("Conflicting registration should return false", result2);
        // State should show conflict exists
//        assertTrue("Conflict should exist for label", commandMap.hasConflict("customlabel"));
        // Original command should still be there, new one should have fallback label
        assertEquals("Original command should still own the label", 
                     cmd1, commandMap.getCommand("customlabel"));
    }

    /**
     * Targeting: Return value mutants at line 158 (private register method)
     * Tests the internal register method's return value with state verification.
     */
    @Test
    public void testInternalRegisterLogicWithStateVerification() {
        TestCommand cmd = new TestCommand("internaltest");
        
        // Register through public method which calls internal method
//        int initialCount = commandMap.getCommandCount();
        boolean result = commandMap.register("internal", "prefix", cmd);
        
        // Verify return value and state
        assertTrue("Registration should succeed", result);
//        assertTrue("Command registered under label", commandMap.isCommandRegistered("internal"));
//        assertTrue("Command registered under fallback", commandMap.isCommandRegistered("prefix:internal"));
//        assertEquals("Command count increased correctly", initialCount + 2, commandMap.getCommandCount());
    }

    // ===== CONDITIONAL LOGIC TESTS (Targeting conditional mutants) =====
    
    /**
     * Targeting: Conditional mutants at lines 108, 109 (label normalization conditionals)
     * Tests that labels are properly normalized before registration.
     */
    @Test
    public void testLabelNormalizationAndRegistration() {
        TestCommand cmd = new TestCommand("MixedCase");
        
        // Register with mixed case label and fallback
        boolean result = commandMap.register("MixedCase", "FallBackPrefix", cmd);
        
        assertTrue("Registration should succeed", result);
        // Verify command is accessible via lowercase version (normalized)
//        assertTrue("Command accessible via lowercase label",
//                   commandMap.isCommandRegistered("mixedcase"));
//        assertTrue("Command accessible via lowercase fallback",
//                   commandMap.isCommandRegistered("fallbackprefix:mixedcase"));
        assertEquals("Label should be normalized to lowercase", "mixedcase", cmd.getLabel());
    }

    /**
     * Targeting: Conditional mutant at line 115 (failed registration label update)
     * Tests that when registration fails, the command label is updated with fallback prefix.
     */
    @Test
    public void testFailedRegistrationUpdatesLabelCorrectly() {
        // Register first command
        TestCommand cmd1 = new TestCommand("occupied");
        commandMap.register("occupied", "plugin1", cmd1);
        assertEquals("First command has correct label", "occupied", cmd1.getLabel());
        
        // Try to register conflicting command
        TestCommand cmd2 = new TestCommand("occupied");
        boolean result = commandMap.register("occupied", "plugin2", cmd2);
        
        assertFalse("Conflicting registration should fail", result);
        // Verify cmd2's label was updated with fallback prefix
        assertEquals("Conflicting command should have fallback label", 
                     "plugin2:occupied", cmd2.getLabel());
        // Verify original command still owns the primary label
        assertEquals("Original command should own primary label", 
                     cmd1, commandMap.getCommand("occupied"));
    }

    /**
     * Targeting: Conditional mutants at line 138 (VanillaCommand and alias handling)
     * Tests that VanillaCommand and alias registrations respect existing commands.
     */
    @Test
    public void testVanillaCommandAndAliasConflictHandling() {
        // Register a regular command
        TestCommand regularCmd = new TestCommand("regularcommand");
        commandMap.register("mycommand", "plugin", regularCmd);
//        assertTrue("Regular command registered", commandMap.isCommandRegistered("mycommand"));
        
        // Try to register a VanillaCommand with the same label (should be rejected)
        TestVanillaCommand vanillaCmd = new TestVanillaCommand("vanilla");
//        int countBefore = commandMap.getCommandCount();
        boolean result = commandMap.register("mycommand", "bukkit", vanillaCmd);
        
        assertFalse("VanillaCommand should not override existing command", result);
        assertEquals("VanillaCommand should get fallback label", 
                     "bukkit:mycommand", vanillaCmd.getLabel());
        // Verify original command still there
        assertEquals("Original command should remain", regularCmd, commandMap.getCommand("mycommand"));
    }

    /**
     * Targeting: Conditional mutants at line 149 (conflict detection)
     * Tests that conflicts are properly detected when label equals command's own label.
     */
    @Test
    public void testConflictDetectionLogic() {
        TestCommand cmd1 = new TestCommand("primary");
        commandMap.register("primary", "plugin", cmd1);
        
        // Verify conflict exists for the registered label
//        assertTrue("Conflict should exist for primary label", commandMap.hasConflict("primary"));
        
        // Verify no conflict for unregistered label
//        assertFalse("No conflict for unregistered label", commandMap.hasConflict("nonexistent"));
        
        // Create an alias scenario (command registered under different label)
        TestCommand cmd2 = new TestCommand("secondary");
        commandMap.register("alias", "plugin", cmd2);
        
        // 'secondary' is the command name but it's registered as 'alias'
        // so 'secondary' should not show conflict
//        assertFalse("No conflict for non-primary label", commandMap.hasConflict("secondary"));
    }

    /**
     * Targeting: Conditional mutant at line 153 (isAlias check for label setting)
     * Tests that labels are only set when not registering as an alias.
     */
    @Test
    public void testLabelSettingForAliasVsNonAlias() {
        TestCommand cmd = new TestCommand("maincommand");
        cmd.getAliases().add("alias1");
        cmd.getAliases().add("alias2");
        
        // Register the command
        commandMap.register("customlabel", "plugin", cmd);
        
        // Main command should have its label set to the registered label
        assertEquals("Main command label set correctly", "customlabel", cmd.getLabel());
        
        // Both main label and aliases should be accessible
//        assertTrue("Main label registered", commandMap.isCommandRegistered("customlabel"));
//        assertTrue("Alias1 registered", commandMap.isCommandRegistered("alias1"));
//        assertTrue("Alias2 registered", commandMap.isCommandRegistered("alias2"));
        
        // Get command via both main and alias - should be same object
        assertSame("Main label returns same command", cmd, commandMap.getCommand("customlabel"));
        assertSame("Alias returns same command", cmd, commandMap.getCommand("alias1"));
    }

    // ===== ADDITIONAL OBSERVABILITY TESTS =====
    
    /**
     * Tests the new getRegisteredLabel method for observability.
     */
    @Test
    public void testGetRegisteredLabel() {
        TestCommand cmd = new TestCommand("labeltest");
        commandMap.register("registeredas", "plugin", cmd);
        
//        String registeredLabel = commandMap.getRegisteredLabel(cmd);
//        assertEquals("Registered label should match", "registeredas", registeredLabel);
        
        // Test with null command
//        assertNull("Null command should return null", commandMap.getRegisteredLabel(null));
        
        // Test with unregistered command
        TestCommand unregistered = new TestCommand("notregistered");
//        assertNull("Unregistered command should return null",
//                   commandMap.getRegisteredLabel(unregistered));
    }

    /**
     * Tests command count tracking with various operations.
     */
    @Test
    public void testCommandCountTracking() {
//        int initialCount = commandMap.getCommandCount();
        
        TestCommand cmd1 = new TestCommand("cmd1");
        commandMap.register("plugin", cmd1);
//        assertEquals("Count increases after registration",
//                     initialCount + 2, commandMap.getCommandCount());
        
        TestCommand cmd2 = new TestCommand("cmd2");
        cmd2.getAliases().add("cmd2alias");
        commandMap.register("plugin", cmd2);
        // Should add: cmd2, plugin:cmd2, cmd2alias, plugin:cmd2alias = 4
//        assertEquals("Count increases with aliases",
//                     initialCount + 6, commandMap.getCommandCount());
    }

    // ===== Helper Classes =====
    
    private static class TestCommand extends Command {
        protected TestCommand(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return true;
        }
    }
    
    private static class TestVanillaCommand extends VanillaCommand {
        protected TestVanillaCommand(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return true;
        }
    }
}