package com.github.allink28;

import org.json.JSONObject;

/**
 * Created by Allen on 4/4/2016.
 */
public class ItemAndScore implements Comparable<ItemAndScore>{
    private JSONObject item;
    private double score;

    public ItemAndScore(JSONObject item, double score) {
        this.item = item;
        this.score = score;
    }

    /**
     * Make this object sortable by implementing Comparable method
     */
    @Override
    public int compareTo(ItemAndScore that){
        if (this.score < that.getScore())
            return -1;
        if (this.score > that.getScore())
            return 1;
        return 0;
    }

    public double getScore() {
        return score;
    }

    public JSONObject getItem() {
        return item;
    }

    public String toString() {
        return item + " Overall review score: " + score;
    }
}
