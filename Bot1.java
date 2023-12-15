import java.util.*;

public class Bot1 {

    private static final int OPEN = 0;
    private static final int LEAK = -5;

    // Method to find the closest potential leak from starting coordinates
    public static int findClosestPotentialLeak(int[][] ship, boolean[][] detectionSquare, int[] startCoords, Stack<Integer> pathToNearestTrue) {
        // Keep track of parents so that you can build path to button
        HashMap<Integer, Integer> parent = new HashMap<Integer, Integer>();
        int size = ship.length;
        Queue<Integer> fringe = new LinkedList<Integer>();
        ArrayList<Integer> closedSet = new ArrayList<Integer>();
        int startLocation = ShipTools.convertCoordinatesToIndex(startCoords, size);

        // Keep track of the element which is the nearest true element
        int nearestTrueElement = -1;


        // Perform BFS starting from startLocation
        parent.put(startLocation, startLocation);
        fringe.add(startLocation);
        closedSet.add(startLocation);
        while (fringe.peek() != null) {
            int currentState = fringe.remove();
            int[] currentCoords = ShipTools.convertIndexToCoordinates(currentState, size);
            int currentCoordsR = currentCoords[0];
            int currentCoordsC = currentCoords[1];

            // If a true cell has been found, break
            if (detectionSquare[currentCoordsR][currentCoordsC]) {
                nearestTrueElement = currentState;
                break;
            } else {
                // find where currentState is on ship and add its valid children to fringe
                int elementCounter = 1;
                for (int r = 0; r < size; r++) {
                    boolean breakout = false;
                    for (int c = 0; c < size; c++) {
                        if (elementCounter == currentState) {
                            // check to see if top neighbor is valid child
                            if (r > 0) {
                                if ( (ship[r-1][c] == OPEN || ship[r-1][c] == LEAK) && (!closedSet.contains(elementCounter-size)) ) {
                                    fringe.add(elementCounter-size);
                                    parent.put(elementCounter-size, currentState);
                                    closedSet.add(elementCounter-size);
                                }
                            }

                            // check to see if bottom neighbor is valid child
                            if (r < (size - 1)) {
                                if ( (ship[r+1][c] == OPEN || ship[r+1][c] == LEAK) && (!closedSet.contains(elementCounter+size)) ) {
                                    fringe.add(elementCounter+size);
                                    parent.put(elementCounter+size, currentState);
                                    closedSet.add(elementCounter+size);
                                }
                            }

                            // check to see if left neighbor is valid child
                            if (c > 0) {
                                if ( (ship[r][c-1] == OPEN || ship[r][c-1] == LEAK) && (!closedSet.contains(elementCounter-1)) ) {
                                    fringe.add(elementCounter-1);
                                    parent.put(elementCounter-1, currentState);
                                    closedSet.add(elementCounter-1);
                                }
                            }

                            // check to see if right neighbor is valid child
                            if (c < (size - 1)) {
                                if ( (ship[r][c+1] == OPEN || ship[r][c+1] == LEAK) && (!closedSet.contains(elementCounter+1)) ) {
                                    fringe.add(elementCounter+1);
                                    parent.put(elementCounter+1, currentState);
                                    closedSet.add(elementCounter+1);
                                }
                            }

                            breakout = true;
                            break;
                        }

                        elementCounter++;
                    }

                    if (breakout) {
                        break;
                    }
                }
            }
        }

        // Traverse parent HashMap and find the number of steps it would take
        // to get to the nearestTrueElement, and add path to pathToNearestTrue stack
        int numberMoves = 1;
        if (parent.containsKey(nearestTrueElement)) {
            int traversalStart = nearestTrueElement;
            while (traversalStart != startLocation) {
                numberMoves++;
                pathToNearestTrue.push(traversalStart);
                traversalStart = parent.get(traversalStart);
            }

            pathToNearestTrue.push(startLocation);
        }

        return numberMoves;
    }

