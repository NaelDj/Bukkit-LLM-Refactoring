package org.bukkit.configuration.file;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 * This is a base class for all File based implementations of {@link
 * Configuration}
 */
public abstract class FileConfiguration extends MemoryConfiguration {
    /**
     * This value specified that the system default encoding should be
     * completely ignored, as it cannot handle the ASCII character set, or it
     * is a strict-subset of UTF8 already (plain ASCII).
     *
     * @deprecated temporary compatibility measure
     */
    @Deprecated
    public static final boolean UTF8_OVERRIDE;
    /**
     * This value specifies if the system default encoding is unicode, but
     * cannot parse standard ASCII.
     *
     * @deprecated temporary compatibility measure
     */
    @Deprecated
    public static final boolean UTF_BIG;
    /**
     * This value specifies if the system supports unicode.
     *
     * @deprecated temporary compatibility measure
     */
    @Deprecated
    public static final boolean SYSTEM_UTF;
    static {
        final byte[] testBytes = Base64Coder.decode("ICEiIyQlJicoKSorLC0uLzAxMjM0NTY3ODk6Ozw9Pj9AQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpbXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX4NCg==");
        final String testString = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\r\n";
        final Charset defaultCharset = Charset.defaultCharset();
        final String resultString = new String(testBytes, defaultCharset);
        final boolean trueUTF = defaultCharset.name().contains("UTF");
        UTF8_OVERRIDE = !testString.equals(resultString) || defaultCharset.equals(Charset.forName("US-ASCII"));
        SYSTEM_UTF = trueUTF || UTF8_OVERRIDE;
        UTF_BIG = trueUTF && UTF8_OVERRIDE;
    }

    /**
     * Creates an empty {@link FileConfiguration} with no default values.
     */
    public FileConfiguration() {
        super();
    }

    /**
     * Creates an empty {@link FileConfiguration} using the specified {@link
     * Configuration} as a source for all default values.
     *
     * @param defaults Default value provider
     */
    public FileConfiguration(Configuration defaults) {
        super(defaults);
    }

