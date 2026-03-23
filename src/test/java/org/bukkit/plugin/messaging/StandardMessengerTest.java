package org.bukkit.plugin.messaging;

import org.bukkit.entity.Player;
import org.bukkit.plugin.TestPlugin;
import java.util.Collection;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class StandardMessengerTest {
    public StandardMessenger getMessenger() {
        return new StandardMessenger();
    }

    private int count = 0;
    public TestPlugin getPlugin() {
        return new TestPlugin("" + count++);
    }

    @Test
    public void testIsReservedChannel() {
        Messenger messenger = getMessenger();

        assertTrue(messenger.isReservedChannel("REGISTER"));
        assertFalse(messenger.isReservedChannel("register"));
        assertTrue(messenger.isReservedChannel("UNREGISTER"));
        assertFalse(messenger.isReservedChannel("unregister"));
        assertFalse(messenger.isReservedChannel("notReserved"));
    }

    @Test
    public void testRegisterAndUnregisterOutgoingPluginChannel() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();

        assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        messenger.registerOutgoingPluginChannel(plugin, "foo");
        assertTrue(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        assertFalse(messenger.isOutgoingChannelRegistered(plugin, "bar"));

        messenger.unregisterOutgoingPluginChannel(plugin, "foo");
        assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
    }

    @Test(expected = ReservedChannelException.class)
    public void testReservedOutgoingRegistration() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();

        messenger.registerOutgoingPluginChannel(plugin, "REGISTER");
    }

    @Test
    public void testUnregisterOutgoingPluginChannel_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();

        assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        messenger.registerOutgoingPluginChannel(plugin, "foo");
        messenger.registerOutgoingPluginChannel(plugin, "bar");
        assertTrue(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        assertTrue(messenger.isOutgoingChannelRegistered(plugin, "bar"));

        messenger.unregisterOutgoingPluginChannel(plugin);
        assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        assertFalse(messenger.isOutgoingChannelRegistered(plugin, "bar"));
    }

    @Test
    public void testRegisterIncomingPluginChannel() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = new TestMessageListener("foo", "bar".getBytes());
        Player player = TestPlayer.getInstance();
        PluginMessageListenerRegistration registration = messenger.registerIncomingPluginChannel(plugin, "foo", listener);

        assertTrue(registration.isValid());
        assertTrue(messenger.isIncomingChannelRegistered(plugin, "foo"));
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        assertTrue(listener.hasReceived());

        messenger.unregisterIncomingPluginChannel(plugin, "foo", listener);
        listener.reset();

        assertFalse(registration.isValid());
        assertFalse(messenger.isIncomingChannelRegistered(plugin, "foo"));
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        assertFalse(listener.hasReceived());
    }

    @Test(expected = ReservedChannelException.class)
    public void testReservedIncomingRegistration() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();

        messenger.registerIncomingPluginChannel(plugin, "REGISTER", new TestMessageListener("foo", "bar".getBytes()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateIncomingRegistration() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = new TestMessageListener("foo", "bar".getBytes());

        messenger.registerIncomingPluginChannel(plugin, "baz", listener);
        messenger.registerIncomingPluginChannel(plugin, "baz", listener);
    }

    @Test
    public void testUnregisterIncomingPluginChannel_Plugin_String() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener1 = new TestMessageListener("foo", "bar".getBytes());
        TestMessageListener listener2 = new TestMessageListener("baz", "qux".getBytes());
        Player player = TestPlayer.getInstance();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin, "foo", listener1);
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin, "baz", listener2);

        assertTrue(registration1.isValid());
        assertTrue(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        assertTrue(listener1.hasReceived());
        assertTrue(listener2.hasReceived());

        messenger.unregisterIncomingPluginChannel(plugin, "foo");
        listener1.reset();
        listener2.reset();

        assertFalse(registration1.isValid());
        assertTrue(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        assertFalse(listener1.hasReceived());
        assertTrue(listener2.hasReceived());
    }

    @Test
    public void testUnregisterIncomingPluginChannel_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener1 = new TestMessageListener("foo", "bar".getBytes());
        TestMessageListener listener2 = new TestMessageListener("baz", "qux".getBytes());
        Player player = TestPlayer.getInstance();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin, "foo", listener1);
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin, "baz", listener2);

        assertTrue(registration1.isValid());
        assertTrue(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        assertTrue(listener1.hasReceived());
        assertTrue(listener2.hasReceived());

        messenger.unregisterIncomingPluginChannel(plugin);
        listener1.reset();
        listener2.reset();

        assertFalse(registration1.isValid());
        assertFalse(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        assertFalse(listener1.hasReceived());
        assertFalse(listener2.hasReceived());
    }

    @Test
    public void testGetOutgoingChannels() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();

        assertEquals(messenger.getOutgoingChannels());

        messenger.registerOutgoingPluginChannel(plugin1, "foo");
        messenger.registerOutgoingPluginChannel(plugin1, "bar");
        messenger.registerOutgoingPluginChannel(plugin2, "baz");
        messenger.registerOutgoingPluginChannel(plugin2, "baz");

        assertEquals(messenger.getOutgoingChannels(), "foo", "bar", "baz");
    }

    @Test
    public void testGetOutgoingChannels_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();

        messenger.registerOutgoingPluginChannel(plugin1, "foo");
        messenger.registerOutgoingPluginChannel(plugin1, "bar");
        messenger.registerOutgoingPluginChannel(plugin2, "baz");
        messenger.registerOutgoingPluginChannel(plugin2, "qux");

        assertEquals(messenger.getOutgoingChannels(plugin1), "foo", "bar");
        assertEquals(messenger.getOutgoingChannels(plugin2), "baz", "qux");
        assertEquals(messenger.getOutgoingChannels(plugin3));
    }

    @Test
    public void testGetIncomingChannels() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();

        assertEquals(messenger.getIncomingChannels());

        messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));

        assertEquals(messenger.getIncomingChannels(), "foo", "bar", "baz");
    }

    @Test
    public void testGetIncomingChannels_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();

        messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "qux", new TestMessageListener("foo", "bar".getBytes()));

        assertEquals(messenger.getIncomingChannels(plugin1), "foo", "bar");
        assertEquals(messenger.getIncomingChannels(plugin2), "baz", "qux");
        assertEquals(messenger.getIncomingChannels(plugin3));
    }

    @Test
    public void testGetIncomingChannelRegistrations_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration3 = messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration4 = messenger.registerIncomingPluginChannel(plugin2, "qux", new TestMessageListener("foo", "bar".getBytes()));

        assertEquals(messenger.getIncomingChannelRegistrations(plugin1), registration1, registration2);
        assertEquals(messenger.getIncomingChannelRegistrations(plugin2), registration3, registration4);
        assertEquals(messenger.getIncomingChannels(plugin3));
    }

    @Test
    public void testGetIncomingChannelRegistrations_String() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration3 = messenger.registerIncomingPluginChannel(plugin2, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration4 = messenger.registerIncomingPluginChannel(plugin2, "bar", new TestMessageListener("foo", "bar".getBytes()));

        assertEquals(messenger.getIncomingChannelRegistrations("foo"), registration1, registration3);
        assertEquals(messenger.getIncomingChannelRegistrations("bar"), registration2, registration4);
        assertEquals(messenger.getIncomingChannelRegistrations("baz"));
    }

    @Test
    public void testGetIncomingChannelRegistrations_Plugin_String() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration3 = messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration4 = messenger.registerIncomingPluginChannel(plugin2, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration5 = messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration6 = messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));

        assertEquals(messenger.getIncomingChannelRegistrations(plugin1, "foo"), registration1, registration2);
        assertEquals(messenger.getIncomingChannelRegistrations(plugin1, "bar"), registration3);
        assertEquals(messenger.getIncomingChannelRegistrations(plugin2, "bar"), registration4);
        assertEquals(messenger.getIncomingChannelRegistrations(plugin2, "baz"), registration5, registration6);
        assertEquals(messenger.getIncomingChannelRegistrations(plugin1, "baz"));
        assertEquals(messenger.getIncomingChannelRegistrations(plugin3, "qux"));
    }

    private static <T> void assertEquals(Collection<T> actual, T... expected) {
        assertThat("Size of the array", actual.size(), is(expected.length));
        assertThat(actual, hasItems(expected));
    }

    // Tests targeting surviving mutants in removeFromOutgoing method (lines 48, 51, 59)
    // These test internal map cleanup behavior that was previously unobservable

    @Test
    public void testRemoveFromOutgoing_LastPluginCleansUpChannelMap() {
        // Targets mutant at line 51: negated conditional if (plugins.isEmpty())
        // Verifies that when last plugin is removed from a channel, the channel map entry is removed
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();

        messenger.registerOutgoingPluginChannel(plugin1, "test-channel");
        messenger.registerOutgoingPluginChannel(plugin2, "test-channel");

        // Both plugins registered, channel map entry should exist
        assertTrue("Channel map should contain entry", messenger.hasOutgoingChannelMapEntry("test-channel"));

        // Remove first plugin, channel map entry should still exist (other plugin still registered)
        messenger.unregisterOutgoingPluginChannel(plugin1, "test-channel");
        assertTrue("Channel map should still contain entry after removing first plugin", 
                   messenger.hasOutgoingChannelMapEntry("test-channel"));
        assertTrue("Second plugin should still be registered", 
                   messenger.isOutgoingChannelRegistered(plugin2, "test-channel"));

        // Remove last plugin, channel map entry should be removed
        messenger.unregisterOutgoingPluginChannel(plugin2, "test-channel");
        assertFalse("Channel map entry should be removed when last plugin unregisters", 
                    messenger.hasOutgoingChannelMapEntry("test-channel"));
    }

    @Test
    public void testRemoveFromOutgoing_LastChannelCleansUpPluginMap() {
        // Targets mutant at line 59: negated conditional if (channels.isEmpty())
        // Verifies that when last channel is removed from a plugin, the plugin map entry is removed
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();

        messenger.registerOutgoingPluginChannel(plugin, "channel1");
        messenger.registerOutgoingPluginChannel(plugin, "channel2");

        // Plugin has 2 channels, plugin map entry should exist
        assertTrue("Plugin map should contain entry", messenger.hasOutgoingPluginMapEntry(plugin));
        org.junit.Assert.assertEquals("Plugin should have 2 channels", 2, messenger.getOutgoingChannelCountForPlugin(plugin));

        // Remove first channel, plugin map entry should still exist
        messenger.unregisterOutgoingPluginChannel(plugin, "channel1");
        assertTrue("Plugin map should still contain entry after removing first channel", 
                   messenger.hasOutgoingPluginMapEntry(plugin));
        org.junit.Assert.assertEquals("Plugin should have 1 channel", 1, messenger.getOutgoingChannelCountForPlugin(plugin));

        // Remove last channel, plugin map entry should be removed
        messenger.unregisterOutgoingPluginChannel(plugin, "channel2");
        assertFalse("Plugin map entry should be removed when last channel unregisters", 
                    messenger.hasOutgoingPluginMapEntry(plugin));
        org.junit.Assert.assertEquals("Plugin should have 0 channels", 0, messenger.getOutgoingChannelCountForPlugin(plugin));
    }

    @Test
    public void testRemoveFromOutgoing_NullCheckDoesNotCauseCleanup() {
        // Targets mutant at line 48: negated conditional if (plugins != null)
        // Verifies behavior when removing from non-existent channel (null check path)
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();

        // Try to unregister from a channel that was never registered
        // Should not throw exception and should not affect internal state
        messenger.unregisterOutgoingPluginChannel(plugin, "never-registered");

        assertFalse("Non-registered channel should not have map entry", 
                    messenger.hasOutgoingChannelMapEntry("never-registered"));
        assertFalse("Plugin with no channels should not have map entry", 
                    messenger.hasOutgoingPluginMapEntry(plugin));
    }

    @Test
    public void testRemoveFromOutgoing_VerifyChannelCountDecreases() {
        // Additional test to verify channel count accurately reflects removals
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();

        org.junit.Assert.assertEquals("Initial count should be 0", 0, messenger.getOutgoingChannelCountForPlugin(plugin));

        messenger.registerOutgoingPluginChannel(plugin, "chan1");
        org.junit.Assert.assertEquals("Count should be 1 after first registration", 1, messenger.getOutgoingChannelCountForPlugin(plugin));

        messenger.registerOutgoingPluginChannel(plugin, "chan2");
        org.junit.Assert.assertEquals("Count should be 2 after second registration", 2, messenger.getOutgoingChannelCountForPlugin(plugin));

        messenger.registerOutgoingPluginChannel(plugin, "chan3");
        org.junit.Assert.assertEquals("Count should be 3 after third registration", 3, messenger.getOutgoingChannelCountForPlugin(plugin));

        messenger.unregisterOutgoingPluginChannel(plugin, "chan2");
        org.junit.Assert.assertEquals("Count should be 2 after removing one channel", 2, messenger.getOutgoingChannelCountForPlugin(plugin));

        messenger.unregisterOutgoingPluginChannel(plugin, "chan1");
        org.junit.Assert.assertEquals("Count should be 1 after removing second channel", 1, messenger.getOutgoingChannelCountForPlugin(plugin));

        messenger.unregisterOutgoingPluginChannel(plugin, "chan3");
        org.junit.Assert.assertEquals("Count should be 0 after removing all channels", 0, messenger.getOutgoingChannelCountForPlugin(plugin));
    }

    // Tests targeting surviving mutants in getIncomingChannelRegistrations (lines 328, 353)
    // These mutants replace returns with Collections.emptySet - tests verify count > 0 to distinguish

    @Test
    public void testGetIncomingChannelRegistrations_String_DistinguishFromEmptyMutant() {
        // Targets mutant at line 328: replaced return value with Collections.emptySet
        // Verifies count is correct before checking the set contents
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        
        // Register listeners for the same channel from different plugins
        messenger.registerIncomingPluginChannel(plugin1, "test-chan", new TestMessageListener("test-chan", "data".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "test-chan", new TestMessageListener("test-chan", "data".getBytes()));
        
        // Use count method to verify registrations exist (distinguishes from mutated empty set)
        org.junit.Assert.assertEquals("Should have 2 registrations for channel", 2, messenger.getIncomingRegistrationCount("test-chan"));
        
        // Now verify the set is not empty
        Set<PluginMessageListenerRegistration> registrations = messenger.getIncomingChannelRegistrations("test-chan");
        assertFalse("Registrations set should not be empty", registrations.isEmpty());
        org.junit.Assert.assertEquals("Set size should match count", 2, registrations.size());
    }

    @Test
    public void testGetIncomingChannelRegistrations_String_EmptyChannel() {
        // Verify behavior when channel truly has no registrations
        StandardMessenger messenger = getMessenger();
        
        org.junit.Assert.assertEquals("Unregistered channel should have 0 registrations", 0, messenger.getIncomingRegistrationCount("never-used"));
        
        Set<PluginMessageListenerRegistration> registrations = messenger.getIncomingChannelRegistrations("never-used");
        assertTrue("Registrations set should be empty for unregistered channel", registrations.isEmpty());
    }

    @Test
    public void testGetIncomingChannelRegistrations_PluginString_DistinguishFromEmptyMutant() {
        // Targets mutant at line 353: replaced return value with Collections.emptySet
        // Verifies count is correct before checking the set contents
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        
        // Register multiple listeners for same plugin+channel
        messenger.registerIncomingPluginChannel(plugin, "multi-chan", new TestMessageListener("multi-chan", "data1".getBytes()));
        messenger.registerIncomingPluginChannel(plugin, "multi-chan", new TestMessageListener("multi-chan", "data2".getBytes()));
        
        // Use count method to verify registrations exist (distinguishes from mutated empty set)
        org.junit.Assert.assertEquals("Should have 2 registrations for plugin+channel", 2, messenger.getIncomingRegistrationCount(plugin, "multi-chan"));
        
        // Now verify the set is not empty
        Set<PluginMessageListenerRegistration> registrations = messenger.getIncomingChannelRegistrations(plugin, "multi-chan");
        assertFalse("Registrations set should not be empty", registrations.isEmpty());
        org.junit.Assert.assertEquals("Set size should match count", 2, registrations.size());
    }

    @Test
    public void testGetIncomingChannelRegistrations_PluginString_VerifyCountVsEmptyBehavior() {
        // Tests both scenarios: registrations exist vs truly empty
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        
        // Initially, no registrations
        org.junit.Assert.assertEquals("Initial count should be 0", 0, messenger.getIncomingRegistrationCount(plugin, "chan-test"));
        assertTrue("Initial set should be empty", messenger.getIncomingChannelRegistrations(plugin, "chan-test").isEmpty());
        
        // Add one registration
        messenger.registerIncomingPluginChannel(plugin, "chan-test", new TestMessageListener("chan-test", "x".getBytes()));
        org.junit.Assert.assertEquals("Count should be 1 after registration", 1, messenger.getIncomingRegistrationCount(plugin, "chan-test"));
        assertFalse("Set should not be empty after registration", messenger.getIncomingChannelRegistrations(plugin, "chan-test").isEmpty());
        
        // Add another registration for same plugin+channel
        messenger.registerIncomingPluginChannel(plugin, "chan-test", new TestMessageListener("chan-test", "y".getBytes()));
        org.junit.Assert.assertEquals("Count should be 2 after second registration", 2, messenger.getIncomingRegistrationCount(plugin, "chan-test"));
        org.junit.Assert.assertEquals("Set size should be 2", 2, messenger.getIncomingChannelRegistrations(plugin, "chan-test").size());
        
        // Remove one
        messenger.unregisterIncomingPluginChannel(plugin, "chan-test");
        org.junit.Assert.assertEquals("Count should be 0 after unregistration", 0, messenger.getIncomingRegistrationCount(plugin, "chan-test"));
        assertTrue("Set should be empty after removing all", messenger.getIncomingChannelRegistrations(plugin, "chan-test").isEmpty());
    }

    // Tests targeting surviving mutants for validateChannel removal (lines 320, 337, 378, 399)
    // These verify that validation is actually executed by passing invalid inputs

    @Test(expected = ChannelNameTooLongException.class)
    public void testGetIncomingChannelRegistrations_String_ValidatesTooLong() {
        // Targets mutant at line 320: removed call to validateChannel
        // Verifies that validation happens by passing a too-long channel name
        StandardMessenger messenger = getMessenger();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= Messenger.MAX_CHANNEL_SIZE; i++) {
            sb.append('a');
        }
        String tooLongChannel = sb.toString();
        
        messenger.getIncomingChannelRegistrations(tooLongChannel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncomingChannelRegistrations_String_ValidatesNull() {
        // Targets mutant at line 320: removed call to validateChannel
        // Verifies that validation happens by passing null
        StandardMessenger messenger = getMessenger();
        
        messenger.getIncomingChannelRegistrations((String) null);
    }

    @Test(expected = ChannelNameTooLongException.class)
    public void testGetIncomingChannelRegistrations_PluginString_ValidatesTooLong() {
        // Targets mutant at line 337: removed call to validateChannel
        // Verifies that validation happens by passing a too-long channel name
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= Messenger.MAX_CHANNEL_SIZE; i++) {
            sb.append('b');
        }
        String tooLongChannel = sb.toString();
        
        messenger.getIncomingChannelRegistrations(plugin, tooLongChannel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncomingChannelRegistrations_PluginString_ValidatesNull() {
        // Targets mutant at line 337: removed call to validateChannel
        // Verifies that validation happens by passing null
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        
        messenger.getIncomingChannelRegistrations(plugin, null);
    }

    @Test(expected = ChannelNameTooLongException.class)
    public void testIsIncomingChannelRegistered_ValidatesTooLong() {
        // Targets mutant at line 378: removed call to validateChannel
        // Verifies that validation happens by passing a too-long channel name
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= Messenger.MAX_CHANNEL_SIZE; i++) {
            sb.append('c');
        }
        String tooLongChannel = sb.toString();
        
        messenger.isIncomingChannelRegistered(plugin, tooLongChannel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsIncomingChannelRegistered_ValidatesNull() {
        // Targets mutant at line 378: removed call to validateChannel
        // Verifies that validation happens by passing null
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        
        messenger.isIncomingChannelRegistered(plugin, null);
    }

    @Test(expected = ChannelNameTooLongException.class)
    public void testIsOutgoingChannelRegistered_ValidatesTooLong() {
        // Targets mutant at line 399: removed call to validateChannel
        // Verifies that validation happens by passing a too-long channel name
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= Messenger.MAX_CHANNEL_SIZE; i++) {
            sb.append('d');
        }
        String tooLongChannel = sb.toString();
        
        messenger.isOutgoingChannelRegistered(plugin, tooLongChannel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsOutgoingChannelRegistered_ValidatesNull() {
        // Targets mutant at line 399: removed call to validateChannel
        // Verifies that validation happens by passing null
        StandardMessenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        
        messenger.isOutgoingChannelRegistered(plugin, null);
    }
}
