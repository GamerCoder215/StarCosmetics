package me.gamercoder215.starcosmetics.api.cosmetics.structure;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestStructureReader {

    @Test
    @DisplayName("Test Raw Points")
    public void testRawPoints() {
        Assertions.assertEquals(point(1, 2, 3), StructureReader.readRawPoint("[1,2,3]"));
        Assertions.assertEquals(point(-2, 5, 8), StructureReader.readRawPoint("[ -2, 5, 8 ]"));
        Assertions.assertEquals(point(0, 0, 0), StructureReader.readRawPoint("[0, 0, 0]"));

        Assertions.assertThrows(MalformedStructureException.class, () -> StructureReader.readRawPoint("[1,2,3"));
        Assertions.assertThrows(MalformedStructureException.class, () -> StructureReader.readRawPoint("1,2,3]"));

        Assertions.assertEquals(point(3, 2, 1), StructureReader.readRawPoint("[3,2,1];"));
        Assertions.assertEquals(point(3, 2, 1), StructureReader.readRawPoint("[3,2,1]; "));
    }

    @Test
    @DisplayName("Test Multiple Raw Points")
    public void testMultipleRawPoints() {
        Assertions.assertEquals(
                Lists.newArrayList(point(1, 2, 3), point(-2, 5, 8)),
                StructureReader.readPoints("[1,2,3]*[-2, 5, 8]")
        );
        Assertions.assertEquals(
                Lists.newArrayList(point(1, 2, 3), point(-3, -9, 12), point(0, 0, 0)),
                StructureReader.readPoints("[1,2,3]*[-3, -9, 12]*[ 0, 0, 0 ]")
        );

        Assertions.assertThrows(MalformedStructureException.class, () -> StructureReader.readPoints("[1,2,3]*[-2, 5, 8"));
        Assertions.assertThrows(MalformedStructureException.class, () -> StructureReader.readPoints("[1,2,3*[-2, 5, 8]"));

        Assertions.assertEquals(
                Lists.newArrayList(point(1, 2, 3), point(-2, 5, 8)),
                StructureReader.readPoints("[1,2,3]*[-2, 5, 8];")
        );
        Assertions.assertEquals(
                Lists.newArrayList(point(1, 2, 3), point(-2, 5, 8)),
                StructureReader.readPoints("[1,2,3]*[-2, 5, 8]; ")
        );
    }

    @Test
    @DisplayName("Test Cuboid Points")
    public void testCuboidPoints() {
        Assertions.assertEquals(9, StructureReader.readPoints("(1,-1,0,0,1,-1)^[1, 2, 3]").size());
        Assertions.assertEquals(18, StructureReader.readPoints("(1,-1,0,0,1,-1)^[1, 2, 3]*(1,-1,0,0,1,-1)^[-3,5,7]").size());

        Assertions.assertEquals(Arrays.asList(
                point(0, 5, 0), point(0, 5, 1), point(0, 5, 2),
                point(1, 5, 0), point(1, 5, 1), point(1, 5, 2),
                point(2, 5, 0), point(2, 5, 1), point(2, 5, 2)
        ), StructureReader.readPoints("(1,-1,0,0,1,-1)^[0,5,0]"));
    }

    public static StructurePoint point(int x, int y, int z) {
        return new StructurePoint(x, y, z);
    }

}
