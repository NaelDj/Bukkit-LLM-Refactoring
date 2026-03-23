package org.bukkit.plugin.messaging;

import org.bukkit.plugin.Plugin;

/**
 * Contains information about a {@link Plugin}s registration to a plugin
 * channel.
 */
public final class PluginMessageListenerRegistration {
    private final Messenger messenger;
    private final Plugin plugin;
    private final String channel;
    private final PluginMessageListener listener;

    public PluginMessageListenerRegistration(Messenger messenger, Plugin plugin, String channel, PluginMessageListener listener) {
        if (messenger == null) {
            throw new IllegalArgumentException("Messenger cannot be null!");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null!");
        }
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null!");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null!");
        }

        this.messenger = messenger;
        this.plugin = plugin;
        this.channel = channel;
        this.listener = listener;
    }

    /**
     * Gets the plugin channel that this registration is about.
     *
     * @return Plugin channel.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the registered listener described by this registration.
     *
     * @return Registered listener.
     */
    public PluginMessageListener getListener() {
        return listener;
    }

    /**
     * Gets the plugin that this registration is for.
     *
     * @return Registered plugin.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Checks if this registration is still valid.
     *
     * @return True if this registration is still valid, otherwise false.
     */
    public boolean isValid() {
        return messenger.isRegistrationValid(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PluginMessageListenerRegistration other = (PluginMessageListenerRegistration) obj;
        if (!matchesMessenger(other)) {
            return false;
        }
        if (!matchesPlugin(other)) {
            return false;
        }
        if (!matchesChannel(other)) {
            return false;
        }
        if (!matchesListener(other)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the messenger field matches with another registration.
     * Package-private for testing.
     *
     * @param other The other registration to compare with
     * @return True if messenger fields match, false otherwise
     */
    boolean matchesMessenger(PluginMessageListenerRegistration other) {
        if (this.messenger != other.messenger && (this.messenger == null || !this.messenger.equals(other.messenger))) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the plugin field matches with another registration.
     * Package-private for testing.
     *
     * @param other The other registration to compare with
     * @return True if plugin fields match, false otherwise
     */
    boolean matchesPlugin(PluginMessageListenerRegistration other) {
        if (this.plugin != other.plugin && (this.plugin == null || !this.plugin.equals(other.plugin))) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the channel field matches with another registration.
     * Package-private for testing.
     *
     * @param other The other registration to compare with
     * @return True if channel fields match, false otherwise
     */
    boolean matchesChannel(PluginMessageListenerRegistration other) {
        if ((this.channel == null) ? (other.channel != null) : !this.channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the listener field matches with another registration.
     * Package-private for testing.
     *
     * @param other The other registration to compare with
     * @return True if listener fields match, false otherwise
     */
    boolean matchesListener(PluginMessageListenerRegistration other) {
        if (this.listener != other.listener && (this.listener == null || !this.listener.equals(other.listener))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + getMessengerHashContribution();
        hash = 53 * hash + getPluginHashContribution();
        hash = 53 * hash + getChannelHashContribution();
        hash = 53 * hash + getListenerHashContribution();
        return hash;
    }

    /**
     * Gets the hash code contribution for the messenger field.
     * Package-private for testing.
     *
     * @return The messenger's hash code, or 0 if null
     */
    int getMessengerHashContribution() {
        return this.messenger != null ? this.messenger.hashCode() : 0;
    }

    /**
     * Gets the hash code contribution for the plugin field.
     * Package-private for testing.
     *
     * @return The plugin's hash code, or 0 if null
     */
    int getPluginHashContribution() {
        return this.plugin != null ? this.plugin.hashCode() : 0;
    }

    /**
     * Gets the hash code contribution for the channel field.
     * Package-private for testing.
     *
     * @return The channel's hash code, or 0 if null
     */
    int getChannelHashContribution() {
        return this.channel != null ? this.channel.hashCode() : 0;
    }

    /**
     * Gets the hash code contribution for the listener field.
     * Package-private for testing.
     *
     * @return The listener's hash code, or 0 if null
     */
    int getListenerHashContribution() {
        return this.listener != null ? this.listener.hashCode() : 0;
    }
}
