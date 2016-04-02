package com.github.allink28;

/**
 * Created by Allen on 4/2/2016.
 */
public class RoboSalesman {

    public static void main(String[] args) {
        RoboSalesman wally = new RoboSalesman();
        wally.startWork(args);
        //TODO intro text
        //TODO buffered input reader on a loop
    }

    public RoboSalesman() {

    }

    private void startWork(String[] args) {
        String userInput = parseInput(args);
        if (userInput == null || userInput.trim().length() == 0) {
            System.err.println("No search string provided!");
            return;
        }
        search(userInput);
    }

    /**
     * @param args Input straight from the user. Presumably a product they're searching for.
     * @return null If no search string was provided. Otherwise returns what the User
     *  typed in as a string, including spaces between words.
     */
    public String parseInput(String[] args) {
        if (args.length > 0) {
            StringBuilder searchString = new StringBuilder();
            for (String s : args) {
                if (s != null) {
                    searchString.append(" ").append(s);
                }
            }
            if (searchString.length() > 1)
                return searchString.toString().trim();
        }
        return null;
    }

    private void search(String searchString) {

    }

}
