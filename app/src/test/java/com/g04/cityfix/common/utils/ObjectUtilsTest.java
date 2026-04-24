package com.g04.cityfix.common.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Map;

/**
 * @author Jerry Yang
 */
public class ObjectUtilsTest {

    static class Person {
        private final String name;
        private final int    age;
        Person(String name, int age) { this.name = name; this.age = age; }
    }

    static class Mixed {
        private String a = null;     // null → skipped
        private String b = "bee";    // non-null → kept
    }

    static class Empty { }          // no declared fields
    /* ------------------------ */

    /** Covers both fields, true branch of (value != null) twice */
    @Test
    public void toMap_returnsAllNonNullFields() {
        Person p = new Person("Alice", 23);

        Map<String,Object> map = ObjectUtils.toMap(p);

        assertEquals(2, map.size());
        assertEquals("Alice", map.get("name"));
        assertEquals(23, map.get("age"));
    }

    /** Covers false branch (field “a”) and true branch (field “b”) */
    @Test
    public void toMap_skipsNullFields() {
        Mixed m = new Mixed();

        Map<String,Object> map = ObjectUtils.toMap(m);

        assertFalse(map.containsKey("a"));
        assertEquals("bee", map.get("b"));
    }

    /** Edge case: no fields → loop never executes */
    @Test
    public void toMap_handlesClassWithNoFields() {
        Map<String,Object> map = ObjectUtils.toMap(new Empty());

        assertTrue(map.isEmpty());
    }
}