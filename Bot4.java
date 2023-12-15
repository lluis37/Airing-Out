import java.util.*;


public class Bot4{


    
    private static final int OPEN = 0;
    private static final int BOT = -1;
    private static final int LEAK = -5;


    public static double calculateSurroundingProbabilities(double[][] probabilities, int botR, int botC){
        double sum = 0;
        if(botR - 1 > 0){
            sum += probabilities[botR - 1][botC];
        }
        if(botR + 1 < probabilities.length){
            sum += probabilities[botR + 1][botC];
        }
        if(botC - 1 > 0){
            sum += probabilities[botR][botC - 1];
        }
        if(botC + 1 < probabilities.length){
            sum += probabilities[botR][botC + 1];
        }
        return sum;
    }


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

    // Method to find the next set of moves the bot has to make to move 
    // to the cell that has the highest probability of containing the leak
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

    // Run task for Bot4
    public static int run(int[][] ship, int[] botCoords, int[] leakCoords, double alpha, ArrayList<Integer> openCells) {
        double[] thresholds = {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        double[][] probabilities = ShipTools.generateProbabilityMatrix(ship, openCells, 1);
        int[][] distanceMatrix = ShipTools.generateDistanceMatrix(ship, botCoords);
        int numActions = 0;

        while (true) {
            boolean leakFound = false;

            // Get the list of moves the bot has to make to get to the cell
            // with the highest probability of containing the leak
            Stack<int[]> listOfMoves = findNextMoves(ship, probabilities, botCoords);
            while ( !listOfMoves.isEmpty() ) {
                botCoords = listOfMoves.pop();
                numActions++;

                int botR = botCoords[0];
                int botC = botCoords[1];
                if (ship[botR][botC] == LEAK) {
                    leakFound = true;
                    break;
                } else {
                    // Update the probabilities along the way to the cell that has the highest
                    // probability of containing the leak
                    probabilities[botR][botC] = 0; 

                    double sum = calculateSurroundingProbabilities(probabilities, botR, botC);
                    int iterator = 0;

                    if(sum == 1){

                        probabilities = ShipTools.updateProbability(botCoords, probabilities);

                    }else if(sum >= .5){

                        while(iterator < 4){

                            probabilities = ShipTools.updateProbability(botCoords, probabilities);
                            iterator++;
                            numActions++;
                        }

                    }else{

                        probabilities = ShipTools.updateProbability(botCoords, probabilities);
                        
                    }
                    
                    
                }

            }
            
            if (leakFound) {
                break;
            } else {
                // Bot has not found the leak, so listen for a beep
                distanceMatrix = ShipTools.generateDistanceMatrix(ship, botCoords);
                probabilities = ShipTools.listenForBeep(ship, probabilities, distanceMatrix, alpha, leakCoords, botCoords);
                numActions++;
            }

        }
        return numActions;

    }


}








