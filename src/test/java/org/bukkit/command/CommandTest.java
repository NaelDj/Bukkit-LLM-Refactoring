package org.bukkit.command;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the Command class, focusing on observability of internal state
 * to distinguish correct behavior from mutated alternatives.
 */
public class CommandTest {
    
    private TestCommand command;
    private MockCommandMap commandMap1;
    private MockCommandMap commandMap2;
    
    /**
     * Concrete implementation of Command for testing purposes
     */
    private static class TestCommand extends Command {
        public TestCommand(String name) {
            super(name);
        }
        
        public TestCommand(String name, String description, String usageMessage, List<String> aliases) {
            super(name, description, usageMessage, aliases);
        }
        
        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return true;
        }
    }
    
    /**
     * Mock CommandMap for testing registration
     */
    private static class MockCommandMap implements CommandMap {
        @Override
        public void registerAll(String fallbackPrefix, List<Command> commands) {}
        
        @Override
        public boolean register(String label, String fallbackPrefix, Command command) {
            return false;
        }
        
        @Override
        public boolean register(String fallbackPrefix, Command command) {
            return false;
        }
        
        @Override
        public boolean dispatch(CommandSender sender, String cmdLine) throws CommandException {
            return false;
        }
        
        @Override
        public void clearCommands() {}
        
        @Override
        public Command getCommand(String name) {
            return null;
        }
        
        @Override
        public List<String> tabComplete(CommandSender sender, String cmdLine) throws IllegalArgumentException {
            return null;
        }
    }
    
    @Before
    public void setUp() {
        command = new TestCommand("test");
        commandMap1 = new MockCommandMap();
        commandMap2 = new MockCommandMap();
    }
    
    // ===== Tests for registration state observability (targeting isRegistered, register, allowChangesFrom mutants) =====
    
    /**
     * Target mutants: isRegistered line 256 (negated conditional, replaced boolean return with true)
     * Tests that isRegistered() correctly returns false when not registered.
     */
    @Test
    public void testIsRegisteredReturnsFalseWhenNotRegistered() {
        // Test that a new command is not registered
        assertFalse("Command should not be registered initially", command.isRegistered());
        
        // Use the new observable method to verify internal state
        assertNull("CommandMap should be null when not registered", command.getCommandMap());
    }
    
    /**
     * Target mutants: isRegistered line 256, register line 218 (negated conditional)
     * Tests that isRegistered() correctly returns true when registered.
     */
    @Test
    public void testIsRegisteredReturnsTrueWhenRegistered() {
        // Register the command
        boolean result = command.register(commandMap1);
        
        assertTrue("Registration should succeed", result);
        assertTrue("Command should be registered", command.isRegistered());
        
        // Use the new observable method to verify the SPECIFIC CommandMap
        assertSame("Should be registered to commandMap1", commandMap1, command.getCommandMap());
    }
    
    /**
     * Target mutants: register line 218 (negated conditional), line 220 (replaced boolean return with false)
     * Tests that register() returns true when registering to a new CommandMap.
     */
    @Test
    public void testRegisterReturnsTrueOnSuccessfulRegistration() {
        boolean result = command.register(commandMap1);
        
        assertTrue("Registration should return true", result);
        assertSame("Should be registered to the provided CommandMap", commandMap1, command.getCommandMap());
    }
    
    /**
     * Target mutants: register line 218 (negated conditional), allowChangesFrom line 247
     * Tests that register() returns false when trying to register to a different CommandMap.
     */
    @Test
    public void testRegisterReturnsFalseWhenAlreadyRegistered() {
        // First registration should succeed
        command.register(commandMap1);
        
        // Second registration to a different map should fail
        boolean result = command.register(commandMap2);
        
        assertFalse("Registration should return false when already registered", result);
        assertSame("Should still be registered to commandMap1", commandMap1, command.getCommandMap());
    }
    
    /**
     * Target mutants: allowChangesFrom line 247 (negated conditional, replaced boolean return with true)
     * Tests that registering to the same CommandMap is allowed.
     */
    @Test
    public void testRegisterAllowsSameCommandMap() {
        command.register(commandMap1);
        
        // Re-registering to the same map should succeed
        boolean result = command.register(commandMap1);
        
        assertTrue("Re-registration to same CommandMap should succeed", result);
        assertSame("Should still be registered to commandMap1", commandMap1, command.getCommandMap());
    }
    
    /**
     * Target mutant: unregister line 240 (replaced boolean return with false)
     * Tests that unregister() returns true when successfully unregistering.
     */
    @Test
    public void testUnregisterReturnsTrueOnSuccess() {
        command.register(commandMap1);
        
        boolean result = command.unregister(commandMap1);
        
        assertTrue("Unregister should return true on success", result);
        assertFalse("Command should no longer be registered", command.isRegistered());
        assertNull("CommandMap should be null after unregister", command.getCommandMap());
    }
    
    /**
     * Target mutant: unregister line 240 (replaced boolean return with false)
     * Tests that unregister() returns false when trying to unregister from wrong CommandMap.
     */
    @Test
    public void testUnregisterReturnsFalseOnFailure() {
        command.register(commandMap1);
        
        boolean result = command.unregister(commandMap2);
        
        assertFalse("Unregister should return false when wrong CommandMap", result);
        assertTrue("Command should still be registered", command.isRegistered());
        assertSame("Should still be registered to commandMap1", commandMap1, command.getCommandMap());
    }
    
    // ===== Tests for label change observability (targeting setLabel mutants) =====
    
    /**
     * Target mutants: setLabel line 202 (negated conditional), line 204 (replaced boolean return with false)
     * Tests that setLabel() immediately updates the label when not registered.
     */
    @Test
    public void testSetLabelUpdatesImmediatelyWhenNotRegistered() {
        String newLabel = "newlabel";
        
        boolean result = command.setLabel(newLabel);
        
        assertTrue("setLabel should return true when not registered", result);
        assertEquals("Label should be updated immediately", newLabel, command.getLabel());
        assertEquals("NextLabel should also be updated", newLabel, command.getNextLabel());
    }
    
    /**
     * Target mutants: setLabel line 202 (negated conditional), line 204 (replaced boolean return with false)
     * Tests that setLabel() defers the label update when registered.
     */
    @Test
    public void testSetLabelDefersUpdateWhenRegistered() {
        String originalLabel = "test";
        String newLabel = "newlabel";
        
        command.register(commandMap1);
        
        boolean result = command.setLabel(newLabel);
        
        assertFalse("setLabel should return false when registered", result);
        assertEquals("Label should remain unchanged", originalLabel, command.getLabel());
        assertEquals("NextLabel should be updated", newLabel, command.getNextLabel());
    }
    
    /**
     * Target mutants: setLabel line 202 (negated conditional)
     * Tests that unregistering applies the deferred label change.
     */
    @Test
    public void testUnregisterAppliesDeferredLabelChange() {
        String newLabel = "newlabel";
        
        command.register(commandMap1);
        command.setLabel(newLabel);
        command.unregister(commandMap1);
        
        assertEquals("Label should be updated after unregister", newLabel, command.getLabel());
        assertEquals("NextLabel should match label", newLabel, command.getNextLabel());
    }
    
    // ===== Tests for alias configuration observability (targeting setAliases mutants) =====
    
    /**
     * Target mutants: setAliases line 307 (negated conditional), line 310 (replaced return value with null)
     * Tests that setAliases() immediately updates active aliases when not registered.
     */
    @Test
    public void testSetAliasesUpdatesImmediatelyWhenNotRegistered() {
        List<String> newAliases = Arrays.asList("alias1", "alias2");
        
        Command result = command.setAliases(newAliases);
        
        assertNotNull("setAliases should return this command, not null", result);
        assertSame("setAliases should return this for chaining", command, result);
        
        assertEquals("Configured aliases should be updated", newAliases, command.getConfiguredAliases());
        assertEquals("Active aliases should also be updated", newAliases, command.getAliases());
    }
    
    /**
     * Target mutants: setAliases line 307 (negated conditional), line 310 (replaced return value with null)
     * Tests that setAliases() defers alias updates when registered.
     */
    @Test
    public void testSetAliasesDefersUpdateWhenRegistered() {
        List<String> originalAliases = Arrays.asList("orig1", "orig2");
        List<String> newAliases = Arrays.asList("alias1", "alias2");
        
        TestCommand cmd = new TestCommand("test", "desc", "/test", originalAliases);
        cmd.register(commandMap1);
        
        Command result = cmd.setAliases(newAliases);
        
        assertNotNull("setAliases should return this command, not null", result);
        assertSame("setAliases should return this for chaining", cmd, result);
        
        assertEquals("Configured aliases should be updated", newAliases, cmd.getConfiguredAliases());
        assertEquals("Active aliases should remain unchanged", originalAliases, cmd.getAliases());
    }
    
    /**
     * Target mutants: setAliases line 307 (negated conditional)
     * Tests that unregistering applies the deferred alias changes.
     */
    @Test
    public void testUnregisterAppliesDeferredAliasChanges() {
        List<String> originalAliases = Arrays.asList("orig1", "orig2");
        List<String> newAliases = Arrays.asList("alias1", "alias2");
        
        TestCommand cmd = new TestCommand("test", "desc", "/test", originalAliases);
        cmd.register(commandMap1);
        cmd.setAliases(newAliases);
        cmd.unregister(commandMap1);
        
        assertEquals("Active aliases should be updated after unregister", newAliases, cmd.getAliases());
        assertEquals("Configured aliases should match active aliases", newAliases, cmd.getConfiguredAliases());
    }
    
    // ===== Tests for getName() edge cases (targeting line 109 empty string mutant) =====
    
    /**
     * Target mutant: getName line 109 (replaced return value with "")
     * Tests that getName() returns the actual name, not an empty string.
     */
    @Test
    public void testGetNameReturnsActualName() {
        String expectedName = "mycommand";
        TestCommand cmd = new TestCommand(expectedName);
        
        String actualName = cmd.getName();
        
        assertNotNull("Name should not be null", actualName);
        assertFalse("Name should not be empty", actualName.isEmpty());
        assertEquals("getName should return the actual command name", expectedName, actualName);
    }
    
    /**
     * Target mutant: getName line 109 (replaced return value with "")
     * Tests that getName() returns different names for different commands.
     */
    @Test
    public void testGetNameReturnsDifferentNamesForDifferentCommands() {
        TestCommand cmd1 = new TestCommand("command1");
        TestCommand cmd2 = new TestCommand("command2");
        
        String name1 = cmd1.getName();
        String name2 = cmd2.getName();
        
        assertNotEquals("Different commands should have different names", name1, name2);
        assertEquals("Command1 should have correct name", "command1", name1);
        assertEquals("Command2 should have correct name", "command2", name2);
    }
    
    // ===== Tests for getAliases() edge cases (targeting line 265 empty list mutant) =====
    
    /**
     * Target mutant: getAliases line 265 (replaced return value with Collections.emptyList)
     * Tests that getAliases() returns actual aliases, not an empty list.
     */
    @Test
    public void testGetAliasesReturnsActualAliases() {
        List<String> expectedAliases = Arrays.asList("alias1", "alias2", "alias3");
        TestCommand cmd = new TestCommand("test", "desc", "/test", expectedAliases);
        
        List<String> actualAliases = cmd.getAliases();
        
        assertNotNull("Aliases should not be null", actualAliases);
        assertFalse("Aliases should not be empty when configured", actualAliases.isEmpty());
        assertEquals("getAliases should return the configured aliases", expectedAliases, actualAliases);
    }
    
    /**
     * Target mutant: getAliases line 265 (replaced return value with Collections.emptyList)
     * Tests that getAliases() returns non-empty list when aliases are set.
     */
    @Test
    public void testGetAliasesReturnsNonEmptyListAfterSetAliases() {
        List<String> newAliases = Arrays.asList("newalias1", "newalias2");
        
        command.setAliases(newAliases);
        List<String> actualAliases = command.getAliases();
        
        assertNotNull("Aliases should not be null", actualAliases);
        assertFalse("Aliases should not be empty after setting", actualAliases.isEmpty());
        assertEquals("Aliases should match what was set", newAliases.size(), actualAliases.size());
        assertTrue("Aliases should contain all set values", actualAliases.containsAll(newAliases));
    }
}