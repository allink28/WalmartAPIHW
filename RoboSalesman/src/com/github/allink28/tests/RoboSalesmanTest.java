package com.github.allink28.tests;

import com.github.allink28.RoboSalesman;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Allink on 4/2/2016.
 */
public class RoboSalesmanTest {

    @Test
    public void parseInput() throws Exception {
        RoboSalesman wally = new RoboSalesman();

        assertEquals("Jeans", wally.parseInput(new String[] {"Jeans"}));
        assertEquals("Baked beans", wally.parseInput(new String[] {"Baked", "beans"}));
        assertNull(wally.parseInput(new String[3]));
        assertNull(wally.parseInput(new String[1]));
        assertNull(wally.parseInput(new String[0]));
        System.out.println("Finished parseInput() tests");
    }

}