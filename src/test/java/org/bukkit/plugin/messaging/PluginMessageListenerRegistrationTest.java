package org.bukkit.plugin.messaging;

import org.bukkit.plugin.TestPlugin;
import org.junit.Test;
import static org.junit.Assert.*;

public class PluginMessageListenerRegistrationTest {

    private StandardMessenger getMessenger() {
        return new StandardMessenger();
    }

    private int count = 0;
    private TestPlugin getPlugin() {
        return new TestPlugin("" + count++);
    }

    private TestMessageListener getListener() {
        return new TestMessageListener("test", "data".getBytes());
    }

    // ========== Tests for matchesMessenger() ==========
    // These tests target surviving mutants in the messenger comparison logic (line 95)

    @Test
    public void testMatchesMessenger_SameMessenger() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel1", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel2", listener);
        
        // Should match when same messenger instance
        assertTrue("Same messenger instance should match", reg1.matchesMessenger(reg2));
    }

    @Test
    public void testMatchesMessenger_DifferentMessenger() {
        Messenger messenger1 = getMessenger();
        Messenger messenger2 = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger1, plugin, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger2, plugin, "channel", listener);
        
        // Should not match when different messenger instances
        assertFalse("Different messenger instances should not match", reg1.matchesMessenger(reg2));
    }

    // ========== Tests for matchesPlugin() ==========
    // These tests target surviving mutants in the plugin comparison logic (line 103)

    @Test
    public void testMatchesPlugin_SamePlugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel1", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel2", listener);
        
        // Should match when same plugin instance
        assertTrue("Same plugin instance should match", reg1.matchesPlugin(reg2));
    }

    @Test
    public void testMatchesPlugin_DifferentPlugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin1, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin2, "channel", listener);
        
        // Should not match when different plugin instances
        assertFalse("Different plugin instances should not match", reg1.matchesPlugin(reg2));
    }

    // ========== Tests for matchesChannel() ==========
    // These tests target surviving mutants in the channel comparison logic (line 111)

    @Test
    public void testMatchesChannel_SameChannel() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Should match when same channel name
        assertTrue("Same channel should match", reg1.matchesChannel(reg2));
    }

    @Test
    public void testMatchesChannel_DifferentChannel() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel1", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel2", listener);
        
        // Should not match when different channel names
        assertFalse("Different channels should not match", reg1.matchesChannel(reg2));
    }

    // ========== Tests for matchesListener() ==========
    // These tests target surviving mutants in the listener comparison logic (line 119)

    @Test
    public void testMatchesListener_SameListener() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel1", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel2", listener);
        
        // Should match when same listener instance
        assertTrue("Same listener instance should match", reg1.matchesListener(reg2));
    }

    @Test
    public void testMatchesListener_DifferentListener() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener1 = getListener();
        TestMessageListener listener2 = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener1);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener2);
        
        // Should not match when different listener instances
        assertFalse("Different listener instances should not match", reg1.matchesListener(reg2));
    }

    // ========== Tests for hashCode contributions ==========
    // These tests target surviving mutants in the hashCode calculation (lines 148-172)

    @Test
    public void testGetMessengerHashContribution() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Hash contribution should equal the messenger's hash code
        assertEquals("Messenger hash contribution should match messenger.hashCode()", 
                     messenger.hashCode(), 
                     reg.getMessengerHashContribution());
    }

    @Test
    public void testGetPluginHashContribution() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Hash contribution should equal the plugin's hash code
        assertEquals("Plugin hash contribution should match plugin.hashCode()", 
                     plugin.hashCode(), 
                     reg.getPluginHashContribution());
    }

    @Test
    public void testGetChannelHashContribution() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Hash contribution should equal the channel's hash code
        assertEquals("Channel hash contribution should match channel.hashCode()", 
                     "channel".hashCode(), 
                     reg.getChannelHashContribution());
    }

    @Test
    public void testGetListenerHashContribution() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Hash contribution should equal the listener's hash code
        assertEquals("Listener hash contribution should match listener.hashCode()", 
                     listener.hashCode(), 
                     reg.getListenerHashContribution());
    }

    // ========== Tests for equals() edge cases ==========
    // These tests target surviving mutants in equals() (lines 70-90)

    @Test
    public void testEquals_Null() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Should return false for null
        assertFalse("Registration should not equal null", reg.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Should return false for different class
        assertFalse("Registration should not equal different class", reg.equals("NotARegistration"));
    }

    @Test
    public void testEquals_SameObject() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Should return true for same object
        assertTrue("Registration should equal itself", reg.equals(reg));
    }

    @Test
    public void testEquals_AllFieldsEqual() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Should return true when all fields are equal
        assertTrue("Registrations with same fields should be equal", reg1.equals(reg2));
    }

    @Test
    public void testEquals_DifferentMessenger() {
        Messenger messenger1 = getMessenger();
        Messenger messenger2 = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger1, plugin, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger2, plugin, "channel", listener);
        
        // Should return false when messenger differs
        assertFalse("Registrations with different messengers should not be equal", reg1.equals(reg2));
    }

    @Test
    public void testEquals_DifferentPlugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin1, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin2, "channel", listener);
        
        // Should return false when plugin differs
        assertFalse("Registrations with different plugins should not be equal", reg1.equals(reg2));
    }

    @Test
    public void testEquals_DifferentChannel() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel1", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel2", listener);
        
        // Should return false when channel differs
        assertFalse("Registrations with different channels should not be equal", reg1.equals(reg2));
    }

    @Test
    public void testEquals_DifferentListener() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener1 = getListener();
        TestMessageListener listener2 = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener1);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener2);
        
        // Should return false when listener differs
        assertFalse("Registrations with different listeners should not be equal", reg1.equals(reg2));
    }

    // ========== Tests for hashCode() properties ==========
    // These tests target surviving mutants in hashCode() by verifying the contract

    @Test
    public void testHashCode_Consistency() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        int hash1 = reg.hashCode();
        int hash2 = reg.hashCode();
        
        // Multiple calls should return same value
        assertEquals("Hash code should be consistent", hash1, hash2);
    }

    @Test
    public void testHashCode_EqualsImpliesEqualHashCode() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Equal objects must have equal hash codes
        assertTrue("Registrations should be equal", reg1.equals(reg2));
        assertEquals("Equal registrations must have equal hash codes", reg1.hashCode(), reg2.hashCode());
    }

    @Test
    public void testHashCode_DifferentMessengerGivesDifferentHash() {
        Messenger messenger1 = getMessenger();
        Messenger messenger2 = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger1, plugin, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger2, plugin, "channel", listener);
        
        // Different objects should (likely) have different hash codes
        // This tests that messenger affects the hash
        assertNotEquals("Different messengers should produce different hash codes", 
                       reg1.hashCode(), 
                       reg2.hashCode());
    }

    @Test
    public void testHashCode_DifferentPluginGivesDifferentHash() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin1, "channel", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin2, "channel", listener);
        
        // Different plugins should produce different hash codes
        assertNotEquals("Different plugins should produce different hash codes", 
                       reg1.hashCode(), 
                       reg2.hashCode());
    }

    @Test
    public void testHashCode_DifferentChannelGivesDifferentHash() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel1", listener);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel2", listener);
        
        // Different channels should produce different hash codes
        assertNotEquals("Different channels should produce different hash codes", 
                       reg1.hashCode(), 
                       reg2.hashCode());
    }

    @Test
    public void testHashCode_DifferentListenerGivesDifferentHash() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener1 = getListener();
        TestMessageListener listener2 = getListener();
        
        PluginMessageListenerRegistration reg1 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener1);
        PluginMessageListenerRegistration reg2 = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener2);
        
        // Different listeners should produce different hash codes
        assertNotEquals("Different listeners should produce different hash codes", 
                       reg1.hashCode(), 
                       reg2.hashCode());
    }

    @Test
    public void testHashCode_NotZero() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = getListener();
        
        PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(messenger, plugin, "channel", listener);
        
        // Hash code should not be zero (targets the return 0 mutation)
        assertNotEquals("Hash code should not be zero", 0, reg.hashCode());
    }
}