    /**
     * Saves this {@link FileConfiguration} to the specified location.
     * <p>
     * If the file does not exist, it will be created. If already exists, it
     * will be overwritten. If it cannot be overwritten or created, an
     * exception will be thrown.
     * <p>
     * This method will save using the system default encoding, or possibly
     * using UTF8.
     *
     * @param file File to save to.
     * @throws IOException Thrown when the given file cannot be written to for
     *     any reason.
     * @throws IllegalArgumentException Thrown when file is null.
     */
    public void save(File file) throws IOException {
        validateNotNull(file, "File cannot be null");

        createParentDirectories(file);

        String data = saveToString();

        Charset charset = selectCharsetForSave();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), charset);

        try {
            writeToWriter(writer, data);
        } finally {
            closeWriter(writer);
        }
    }

    /**
     * Saves this {@link FileConfiguration} to the specified location.
     * <p>
     * If the file does not exist, it will be created. If already exists, it
     * will be overwritten. If it cannot be overwritten or created, an
     * exception will be thrown.
     * <p>
     * This method will save using the system default encoding, or possibly
     * using UTF8.
     *
     * @param file File to save to.
     * @throws IOException Thrown when the given file cannot be written to for
     *     any reason.
     * @throws IllegalArgumentException Thrown when file is null.
     */
    public void save(String file) throws IOException {
        validateNotNull(file, "File cannot be null");

        saveToFile(new File(file));
    }

    /**
     * Saves this {@link FileConfiguration} to a string, and returns it.
     *
     * @return String containing this configuration.
     */
    public abstract String saveToString();

    /**
     * Loads this {@link FileConfiguration} from the specified location.
     * <p>
     * All the values contained within this configuration will be removed,
     * leaving only settings and defaults, and the new values will be loaded
     * from the given file.
     * <p>
     * If the file cannot be loaded for any reason, an exception will be
     * thrown.
     * <p>
     * This will attempt to use the {@link Charset#defaultCharset()} for
     * files, unless {@link #UTF8_OVERRIDE} but not {@link #UTF_BIG} is
     * specified.
     *
     * @param file File to load from.
     * @throws FileNotFoundException Thrown when the given file cannot be
     *     opened.
     * @throws IOException Thrown when the given file cannot be read.
     * @throws InvalidConfigurationException Thrown when the given file is not
     *     a valid Configuration.
     * @throws IllegalArgumentException Thrown when file is null.
     */
    public void load(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        validateNotNull(file, "File cannot be null");

        final FileInputStream stream = new FileInputStream(file);

        Charset charset = selectCharsetForLoad();
        load(new InputStreamReader(stream, charset));
    }

    /**
     * Loads this {@link FileConfiguration} from the specified stream.
     * <p>
     * All the values contained within this configuration will be removed,
     * leaving only settings and defaults, and the new values will be loaded
     * from the given stream.
     * <p>
     * This will attempt to use the {@link Charset#defaultCharset()}, unless
     * {@link #UTF8_OVERRIDE} or {@link #UTF_BIG} is specified.
     *
     * @param stream Stream to load from
     * @throws IOException Thrown when the given file cannot be read.
     * @throws InvalidConfigurationException Thrown when the given file is not
     *     a valid Configuration.
     * @throws IllegalArgumentException Thrown when stream is null.
     * @deprecated This does not consider encoding
     * @see #load(Reader)
     */
    @Deprecated
    public void load(InputStream stream) throws IOException, InvalidConfigurationException {
        Validate.notNull(stream, "Stream cannot be null");

        load(new InputStreamReader(stream, UTF8_OVERRIDE ? Charsets.UTF_8 : Charset.defaultCharset()));
    }

    /**
     * Loads this {@link FileConfiguration} from the specified reader.
     * <p>
     * All the values contained within this configuration will be removed,
     * leaving only settings and defaults, and the new values will be loaded
     * from the given stream.
     *
     * @param reader the reader to load from
     * @throws IOException thrown when underlying reader throws an IOException
     * @throws InvalidConfigurationException thrown when the reader does not
     *      represent a valid Configuration
     * @throws IllegalArgumentException thrown when reader is null
     */
    public void load(Reader reader) throws IOException, InvalidConfigurationException {
        BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);

        StringBuilder builder = new StringBuilder();

        try {
            String line;

            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } finally {
            closeReader(input);
        }

        loadFromString(builder.toString());
    }

    /**
     * Loads this {@link FileConfiguration} from the specified location.
     * <p>
     * All the values contained within this configuration will be removed,
     * leaving only settings and defaults, and the new values will be loaded
     * from the given file.
     * <p>
     * If the file cannot be loaded for any reason, an exception will be
     * thrown.
     *
     * @param file File to load from.
     * @throws FileNotFoundException Thrown when the given file cannot be
     *     opened.
     * @throws IOException Thrown when the given file cannot be read.
     * @throws InvalidConfigurationException Thrown when the given file is not
     *     a valid Configuration.
     * @throws IllegalArgumentException Thrown when file is null.
     */
    public void load(String file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        validateNotNull(file, "File cannot be null");

        load(new File(file));
    }

    /**
     * Loads this {@link FileConfiguration} from the specified string, as
     * opposed to from file.
     * <p>
     * All the values contained within this configuration will be removed,
     * leaving only settings and defaults, and the new values will be loaded
     * from the given string.
     * <p>
     * If the string is invalid in any way, an exception will be thrown.
     *
     * @param contents Contents of a Configuration to load.
     * @throws InvalidConfigurationException Thrown if the specified string is
     *     invalid.
     * @throws IllegalArgumentException Thrown if contents is null.
     */
    public abstract void loadFromString(String contents) throws InvalidConfigurationException;

    /**
     * Compiles the header for this {@link FileConfiguration} and returns the
     * result.
     * <p>
     * This will use the header from {@link #options()} -> {@link
     * FileConfigurationOptions#header()}, respecting the rules of {@link
     * FileConfigurationOptions#copyHeader()} if set.
     *
     * @return Compiled header
     */
    protected abstract String buildHeader();

    @Override
    public FileConfigurationOptions options() {
        if (options == null) {
            options = new FileConfigurationOptions(this);
        }

        return (FileConfigurationOptions) options;
    }

    /**
     * Hook method for validating non-null parameters.
     * This method can be overridden in tests to track validation calls.
     * 
     * @param obj the object to validate
     * @param message the error message if null
     * @throws IllegalArgumentException if obj is null
     */
    protected void validateNotNull(Object obj, String message) {
        Validate.notNull(obj, message);
    }

    /**
     * Hook method for creating parent directories.
     * This method can be overridden in tests to track directory creation.
     * 
     * @param file the file whose parent directories should be created
     * @throws IOException if directory creation fails
     */
    protected void createParentDirectories(File file) throws IOException {
        Files.createParentDirs(file);
    }

    /**
     * Hook method for delegating save to File version.
     * This method can be overridden in tests to track delegation.
     * 
     * @param file the file to save to
     * @throws IOException if save fails
     */
    protected void saveToFile(File file) throws IOException {
        save(file);
    }

    /**
     * Hook method for writing data to a writer.
     * This method can be overridden in tests to track write operations.
     * 
     * @param writer the writer to write to
     * @param data the data to write
     * @throws IOException if write fails
     */
    protected void writeToWriter(Writer writer, String data) throws IOException {
        writer.write(data);
    }

    /**
     * Hook method for closing a writer.
     * This method can be overridden in tests to track close operations.
     * 
     * @param writer the writer to close
     * @throws IOException if close fails
     */
    protected void closeWriter(Writer writer) throws IOException {
        writer.close();
    }

    /**
     * Hook method for closing a reader.
     * This method can be overridden in tests to track close operations.
     * 
     * @param reader the reader to close
     * @throws IOException if close fails
     */
    protected void closeReader(Reader reader) throws IOException {
        reader.close();
    }

    /**
     * Hook method for selecting charset for save operations.
     * This method can be overridden in tests to track encoding selection.
     * 
     * @return the charset to use for saving
     */
    protected Charset selectCharsetForSave() {
        return UTF8_OVERRIDE && !UTF_BIG ? Charsets.UTF_8 : Charset.defaultCharset();
    }

    /**
     * Hook method for selecting charset for load operations.
     * This method can be overridden in tests to track encoding selection.
     * 
     * @return the charset to use for loading
     */
    protected Charset selectCharsetForLoad() {
        return UTF8_OVERRIDE && !UTF_BIG ? Charsets.UTF_8 : Charset.defaultCharset();
    }
}
