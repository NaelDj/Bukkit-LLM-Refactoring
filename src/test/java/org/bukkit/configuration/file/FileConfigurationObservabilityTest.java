package org.bukkit.configuration.file;

import com.google.common.base.Charsets;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests targeting surviving mutants in FileConfiguration by verifying
 * internal operations become observable through protected hook methods.
 */
public class FileConfigurationObservabilityTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private TestableFileConfiguration config;

    @Before
    public void setUp() {
        config = new TestableFileConfiguration();
    }

    /**
     * Targets surviving mutant at line 99: removed call to Validate.notNull in save(File)
     * 
     * Refactoring: Extracted validation into validateNotNull() hook
     * Observability: Tests can now verify null validation throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveFile_ThrowsOnNullFile() throws IOException {
        config.save((File) null);
    }

    /**
     * Targets surviving mutant at line 130: removed call to Validate.notNull in save(String)
     * 
     * Refactoring: Extracted validation into validateNotNull() hook
     * Observability: Tests can now verify null validation throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveString_ThrowsOnNullPath() throws IOException {
        config.save((String) null);
    }

    /**
     * Targets surviving mutant at line 165: removed call to Validate.notNull in load(File)
     * 
     * Refactoring: Extracted validation into validateNotNull() hook
     * Observability: Tests can now verify null validation throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadFile_ThrowsOnNullFile() throws IOException, InvalidConfigurationException {
        config.load((File) null);
    }

    /**
     * Targets surviving mutant at line 248: removed call to Validate.notNull in load(String)
     * 
     * Refactoring: Extracted validation into validateNotNull() hook
     * Observability: Tests can now verify null validation throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadString_ThrowsOnNullPath() throws IOException, InvalidConfigurationException {
        config.load((String) null);
    }

    /**
     * Targets surviving mutant at line 101: removed call to Files.createParentDirs
     * 
     * Refactoring: Extracted directory creation into createParentDirectories() hook
     * Observability: Tests can now verify parent directories are created
     */
    @Test
    public void testSave_CreatesParentDirectories() throws IOException {
        File parentDir = testFolder.newFolder("parent");
        File subDir = new File(parentDir, "sub");
        File file = new File(subDir, "config.yml");

        assertFalse("Subdirectory should not exist before save", subDir.exists());

        config.set("key", "value");
        config.save(file);

        assertTrue("Parent directories should have been created", file.getParentFile().exists());
        assertTrue("File should exist after save", file.exists());
    }

    /**
     * Targets surviving mutant at line 132: removed call to save(File) from save(String)
     * 
     * Refactoring: Extracted delegation into saveToFile() hook
     * Observability: Tests can now verify delegation occurs and operations are tracked
     */
    @Test
    public void testSaveString_DelegatesToSaveFile() throws IOException {
        File file = testFolder.newFile("config.yml");
        
        config.set("key", "value");
        config.save(file.getAbsolutePath());

        assertTrue("Validation should have been called", config.operations.contains("validate:file"));
        assertTrue("Delegation to save(File) should occur", config.operations.contains("saveToFile"));
        assertTrue("File should exist after delegated save", file.exists());
    }

    /**
     * Targets surviving mutant at line 108: removed call to writer.write
     * 
     * Refactoring: Extracted write operation into writeToWriter() hook
     * Observability: Tests can now verify data is actually written
     */
    @Test
    public void testSave_WritesContent() throws IOException, InvalidConfigurationException {
        File file = testFolder.newFile("config.yml");
        
        config.set("key", "testValue");
        config.save(file);

        assertTrue("Write operation should be tracked", config.operations.contains("write:data"));
        
        // Verify actual content was written by reading it back
        YamlConfiguration readConfig = new YamlConfiguration();
        readConfig.load(file);
        assertEquals("Content should be written to file", "testValue", readConfig.getString("key"));
    }

    /**
     * Targets surviving mutant at line 110: removed call to writer.close in save
     * 
     * Refactoring: Extracted close operation into closeWriter() hook
     * Observability: Tests can now verify writer is properly closed
     */
    @Test
    public void testSave_ClosesWriter() throws IOException {
        File file = testFolder.newFile("config.yml");
        
        config.set("key", "value");
        config.save(file);

        assertTrue("Writer close should be tracked", config.operations.contains("close:writer"));
    }

    /**
     * Targets surviving mutant at line 223: removed call to input.close in load(Reader)
     * 
     * Refactoring: Extracted close operation into closeReader() hook
     * Observability: Tests can now verify reader is properly closed
     */
    @Test
    public void testLoad_ClosesReader() throws IOException, InvalidConfigurationException {
        String content = "key: value\n";
        Reader reader = new StringReader(content);
        
        config.load(reader);

        assertTrue("Reader close should be tracked", config.operations.contains("close:reader"));
    }

    /**
     * Targets surviving mutant at line 105: negated conditional in charset selection for save
     * 
     * Refactoring: Extracted encoding selection into selectCharsetForSave() hook
     * Observability: Tests can now verify correct encoding is selected
     */
    @Test
    public void testSave_SelectsCorrectCharset() throws IOException {
        File file = testFolder.newFile("config.yml");
        
        config.set("key", "value");
        config.save(file);

        assertTrue("Charset selection for save should be tracked", config.operations.contains("charset:save"));
        
        // Verify the expected charset was recorded
        Charset expectedCharset = FileConfiguration.UTF8_OVERRIDE && !FileConfiguration.UTF_BIG 
            ? Charsets.UTF_8 
            : Charset.defaultCharset();
        String expectedOp = "charset:save:" + expectedCharset.name();
        assertTrue("Expected charset " + expectedCharset.name() + " should be selected", 
                   config.operations.contains(expectedOp));
    }

    /**
     * Targets surviving mutant at line 169: negated conditional in charset selection for load
     * 
     * Refactoring: Extracted encoding selection into selectCharsetForLoad() hook
     * Observability: Tests can now verify correct encoding is selected
     */
    @Test
    public void testLoad_SelectsCorrectCharset() throws IOException, InvalidConfigurationException {
        File file = testFolder.newFile("config.yml");
        
        // Write a simple config file
        FileWriter writer = new FileWriter(file);
        writer.write("key: value\n");
        writer.close();
        
        config.load(file);

        assertTrue("Charset selection for load should be tracked", config.operations.contains("charset:load"));
        
        // Verify the expected charset was recorded
        Charset expectedCharset = FileConfiguration.UTF8_OVERRIDE && !FileConfiguration.UTF_BIG 
            ? Charsets.UTF_8 
            : Charset.defaultCharset();
        String expectedOp = "charset:load:" + expectedCharset.name();
        assertTrue("Expected charset " + expectedCharset.name() + " should be selected", 
                   config.operations.contains(expectedOp));
    }

    /**
     * Integration test: Verify all hook methods are called in correct sequence during save
     */
    @Test
    public void testSave_CallsAllHooksInSequence() throws IOException {
        File file = testFolder.newFile("config.yml");
        
        config.set("key", "value");
        config.save(file);

        List<String> ops = config.operations;
        
        // Verify operation sequence
        int validateIndex = ops.indexOf("validate:file");
        int createDirsIndex = ops.indexOf("createParentDirs");
        int charsetIndex = findIndexStartingWith(ops, "charset:save:");
        int writeIndex = ops.indexOf("write:data");
        int closeIndex = ops.indexOf("close:writer");

        assertTrue("All operations should be recorded", 
                   validateIndex >= 0 && createDirsIndex >= 0 && charsetIndex >= 0 
                   && writeIndex >= 0 && closeIndex >= 0);
        
        assertTrue("Operations should occur in correct order", 
                   validateIndex < createDirsIndex 
                   && createDirsIndex < charsetIndex 
                   && charsetIndex < writeIndex 
                   && writeIndex < closeIndex);
    }

    /**
     * Integration test: Verify all hook methods are called in correct sequence during load
     */
    @Test
    public void testLoad_CallsAllHooksInSequence() throws IOException, InvalidConfigurationException {
        File file = testFolder.newFile("config.yml");
        
        // Write test content
        FileWriter writer = new FileWriter(file);
        writer.write("key: value\n");
        writer.close();
        
        config.load(file);

        List<String> ops = config.operations;
        
        // Verify operation sequence
        int validateIndex = ops.indexOf("validate:file");
        int charsetIndex = findIndexStartingWith(ops, "charset:load:");
        int closeIndex = ops.indexOf("close:reader");

        assertTrue("All operations should be recorded", 
                   validateIndex >= 0 && charsetIndex >= 0 && closeIndex >= 0);
        
        assertTrue("Operations should occur in correct order", 
                   validateIndex < charsetIndex && charsetIndex < closeIndex);
    }

    private int findIndexStartingWith(List<String> list, String prefix) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Testable subclass that tracks internal operations through hook methods.
     * This makes previously unobservable behavior observable for testing.
     */
    private static class TestableFileConfiguration extends YamlConfiguration {
        public final List<String> operations = new ArrayList<String>();

        @Override
        protected void validateNotNull(Object obj, String message) {
            if (message.contains("File")) {
                operations.add("validate:file");
            } else {
                operations.add("validate:other");
            }
            super.validateNotNull(obj, message);
        }

        @Override
        protected void createParentDirectories(File file) throws IOException {
            operations.add("createParentDirs");
            super.createParentDirectories(file);
        }

        @Override
        protected void saveToFile(File file) throws IOException {
            operations.add("saveToFile");
            super.saveToFile(file);
        }

        @Override
        protected void writeToWriter(Writer writer, String data) throws IOException {
            operations.add("write:data");
            super.writeToWriter(writer, data);
        }

        @Override
        protected void closeWriter(Writer writer) throws IOException {
            operations.add("close:writer");
            super.closeWriter(writer);
        }

        @Override
        protected void closeReader(Reader reader) throws IOException {
            operations.add("close:reader");
            super.closeReader(reader);
        }

        @Override
        protected Charset selectCharsetForSave() {
            Charset charset = super.selectCharsetForSave();
            operations.add("charset:save");
            operations.add("charset:save:" + charset.name());
            return charset;
        }

        @Override
        protected Charset selectCharsetForLoad() {
            Charset charset = super.selectCharsetForLoad();
            operations.add("charset:load");
            operations.add("charset:load:" + charset.name());
            return charset;
        }
    }
}