    // Method to find which path the bot should take to the nearest true cell
    public static Stack<Integer> choosePath(int[][] ship, int[] botCoords, boolean[][] detectionSquare) {
        boolean leakFound = false;
        int botR = botCoords[0];
        int botC = botCoords[1];

        // Keep track of coords to run BFS from
        int[] startCoords = new int[2];

        // ArrayList to keep track of the number of moves to get to a possible leak cell
        // from each possible cell adjacent to bot.
        ArrayList<Integer> movesToTrue = new ArrayList<Integer>(4);

        // Stacks to keep track of path to nearestTrueCell
        Stack<Integer> pathToNearestTrue1 = new Stack<Integer>();
        Stack<Integer> pathToNearestTrue2 = new Stack<Integer>();
        Stack<Integer> pathToNearestTrue3 = new Stack<Integer>();
        Stack<Integer> pathToNearestTrue4 = new Stack<Integer>();

        // Get number of moves from cell above bot
        if (botR > 0) {
            if ( (ship[botR-1][botC] == OPEN) || (ship[botR-1][botC] == LEAK) ) {
                startCoords[0] = botR - 1;
                startCoords[1] = botC;
                movesToTrue.add( findClosestPotentialLeak(ship, detectionSquare, startCoords, pathToNearestTrue1) );
            } else {
                movesToTrue.add(Integer.MAX_VALUE);
            }
        } else {
            movesToTrue.add(Integer.MAX_VALUE);
        }

        // Get number of moves from cell below bot
        if (botR < (ship.length - 1) ) {
            if ( (ship[botR+1][botC] == OPEN) || (ship[botR+1][botC] == LEAK) ) {
                startCoords[0] = botR + 1;
                startCoords[1] = botC;
                movesToTrue.add( findClosestPotentialLeak(ship, detectionSquare, startCoords, pathToNearestTrue2) );
            } else {
                movesToTrue.add(Integer.MAX_VALUE);
            }
        } else {
            movesToTrue.add(Integer.MAX_VALUE);
        }

        // Get number of moves from cell left of bot
        if (botC > 0) {
            if ( (ship[botR][botC-1] == OPEN) || (ship[botR][botC-1] == LEAK) ) {
                startCoords[0] = botR;
                startCoords[1] = botC - 1;
                movesToTrue.add( findClosestPotentialLeak(ship, detectionSquare, startCoords, pathToNearestTrue3) );
            } else {
                movesToTrue.add(Integer.MAX_VALUE);
            }
        } else {
            movesToTrue.add(Integer.MAX_VALUE);
        }

        // Get number of moves from cell right of bot
        if (botC < (ship.length - 1) ) {
            if ( (ship[botR][botC+1] == OPEN) || (ship[botR][botC+1] == LEAK) ) {
                startCoords[0] = botR;
                startCoords[1] = botC + 1;
                movesToTrue.add( findClosestPotentialLeak(ship, detectionSquare, startCoords, pathToNearestTrue4) );
            } else {
                movesToTrue.add(Integer.MAX_VALUE);
            }
        } else {
            movesToTrue.add(Integer.MAX_VALUE);
        }

        int leastMoves = movesToTrue.get(0);
        // Find the least number of moves it takes to get from a cell adjacent to bot
        // to a true cell
        for (int i = 0; i < movesToTrue.size(); i++) {
            if (movesToTrue.get(i) == null) {
                continue;
            }
            for (int j = i + 1; j < movesToTrue.size(); j++) {
                if (movesToTrue.get(j) != null) {
                    if (movesToTrue.get(j) < leastMoves) {
                        leastMoves = movesToTrue.get(j);
                        movesToTrue.set(i, null);
                        break;
                    } else if ( (movesToTrue.get(j) > leastMoves) || (movesToTrue.get(j) == Integer.MAX_VALUE) ) {
                        movesToTrue.set(j, null);
                    }
                }
            }
        }

        // Randomly select which path the bot should move to based on the number of moves
        // it takes to get from each adjacent cell to a possible leak cell
        int randomNum = (int) ( (Math.random() * 4) );
        while (movesToTrue.get(randomNum) == null) {
            randomNum = (int) ( (Math.random() * 4) );
        }

        // randomNum = 0 means bot moves to top cell
        if (randomNum == 0) {
            return pathToNearestTrue1;
        } else if (randomNum == 1) {
            // randomNum = 1 means bot moves to bottom cell
            return pathToNearestTrue2;
        } else if (randomNum == 2) {
            // randomNum = 2 means bot moves to left cell
            return pathToNearestTrue3;
        } else {
            // randomNum = 3 means bot moves to right cell
            return pathToNearestTrue4;
        }

    }
    

    // Run task for Bot1
    public static int run(int[][] ship, int[] botCoords, boolean[][] detectionSquare, int k) {
        int botR = botCoords[0];
        int botC = botCoords[1];
        int numActions = 1;
        while (ship[botR][botC] != LEAK) {
            Stack<Integer> pathToNearestTrue = Bot1.choosePath(ship, botCoords, detectionSquare);
            // Bot1 moves towards the nearest cell that possibly contains a leak 
            while ( !pathToNearestTrue.empty() ) {
                int botLocation = pathToNearestTrue.pop();
                botCoords = ShipTools.convertIndexToCoordinates(botLocation, ship.length);
                numActions++;
            }
            
            botR = botCoords[0];
            botC = botCoords[1];
            if (ship[botR][botC] == LEAK) {
                break;
            } else {
                detectionSquare[botR][botC] = false;
            }

            // If Bot1 did not find the leak, take a sense action
            detectionSquare = ShipTools.updateDetectionSquare(ship, k, botCoords, detectionSquare);
            numActions++;
        }
        return numActions;
    }
}
