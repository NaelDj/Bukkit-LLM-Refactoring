package org.bukkit.configuration;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for type checking observable behavior in MemorySection.
 * These tests target surviving mutants related to instanceof checks.
 */
public class MemorySectionTypeCheckTest {
    private MemorySection section;

    @Before
    public void setUp() {
        section = (MemorySection) new MemoryConfiguration().createSection("test");
    }

    /**
     * Test for surviving mutant at line 296 (getInt - negated conditional).
     * When a non-Number type is stored, isValidIntType should return false.
     */
    @Test
    public void testIsValidIntTypeWithWrongType() {
        section.set("stringValue", "not a number");
        
        // The new observable method should detect wrong type
        assertFalse("isValidIntType should return false for String", 
                    section.isValidIntType("stringValue"));
        
        // Verify the getter returns default when wrong type
        assertEquals(99, section.getInt("stringValue", 99));
    }

    /**
     * Test for surviving mutant at line 296 (getInt - negated conditional).
     * When a Number type is stored (even if not Integer), isValidIntType should return true.
     */
    @Test
    public void testIsValidIntTypeWithCorrectType() {
        section.set("doubleAsInt", 42.7);
        
        // Should detect it's a valid Number type
        assertTrue("isValidIntType should return true for Double", 
                   section.isValidIntType("doubleAsInt"));
        
        // Should convert successfully
        assertEquals(42, section.getInt("doubleAsInt"));
    }

    /**
     * Test for surviving mutant at line 296.
     * When value doesn't exist, isValidIntType should return false.
     */
    @Test
    public void testIsValidIntTypeWithNonExistentPath() {
        assertFalse("isValidIntType should return false for non-existent path",
                    section.isValidIntType("nonExistent"));
    }

    /**
     * Test for surviving mutant at line 326 (getDouble - negated conditional).
     * When a non-Number type is stored, isValidDoubleType should return false.
     */
    @Test
    public void testIsValidDoubleTypeWithWrongType() {
        section.set("boolValue", true);
        
        assertFalse("isValidDoubleType should return false for Boolean",
                    section.isValidDoubleType("boolValue"));
        
        assertEquals(3.14, section.getDouble("boolValue", 3.14), 0.001);
    }

    /**
     * Test for surviving mutant at line 326 (getDouble - negated conditional).
     * When a Number type is stored, isValidDoubleType should return true.
     */
    @Test
    public void testIsValidDoubleTypeWithCorrectType() {
        section.set("intAsDouble", 42);
        
        assertTrue("isValidDoubleType should return true for Integer",
                   section.isValidDoubleType("intAsDouble"));
        
        assertEquals(42.0, section.getDouble("intAsDouble"), 0.001);
    }

    /**
     * Test for surviving mutant at line 341 (getLong - negated conditional).
     * When a non-Number type is stored, isValidLongType should return false.
     */
    @Test
    public void testIsValidLongTypeWithWrongType() {
        section.set("listValue", java.util.Arrays.asList(1, 2, 3));
        
        assertFalse("isValidLongType should return false for List",
                    section.isValidLongType("listValue"));
        
        assertEquals(100L, section.getLong("listValue", 100L));
    }

    /**
     * Test for surviving mutant at line 341 (getLong - negated conditional).
     * When a Number type is stored, isValidLongType should return true.
     */
    @Test
    public void testIsValidLongTypeWithCorrectType() {
        section.set("shortAsLong", (short) 255);
        
        assertTrue("isValidLongType should return true for Short",
                   section.isValidLongType("shortAsLong"));
        
        assertEquals(255L, section.getLong("shortAsLong"));
    }

    /**
     * Test for surviving mutant at line 357 (getList - negated conditional).
     * When a non-List type is stored, isValidListType should return false.
     */
    @Test
    public void testIsValidListTypeWithWrongType() {
        section.set("intValue", 42);
        
        assertFalse("isValidListType should return false for Integer",
                    section.isValidListType("intValue"));
        
        assertNull("getList should return null for non-List",
                   section.getList("intValue", null));
    }

    /**
     * Test for surviving mutant at line 357 (getList - negated conditional).
     * When a List type is stored, isValidListType should return true.
     */
    @Test
    public void testIsValidListTypeWithCorrectType() {
        java.util.List<String> list = java.util.Arrays.asList("a", "b", "c");
        section.set("listValue", list);
        
        assertTrue("isValidListType should return true for List",
                   section.isValidListType("listValue"));
        
        assertEquals(list, section.getList("listValue"));
    }

    /**
     * Test for surviving mutant at line 620 (getVector - negated conditional).
     * When a non-Vector type is stored, isValidVectorType should return false.
     */
    @Test
    public void testIsValidVectorTypeWithWrongType() {
        section.set("stringValue", "not a vector");
        
        assertFalse("isValidVectorType should return false for String",
                    section.isValidVectorType("stringValue"));
        
        Vector defaultVec = new Vector(1, 2, 3);
        assertEquals(defaultVec, section.getVector("stringValue", defaultVec));
    }

