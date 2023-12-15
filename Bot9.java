import java.util.*;

public class Bot9 {
    
    private static final int OPEN = 0;
    private static final int LEAK = -5;

    // Method to help add a random neighbor to the fringe in order to break ties
    // to the cell that has the highest probability of containing the leak at first
    // by distance from the bot, and then at random
    public static int randomNeighborToAdd(ArrayList<Integer> currentNeighbors) {
        int currNumNeighbors = currentNeighbors.size();
        int randomNum = (int) (Math.random() * currNumNeighbors);
        int neighborToAdd = currentNeighbors.get(randomNum);
        currentNeighbors.remove(randomNum);


        return neighborToAdd;
    }

    // Method to find the cell that has the highestProbability by performing BFS
    // and adding the children to the fringe in a random order
    public static double findHighestProbabilityCell(double[][] probabilities) {
        double highestProbability = 0;
        for (int r = 0; r < probabilities.length; r++) {
            for (int c = 0; c < probabilities.length; c++) {
                if (probabilities[r][c] > highestProbability) {
                    highestProbability = probabilities[r][c];
                }
            }
        }

        return highestProbability;
    }

    // Method to find the next move the bot has to make to move towards the cell that has
    // the highest probability of containing the leak
    public static Stack<int[]> findNextMoves(int[][] ship, double[][] probabilities, int[] startCoords) {
        // Keep track of parents so that you can build path to button
        HashMap<Integer, Integer> parent = new HashMap<Integer, Integer>();
        int size = ship.length;
        Queue<Integer> fringe = new LinkedList<Integer>();
        ArrayList<Integer> closedSet = new ArrayList<Integer>();
        int startLocation = ShipTools.convertCoordinatesToIndex(startCoords, size);

        double highestProbability = findHighestProbabilityCell(probabilities);
        // Keep track of the location of the highestProbability
        int highestProbabilityLocation = 0;

        // Perform BFS starting from startLocation
        parent.put(startLocation, startLocation);
        fringe.add(startLocation);
        closedSet.add(startLocation);
        while (fringe.peek() != null) {
            int currentState = fringe.remove();
            int[] currentCoords = ShipTools.convertIndexToCoordinates(currentState, size);
            int currentCoordsR = currentCoords[0];
            int currentCoordsC = currentCoords[1];

            // If a highest probability cell has been found, break
            if (probabilities[currentCoordsR][currentCoordsC] == highestProbability) {
                highestProbabilityLocation = currentState;
                break;
            } else {
                // find where currentState is on ship and add its valid children to fringe
                int elementCounter = 1;
                for (int r = 0; r < size; r++) {
                    boolean breakout = false;
                    for (int c = 0; c < size; c++) {
                        if (elementCounter == currentState) {
                            ArrayList<Integer> currentNeighbors = new ArrayList<Integer>();

                            // check to see if top neighbor is valid child
                            if (r > 0) {
                                if ( (ship[r-1][c] == OPEN || ship[r-1][c] == LEAK) && (!closedSet.contains(elementCounter-size)) ) {
                                    currentNeighbors.add(elementCounter-size);
                                }
                            }

                            // check to see if bottom neighbor is valid child
                            if (r < (size - 1)) {
                                if ( (ship[r+1][c] == OPEN || ship[r+1][c] == LEAK) && (!closedSet.contains(elementCounter+size)) ) {
                                    currentNeighbors.add(elementCounter+size);
                                }
                            }

                            // check to see if left neighbor is valid child
                            if (c > 0) {
                                if ( (ship[r][c-1] == OPEN || ship[r][c-1] == LEAK) && (!closedSet.contains(elementCounter-1)) ) {
                                    currentNeighbors.add(elementCounter-1);
                                }
                            }

                            // check to see if right neighbor is valid child
                            if (c < (size - 1)) {
                                if ( (ship[r][c+1] == OPEN || ship[r][c+1] == LEAK) && (!closedSet.contains(elementCounter+1)) ) {
                                    currentNeighbors.add(elementCounter+1);
                                }
                            }

                            // add neighbors randomly to fringe
                            while ( !currentNeighbors.isEmpty() ) {
                                int randomNeighbor = randomNeighborToAdd(currentNeighbors);

                                fringe.add(randomNeighbor);
                                parent.put(randomNeighbor, currentState);
                                closedSet.add(randomNeighbor);
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

        Stack<int[]> coordsToMoveTo = new Stack<int[]>();
        // Traverse parent HashMap and find the next move the bot needs to make
        if (parent.containsKey(highestProbabilityLocation)) {
            int traversalStart = highestProbabilityLocation;
            while (traversalStart != startLocation) {
                coordsToMoveTo.push( ShipTools.convertIndexToCoordinates(traversalStart, size) );
                traversalStart = parent.get(traversalStart);

            }
        }

        return coordsToMoveTo;
    }

    // Update the probability that there is a leak in any given cell based on
    // whether or not you receieved a beep (from one or two leaks)
    public static double[][] correctListenForBeep2(int[][] ship, double[][] probabilities, int[][] distanceMatrix, double alpha, int[] leakCoords, int[] leak2Coords, int[] botCoords) {
        int size = probabilities.length;
        double[][] updatedProbabilities = new double[size][size];

        // Get distance from each leak to bot
        int leakR = leakCoords[0];
        int leakC = leakCoords[1];
        int leak2R = leak2Coords[0];
        int leak2C = leak2Coords[1];
        double beep = 0;
        double beep2 = 0;

        // Check if first leak has not been found
        if ( (leakR != -10) && (leakC != -10) ) {
            int d = distanceMatrix[leakR][leakC];
            beep = Math.pow(Math.E, (-alpha * (d - 1)) );
        }
        // Check if second leak has not been found
        if ( (leak2R != -10) && (leak2C != -10) ) {
            int d2 = distanceMatrix[leak2R][leak2C];
            beep2 = Math.pow(Math.E, (-alpha * (d2 - 1)) );
        }

        double randomNum = Math.random();
        // Bot receives a beep from leak1 or leak2
        if ( (randomNum < beep) || (randomNum < beep2) ) {
            double sumProbabilities = sumProbabilities(ship, probabilities, distanceMatrix, alpha);
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if ( (ship[r][c] == OPEN) || (ship[r][c] == LEAK) ) {
                        double probLeakInJ = probabilities[r][c];
                        int distanceFromJToI = distanceMatrix[r][c];

                        // P(no beep in i from j | leak j) = 1 - P(beep in i from j | leak j)
                        double probBeepIFromJ = Math.pow(Math.E, (-alpha * (distanceFromJToI - 1)) );

                        // Updated probabilites --> p(leak in j | beep in botLocation)
                        updatedProbabilities[r][c] = ( (probLeakInJ * probBeepIFromJ)  / (1 - sumProbabilities) );
                    }
                }
            }
        } else {
            // Bot does not receive a beep

            // This means that the cells adjacent to the bot do not contain the leak,
            // so update their probabilities to be 0 and then update the remaining
            // remaining probabilities
            int botR = botCoords[0];
            int botC = botCoords[1];
            // Update top cell probability
            if (botR > 0) {
                probabilities[botR - 1][botC] = 0;
            }
            // Update bottom cell probability
            if ( botR < (probabilities.length - 1) ) {
                probabilities[botR + 1][botC] = 0;
            }
            // Update left cell probability
            if (botC > 0) {
                probabilities[botR][botC - 1] = 0;
            }
            // Update right cell probability
            if ( botC < (probabilities.length - 1) ) {
                probabilities[botR][botC + 1] = 0;
            }

            probabilities = ShipTools.updateProbability(botCoords, probabilities);

            double sumProbabilities = sumProbabilities(ship, probabilities, distanceMatrix, alpha);
            for(int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if ( (ship[r][c] == OPEN) || (ship[r][c] == LEAK) ) {
                        double probLeakInJ = probabilities[r][c];
                        int distanceToBot = distanceMatrix[r][c];
                        double probNoBeepIFromJ = 1 - Math.pow(Math.E, (-alpha * (distanceToBot - 1)) );
                        // Updated probabilities --> p(leak in j | no beep in botLocation)
                        updatedProbabilities[r][c] = ( (probLeakInJ * probNoBeepIFromJ) / sumProbabilities);
                    }
                }
            }
        }

        return updatedProbabilities;
    }

    // Method to calculate the sum over all values of j of the sum over all values of k of
    // P(leak j) * P(leak k) * [ (1 - P(beep i from j | leak j)) * (1 - P(beep i from k | leak k)) ]
    public static double sumProbabilities(int[][] ship, double[][] probabilities, int[][] distanceMatrix, double alpha) {
        double sumProbabilities = 0;

        int size = ship.length;
        int maxCellNum = size * size;
        for (int j = 1; j <= maxCellNum; j++) {
            // j represents the element number in the ship of a given cell, so convert it to coordinates
            // to get the row, col value of the cell
            int[] jCoords = ShipTools.convertIndexToCoordinates(j, size);
            double probLeakInJ = probabilities[jCoords[0]][jCoords[1]];
            int distanceFromJToI = distanceMatrix[jCoords[0]][jCoords[1]];
            double probNoBeepIFromJ = 1 - Math.pow(Math.E, (-alpha * (distanceFromJToI - 1)) );

            for (int k = j+1; k <= maxCellNum; k++) {
                // k represents the element number in the ship of a given cell, so convert it to coordinates
                // to get the row, col value of the cell
                int[] kCoords = ShipTools.convertIndexToCoordinates(k, size);
                
                double probLeakInK = probabilities[kCoords[0]][kCoords[1]];
                int distanceFromKToI = distanceMatrix[kCoords[0]][kCoords[1]];
                double probNoBeepIFromK = 1 - Math.pow(Math.E, (-alpha * (distanceFromKToI - 1)) );


                sumProbabilities = sumProbabilities + (probLeakInJ * probLeakInK * probNoBeepIFromJ * probNoBeepIFromK);
            }
        }

        return sumProbabilities;
    }

    // Run task for Bot9
    public static int run(int[][] ship, int[] botCoords, int[] leakCoords, int[] leak2Coords, double alpha, ArrayList<Integer> openCells) {
        double[][] probabilities = ShipTools.generateProbabilityMatrix(ship, openCells, 2);
        int[][] distanceMatrix = ShipTools.generateDistanceMatrix(ship, botCoords);
        int numActions = 0;
        int numLeaksFound = 0;

        while (true) {
            // Get the list of moves the bot has to make to get to the cell
            // with the highest probability of containing the leak
            Stack<int[]> listOfMoves = findNextMoves(ship, probabilities, botCoords);
            while ( !listOfMoves.isEmpty() ){
                botCoords = listOfMoves.pop();
                numActions++;

                int botR = botCoords[0];
                int botC = botCoords[1];
                if (ship[botR][botC] == LEAK) {
                    numLeaksFound++;

                    // Plug the leak
                    ship[botR][botC] = OPEN;
                    probabilities[botR][botC] = 0;
                    probabilities = ShipTools.updateProbability(botCoords, probabilities);

                    if ( (leakCoords[0] == botR) && (leakCoords[1] == botC) ) {
                        // Bot has found first leak so update the leak coordinates to -10
                        // to make sure the bot cannot hear a beep from this leak
                        leakCoords[0] = -10;
                        leakCoords[1] = -10;
                    } else if ( (leak2Coords[0] == botR) && (leak2Coords[1] == botC) ) {
                        // Bot has found second leak, so update leak coordinates to -10
                        // to make sure the bot cannot hear a beep from this leak
                        leak2Coords[0] = -10;
                        leak2Coords[1] = -10;
                    }

                    if (numLeaksFound == 2) {
                        break;
                    }
                } else {
                    // Update the probabilities along the way to the cell that has the highest
                    // probability of containing the leak
                    probabilities[botR][botC] = 0;
                    probabilities = ShipTools.updateProbability(botCoords, probabilities);
                }

            }
            
            if (numLeaksFound == 2) {
                break;
            } else {
                distanceMatrix = ShipTools.generateDistanceMatrix(ship, botCoords);
                
                // Bot 9 listens for a beep three times
                probabilities = correctListenForBeep2(ship, probabilities, distanceMatrix, alpha, leakCoords, leak2Coords, botCoords);
                probabilities = correctListenForBeep2(ship, probabilities, distanceMatrix, alpha, leakCoords, leak2Coords, botCoords);
                probabilities = correctListenForBeep2(ship, probabilities, distanceMatrix, alpha, leakCoords, leak2Coords, botCoords);

                numActions++;
                numActions++;
                numActions++;
            }

        }
        return numActions;
    }


}
