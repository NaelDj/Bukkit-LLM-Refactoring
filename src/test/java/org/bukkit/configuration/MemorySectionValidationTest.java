package org.bukkit.configuration;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for validation observable behavior in MemorySection.
 * These tests target surviving mutants related to Validate.notNull/notEmpty calls.
 */
public class MemorySectionValidationTest {
    private MemorySection section;

    @Before
    public void setUp() {
        section = (MemorySection) new MemoryConfiguration().createSection("test");
    }

    /**
     * Test for surviving mutants at lines 59, 60 (constructor - Validate.notNull(parent/path)).
     * Verifies that validation logic for parent parameter works correctly.
     */
    @Test
    public void testIsValidParentWithNull() {
        assertFalse("isValidParent should return false for null parent",
                    MemorySection.isValidParent(null));
    }

    /**
     * Test for surviving mutants at lines 59, 60 (constructor - Validate.notNull(parent/path)).
     * Verifies that validation logic for parent parameter works correctly with valid parent.
     */
    @Test
    public void testIsValidParentWithValidParent() {
        ConfigurationSection validParent = new MemoryConfiguration();
        assertTrue("isValidParent should return true for valid parent",
                   MemorySection.isValidParent(validParent));
    }

    /**
     * Test for surviving mutant at line 137 (addDefault - Validate.notNull(path)).
     * Verifies that path validation works correctly.
     */
    @Test
    public void testIsValidPathWithNull() {
        assertFalse("isValidPath should return false for null path",
                    section.isValidPath(null));
    }

    /**
     * Test for surviving mutant at line 137 (addDefault - Validate.notNull(path)).
     * Verifies that path validation works correctly with valid path.
     */
    @Test
    public void testIsValidPathWithValidPath() {
        assertTrue("isValidPath should return true for valid path",
                   section.isValidPath("some.path"));
    }

    /**
     * Test for surviving mutant at line 137 (addDefault - Validate.notNull(path)).
     * Verifies that empty string is still a valid path (not null).
     */
    @Test
    public void testIsValidPathWithEmptyString() {
        assertTrue("isValidPath should return true for empty string (not null)",
                   section.isValidPath(""));
    }

    /**
     * Test for surviving mutants at lines 163, 234 (set/createSection - Validate.notEmpty(path)).
     * Verifies that non-empty path validation works correctly with null.
     */
    @Test
    public void testIsValidNonEmptyPathWithNull() {
        assertFalse("isValidNonEmptyPath should return false for null",
                    section.isValidNonEmptyPath(null));
    }

    /**
     * Test for surviving mutants at lines 163, 234 (set/createSection - Validate.notEmpty(path)).
     * Verifies that non-empty path validation works correctly with empty string.
     */
    @Test
    public void testIsValidNonEmptyPathWithEmptyString() {
        assertFalse("isValidNonEmptyPath should return false for empty string",
                    section.isValidNonEmptyPath(""));
    }

    /**
     * Test for surviving mutants at lines 163, 234 (set/createSection - Validate.notEmpty(path)).
     * Verifies that non-empty path validation works correctly with valid path.
     */
    @Test
    public void testIsValidNonEmptyPathWithValidPath() {
        assertTrue("isValidNonEmptyPath should return true for valid non-empty path",
                   section.isValidNonEmptyPath("validPath"));
    }

    /**
     * Test for surviving mutant at line 779 (createPath - Validate.notNull(section)).
     * Verifies that section validation works correctly.
     */
    @Test
    public void testIsValidSectionWithNull() {
        assertFalse("isValidSection should return false for null section",
                    MemorySection.isValidSection(null));
    }

    /**
     * Test for surviving mutant at line 779 (createPath - Validate.notNull(section)).
     * Verifies that section validation works correctly with valid section.
     */
    @Test
    public void testIsValidSectionWithValidSection() {
        ConfigurationSection validSection = new MemoryConfiguration();
        assertTrue("isValidSection should return true for valid section",
                   MemorySection.isValidSection(validSection));
    }