    /**
     * Test for surviving mutant at line 620 (getVector - negated conditional).
     * When a Vector type is stored, isValidVectorType should return true.
     */
    @Test
    public void testIsValidVectorTypeWithCorrectType() {
        Vector vec = new Vector(10, 20, 30);
        section.set("vectorValue", vec);
        
        assertTrue("isValidVectorType should return true for Vector",
                   section.isValidVectorType("vectorValue"));
        
        assertEquals(vec, section.getVector("vectorValue"));
    }

    /**
     * Test for surviving mutant at line 650 (getItemStack - negated conditional).
     * When a non-ItemStack type is stored, isValidItemStackType should return false.
     */
    @Test
    public void testIsValidItemStackTypeWithWrongType() {
        section.set("numberValue", 123);
        
        assertFalse("isValidItemStackType should return false for Integer",
                    section.isValidItemStackType("numberValue"));
        
        ItemStack defaultStack = new ItemStack(Material.STONE);
        assertEquals(defaultStack, section.getItemStack("numberValue", defaultStack));
    }

    /**
     * Test for surviving mutant at line 650 (getItemStack - negated conditional).
     * When an ItemStack type is stored, isValidItemStackType should return true.
     */
    @Test
    public void testIsValidItemStackTypeWithCorrectType() {
        ItemStack stack = new ItemStack(Material.DIAMOND, 64);
        section.set("itemValue", stack);
        
        assertTrue("isValidItemStackType should return true for ItemStack",
                   section.isValidItemStackType("itemValue"));
        
        assertEquals(stack, section.getItemStack("itemValue"));
    }

    /**
     * Test for surviving mutant at line 665 (getColor - negated conditional).
     * When a non-Color type is stored, isValidColorType should return false.
     */
    @Test
    public void testIsValidColorTypeWithWrongType() {
        section.set("vectorValue", new Vector(1, 2, 3));
        
        assertFalse("isValidColorType should return false for Vector",
                    section.isValidColorType("vectorValue"));
        
        Color defaultColor = Color.RED;
        assertEquals(defaultColor, section.getColor("vectorValue", defaultColor));
    }

    /**
     * Test for surviving mutant at line 665 (getColor - negated conditional).
     * When a Color type is stored, isValidColorType should return true.
     */
    @Test
    public void testIsValidColorTypeWithCorrectType() {
        Color color = Color.fromRGB(100, 150, 200);
        section.set("colorValue", color);
        
        assertTrue("isValidColorType should return true for Color",
                   section.isValidColorType("colorValue"));
        
        assertEquals(color, section.getColor("colorValue"));
    }

    /**
     * Test that demonstrates type checking with multiple different wrong types.
     * This test ensures the instanceof checks work correctly for all scenarios.
     */
    @Test
    public void testTypeCheckingWithVariousWrongTypes() {
        // Store values of various types
        section.set("string", "text");
        section.set("bool", false);
        section.set("list", java.util.Arrays.asList(1, 2));
        section.set("vector", new Vector(1, 1, 1));
        
        // Verify int type checking fails for all non-Number types
        assertFalse(section.isValidIntType("string"));
        assertFalse(section.isValidIntType("bool"));
        assertFalse(section.isValidIntType("list"));
        assertFalse(section.isValidIntType("vector"));
        
        // Verify double type checking fails for all non-Number types
        assertFalse(section.isValidDoubleType("string"));
        assertFalse(section.isValidDoubleType("bool"));
        assertFalse(section.isValidDoubleType("list"));
        assertFalse(section.isValidDoubleType("vector"));
        
        // Verify long type checking fails for all non-Number types
        assertFalse(section.isValidLongType("string"));
        assertFalse(section.isValidLongType("bool"));
        assertFalse(section.isValidLongType("list"));
        assertFalse(section.isValidLongType("vector"));
    }

    /**
     * Test that verifies all Number types are accepted for numeric getters.
     * This ensures the type checking logic accepts any Number subclass.
     */
    @Test
    public void testAllNumberTypesAreValid() {
        section.set("byte", (byte) 1);
        section.set("short", (short) 2);
        section.set("int", 3);
        section.set("long", 4L);
        section.set("float", 5.0f);
        section.set("double", 6.0);
        
        // All should be valid for int
        assertTrue(section.isValidIntType("byte"));
        assertTrue(section.isValidIntType("short"));
        assertTrue(section.isValidIntType("int"));
        assertTrue(section.isValidIntType("long"));
        assertTrue(section.isValidIntType("float"));
        assertTrue(section.isValidIntType("double"));
        
        // All should be valid for double
        assertTrue(section.isValidDoubleType("byte"));
        assertTrue(section.isValidDoubleType("short"));
        assertTrue(section.isValidDoubleType("int"));
        assertTrue(section.isValidDoubleType("long"));
        assertTrue(section.isValidDoubleType("float"));
        assertTrue(section.isValidDoubleType("double"));
        
        // All should be valid for long
        assertTrue(section.isValidLongType("byte"));
        assertTrue(section.isValidLongType("short"));
        assertTrue(section.isValidLongType("int"));
        assertTrue(section.isValidLongType("long"));
        assertTrue(section.isValidLongType("float"));
        assertTrue(section.isValidLongType("double"));
    }
}