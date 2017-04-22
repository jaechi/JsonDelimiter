package com.chi;

import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Created on 4/22/17.
 */
public class JsonDelimiter {


    private static final Logger log = Logger.getLogger(JsonDelimiter.class);

    private final StringBuffer buffer = new StringBuffer();

    private static final String OPEN = "{";
    private static final String CLOSE = "}";

    /**
     *
     * @param data
     * @param size
     */
    public void add(char[] data, int size) {
        buffer.append(data, 0, size);
    }

    /**
     *
     * @return
     */
    public String split() {
        String json = null;

        if (buffer.length() == 0) {
            return json;
        }

        Stack<JsonBoundary> boundaries = new Stack<>();
        log.info("buffer:\n" + buffer.toString());

        boolean closeBracket = false;
        int openBracketIndex = 0;
        while (!closeBracket) {
            openBracketIndex = findNextOpenBracket(boundaries, openBracketIndex);
            if (openBracketIndex < 0) {
                closeBracket = true;
            } else {
                // advance so that we can find the next boundary
                openBracketIndex++;
            }
        }

        boolean finished = false;
        int startIndex = -1;
        while (!boundaries.empty() && !finished) {
            int closeBracketIndex = findNextCloseBracket(boundaries, startIndex);

            if (boundaries.size() == 1) {
                // let's check to see if we have a complete boundary
                JsonBoundary boundary = boundaries.peek();
                if (boundary.startIndex >= 0 && boundary.endIndex > boundary.startIndex) {
                    // we have a complete json, let's chop and return
                    json = cut(boundary.startIndex, boundary.endIndex + 1);
                    boundaries.pop();
                } else {
                    // single boundary, but not valid
                    finished = true;
                }
            } else if (closeBracketIndex > startIndex) {
                startIndex = closeBracketIndex + 1;
            } else {
                JsonBoundary boundary = boundaries.peek();
                if (boundary.startIndex >= 0 && boundary.endIndex > boundary.startIndex) {
                    boundaries.pop();
                    startIndex = boundary.endIndex + 1;
                } else {
                    // no completed boundary, finish splitting
                    finished = true;
                }
            }
        }

        return json;
    }

    String cut(int start, int stop) {
        String json = buffer.substring(start, stop);
        buffer.delete(start, stop);
        return json;
    }


    /**
     * searches for the next open bracket, returns
     * @param boundaries
     * @param start
     * @return -1 when close bracket is found, openBracketIndex otherwise
     */
    int findNextOpenBracket(Stack<JsonBoundary> boundaries, int start) {

        int index = -1;
        int openBracketIndex = buffer.indexOf(OPEN, start);
        int closeBracketIndex = buffer.indexOf(CLOSE, start);

        if (openBracketIndex >= start && closeBracketIndex > start ) {

            // need to skip {}. This is an empty map
            if (openBracketIndex + 1 == closeBracketIndex) {
                index = closeBracketIndex;
            } else if (openBracketIndex < closeBracketIndex) {
                boundaries.add(new JsonBoundary(openBracketIndex, -1));
                index = openBracketIndex;
            }

        } else if (openBracketIndex < 0 && closeBracketIndex >= start) {
            // no openBracket, found closeBracket, go back and work off the stack
            index = -1;
        }

        return index;
    }

    /**
     * searches for close bracket
     * @param boundaries
     * @return -1 if no longer needs to move forward, otherwise next start index
     */
    int findNextCloseBracket(Stack<JsonBoundary> boundaries, int start) {
        int index = -1;
        JsonBoundary boundary = boundaries.peek();
        if (start < 0) {
            // first time, use the startIndex from the boundary
            start = boundary.startIndex + 1;
        }
        int openBracketIndex = buffer.indexOf(OPEN, start);
        int closeBracketIndex = buffer.indexOf(CLOSE, start);

        if (openBracketIndex < 0) {
            if (closeBracketIndex >= start) {
                // no more open bracket, complete the boundary
                boundary.endIndex = closeBracketIndex;
            }
        } else {  // if open bracket is found
            if (openBracketIndex + 1 == closeBracketIndex) {
                // by pass empty map, {}
                index = closeBracketIndex;
            } else if (closeBracketIndex >= start) {
                // if close bracket is found
                if (openBracketIndex < closeBracketIndex) {
                    // we have another boundary start inside.
                    boundaries.push(new JsonBoundary(openBracketIndex, -1));
                } else {
                    // we found a closeBracket, end of boundary
                    boundary.endIndex = closeBracketIndex;
                }
            }

        }

        return index;
    }
}
