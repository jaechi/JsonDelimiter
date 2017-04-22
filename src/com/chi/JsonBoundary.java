package com.chi;

/**
 * Created on 4/22/17.
 */
public class JsonBoundary {
    public int startIndex = -1;
    public int endIndex = -1;

    public JsonBoundary(int start, int end) {
        startIndex = start;
        endIndex = end;
    }
}