    /**
     * Integration test: verify that methods actually use validation.
     * This test confirms that calling methods with null throws expected exceptions.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultWithNullPathThrowsException() {
        section.addDefault(null, "value");
    }

    /**
     * Integration test: verify set method validates empty path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetWithEmptyPathThrowsException() {
        section.set("", "value");
    }

    /**
     * Integration test: verify get method validates null path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetWithNullPathThrowsException() {
        section.get(null, "default");
    }

    /**
     * Integration test: verify createSection validates empty path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSectionWithEmptyPathThrowsException() {
        section.createSection("");
    }

    /**
     * Integration test: verify createPath validates null section.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreatePathWithNullSectionThrowsException() {
        MemorySection.createPath(null, "key");
    }

    /**
     * Test that validation correctly distinguishes between null and empty.
     * This ensures the validation logic is precise.
     */
    @Test
    public void testValidationDistinguishesNullFromEmpty() {
        // isValidPath accepts empty (just checks not null)
        assertTrue("isValidPath should accept empty string",
                   section.isValidPath(""));
        assertFalse("isValidPath should reject null",
                    section.isValidPath(null));
        
        // isValidNonEmptyPath rejects both
        assertFalse("isValidNonEmptyPath should reject empty string",
                    section.isValidNonEmptyPath(""));
        assertFalse("isValidNonEmptyPath should reject null",
                    section.isValidNonEmptyPath(null));
    }

    /**
     * Test validation with various whitespace paths.
     * Verifies that whitespace-only strings are still considered non-empty.
     */
    @Test
    public void testValidationWithWhitespacePaths() {
        // Single space is not empty
        assertTrue("isValidNonEmptyPath should accept single space",
                   section.isValidNonEmptyPath(" "));
        
        // Tab is not empty
        assertTrue("isValidNonEmptyPath should accept tab",
                   section.isValidNonEmptyPath("\t"));
        
        // Multiple spaces are not empty
        assertTrue("isValidNonEmptyPath should accept multiple spaces",
                   section.isValidNonEmptyPath("   "));
    }

    /**
     * Test that validation methods work correctly across different scenarios.
     * This comprehensive test ensures the validation logic is consistent.
     */
    @Test
    public void testValidationConsistency() {
        // Test multiple valid paths
        String[] validPaths = {"path", "my.path", "a.b.c", "123", "path-with-dash"};
        for (String path : validPaths) {
            assertTrue("isValidPath should accept: " + path,
                       section.isValidPath(path));
            assertTrue("isValidNonEmptyPath should accept: " + path,
                       section.isValidNonEmptyPath(path));
        }
        
        // Test that null is consistently rejected
        assertFalse(section.isValidPath(null));
        assertFalse(section.isValidNonEmptyPath(null));
        
        // Test that empty is handled differently
        assertTrue(section.isValidPath(""));
        assertFalse(section.isValidNonEmptyPath(""));
    }

    /**
     * Test validation of parent in realistic scenario.
     * Verifies that validation works when creating subsections.
     */
    @Test
    public void testParentValidationInSubsectionCreation() {
        ConfigurationSection subsection = section.createSection("subsection");
        
        // The parent of the subsection should be valid
        assertTrue("Parent of created subsection should be valid",
                   MemorySection.isValidParent(subsection.getParent()));
        
        // The section itself should be valid
        assertTrue("Created subsection should be valid section",
                   MemorySection.isValidSection(subsection));
    }

    /**
     * Test that validation catches edge cases with special characters.
     * This ensures validation works with paths containing dots, slashes, etc.
     */
    @Test
    public void testValidationWithSpecialCharacters() {
        // Paths with dots (common in configuration)
        assertTrue(section.isValidNonEmptyPath("server.port"));
        assertTrue(section.isValidNonEmptyPath("database.connection.url"));
        
        // Paths with numbers
        assertTrue(section.isValidNonEmptyPath("item.123"));
        
        // Paths starting with special chars (still non-empty)
        assertTrue(section.isValidNonEmptyPath(".hidden"));
        assertTrue(section.isValidNonEmptyPath("-dash"));
    }
}