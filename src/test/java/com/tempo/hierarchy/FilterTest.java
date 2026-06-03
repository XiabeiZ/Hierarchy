package com.tempo.hierarchy;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Hierarchy Filter Tests")

class FilterTest {
    @Test
    void testFilter() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 3 != 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 5, 8, 10, 11},
                new int[]{0, 1, 1, 0, 1, 2}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testEmptyHierarchy() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(new int[]{}, new int[]{});
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, id -> true);
        assertEquals("[]", filteredActual.formatString());
    }

    @Test
    void testSingleRoot() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(new int[]{1}, new int[]{0});
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, id -> id != 1);
        assertEquals("[]", filteredActual.formatString());
    }

    @Test
    void testFilterAll() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 2}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, id -> false);
        assertEquals("[]", filteredActual.formatString());
    }

    @Test
    void testFilterRoot() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6},
                new int[]{0, 1, 0, 1, 2, 0}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, id -> id != 3);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 6},
                new int[]{0, 1, 0}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilterMiddleNode() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 2, 1}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, id -> id != 2);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 4},
                new int[]{0, 1}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }
}


