import java.util.List;
import java.util.Queue;

import javax.print.attribute.PrintRequestAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;


public class ShipTools {

    private static final int OPEN = 0;
    private static final int BOT = -1;
    private static final int LEAK = -5;

    public static int[][] generateShip(int size) {
        int[][] ship = new int[size][size];

        // ArrayList to keep track of cells which have one open neighbor
        ArrayList<Integer> oneNeighbor = new ArrayList<Integer>();
        
        // Hashtable to keep track of all cells that have been removed from ArrayList
        Hashtable<Integer, Character> visited = new Hashtable<Integer, Character>();

        // Enumerate the cells of the ship to make identification of cells with one open neighbor
        // easier
        int designator = 1;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                ship[r][c] = designator;
                designator++;
            }
        }

        //Select random interior cell to open and add its neighbors to ArrayList
        int randomR = (int) (Math.random() * (size - 3) + 1);
        int randomC = (int) (Math.random() * (size - 3) + 1);
        ship[randomR][randomC] = OPEN;
        // top neighbor
        oneNeighbor.add(ship[randomR-1][randomC]);
        // bottom neighbor
        oneNeighbor.add(ship[randomR+1][randomC]);
        // left neighbor
        oneNeighbor.add(ship[randomR][randomC-1]);
        // right neighbor
        oneNeighbor.add(ship[randomR][randomC+1]);

        // Make sure that any open cell is marked as visited
        visited.put(0, 'v');

        // Iteratively open blocked cells in the board
        while ( !oneNeighbor.isEmpty() ) {
            //printArray(ship);
            //System.out.println();
            int randomCell = (int) (Math.random() * oneNeighbor.size());
            int cellToOpen = oneNeighbor.get(randomCell);
            oneNeighbor.remove(randomCell);
            visited.put(cellToOpen, 'v');

            // search for the cellToOpen and open it
            for (int r = 0; r < size; r++) {
                int breakOut = 0;
                for (int c = 0; c < size; c++) {
                    if (ship[r][c] == cellToOpen) {
                        ship[r][c] = OPEN;

                        // add/remove top cell of newly opened cell to/from ArrayList 
                        if (r > 0) {
                            if ( oneNeighbor.contains(ship[r-1][c]) ) {
                                oneNeighbor.remove(oneNeighbor.indexOf(ship[r-1][c]));
                                visited.put(ship[r-1][c], 'v');
                            } else if ( !visited.containsKey(ship[r-1][c]) ) {
                                oneNeighbor.add(ship[r-1][c]);
                            }
                        }
                        // add/remove bottom cell of newly opened cell to/from ArrayList
                        if (r < (size - 1)) {
                            if ( oneNeighbor.contains(ship[r+1][c]) ) {
                                oneNeighbor.remove(oneNeighbor.indexOf(ship[r+1][c]));
                                visited.put(ship[r+1][c], 'v');
                            } else if ( !visited.containsKey(ship[r+1][c]) ) {
                                oneNeighbor.add(ship[r+1][c]);
                            }
                        }
                        // add/remove left cell of newly opened cell to/from ArrayList
                        if (c > 0) {
                            if ( oneNeighbor.contains(ship[r][c-1]) ) {
                                oneNeighbor.remove(oneNeighbor.indexOf(ship[r][c-1]));
                                visited.put(ship[r][c-1], 'v');
                            } else if ( !visited.containsKey(ship[r][c-1]) ) {
                                oneNeighbor.add(ship[r][c-1]);
                            }
                        }
                        // add/remove right cell of newly opened cell to/from ArrayList
                        if (c < (size - 1)) {
                            if ( oneNeighbor.contains(ship[r][c+1]) ) {
                                oneNeighbor.remove(oneNeighbor.indexOf(ship[r][c+1]));
                                visited.put(ship[r][c+1], 'v');
                            } else if ( !visited.containsKey(ship[r][c+1]) ){
                                 oneNeighbor.add(ship[r][c+1]);
                            }
                        }

                        breakOut = 1;
                        break;
                    }
                }

                if (breakOut == 1) {
                    break;
                }
            }
        }

        // Get all dead ends currently present in the ship
        ArrayList<Integer> deadEnds = new ArrayList<Integer>();
        // elementCounter helps to keep track of where the deadEnds exist in the ship (Array)
        int elementCounter = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int numOpenNeighbors = 0;
                if (ship[r][c] == OPEN) {
                    // check to see if top neighbor is open
                    if (r > 0) {
                        if (ship[r-1][c] == OPEN) {
                            numOpenNeighbors++;
                        }
                    }

                    // check to see if bottom neighbor is open
                    if (r < (size - 1)) {
                        if (ship[r+1][c] == OPEN) {
                            numOpenNeighbors++;
                        }
                    }

                    // check to see if left neighbor is open
                    if (c > 0) {
                        if (ship[r][c-1] == OPEN) {
                            numOpenNeighbors++;
                        }
                    }

                    // check to see if right neighbor is open
                    if (c < (size - 1)) {
                        if (ship[r][c+1] == OPEN) {
                            numOpenNeighbors++;
                        }
                    }

                    if (numOpenNeighbors == 1) {
                        deadEnds.add(elementCounter);
                    }
                }

                elementCounter++;
            }
        }

        // Open half of the dead ends in the ship
        int halfDeadEnds = deadEnds.size()/2;
        int numberEndsOpened = 0;
        ArrayList<Integer> blockedNeighbors = new ArrayList<Integer>();
        while (numberEndsOpened < halfDeadEnds) {
            int randomDeadEnd = (int) (Math.random() * deadEnds.size());
            int deadEndToUnblock = deadEnds.get(randomDeadEnd);
            elementCounter = 0;
            for (int r = 0; r < size; r++) {
                boolean breakout = false;
                for (int c = 0; c < size; c++) {
                    if (elementCounter == deadEndToUnblock) {
                        // now add blocked neighbors of dead end to ArrayList

                        // check if top neighbor is blocked
                        if (r > 0) {
                            if (ship[r-1][c] != OPEN) {
                                blockedNeighbors.add(ship[r-1][c]);
                            }
                        }

                        // check if bottom neighbor is blocked
                        if (r < (size - 1)) {
                            if (ship[r+1][c] != OPEN) {
                                blockedNeighbors.add(ship[r+1][c]);
                            }
                        }

                        // check if left neighbor is blocked
                        if (c > 0) {
                            if (ship[r][c-1] != OPEN) {
                                blockedNeighbors.add(ship[r][c-1]);
                            }
                        }

                        // check if right neighbor is blocked
                        if (c < (size - 1)) {
                            if (ship[r][c+1] != OPEN) {
                                blockedNeighbors.add(ship[r][c+1]);
                            }
                        }

                        // randomly choose blocked neighbor to open
                        int randomNeighbor = (int) (Math.random() * blockedNeighbors.size());
                        int neighborToOpen = blockedNeighbors.get(randomNeighbor);

                        // now find the blocked neighbor and open it

                        // check if top neighbor is neighborToOpen
                        if (r > 0) {
                            if (ship[r-1][c] == neighborToOpen) {
                                ship[r-1][c] = OPEN;
                            }
                        }

                        // check if bottom neighbor is neighborToOpen
                        if (r < (size - 1)) {
                            if (ship[r+1][c] == neighborToOpen) {
                                ship[r+1][c] = OPEN;
                            }
                        }

                        // check if left neighbor is neighborToOpen
                        if (c > 0) {
                            if (ship[r][c-1] == neighborToOpen) {
                                ship[r][c-1] = OPEN;
                            }
                        }
                        
                        // check if right neighbor is neighborToOpen
                        if (c < (size - 1)) {
                            if (ship[r][c+1] == neighborToOpen) {
                                ship[r][c+1] = OPEN;
                            }
                        }
                        deadEnds.remove(randomDeadEnd);
                        numberEndsOpened++;
                        blockedNeighbors.clear();

                        // now find all remaining deadEnds and remove any deadEnds in ArrayList which no longer exist
                        elementCounter = 0;
                        for (int row = 0; row < size; row++) {
                            for (int col = 0; col < size; col++) {
                                if (deadEnds.contains(elementCounter)) {
                                    // check if dead end is still a dead end
                                    int numOpenNeighbors = 0;

                                    // check to see if top neighbor is open
                                    if (row > 0) {
                                        if (ship[row-1][col] == OPEN) {
                                            numOpenNeighbors++;
                                        }
                                     }

                                    // check to see if bottom neighbor is open
                                    if (row < (size - 1)) {
                                        if (ship[row+1][col] == OPEN) {
                                            numOpenNeighbors++;
                                        }
                                    }

                                    // check to see if left neighbor is open
                                    if (col > 0) {
                                        if (ship[row][col-1] == OPEN) {
                                            numOpenNeighbors++;
                                        }
                                    }

                                    // check to see if right neighbor is open
                                    if (col < (size - 1)) {
                                        if (ship[row][col+1] == OPEN) {
                                         numOpenNeighbors++;
                                        }
                                    }

                                    // if numOpenNeighbors != 1, the dead end is no longer a dead end
                                    // so remove from ArrayList
                                    if (numOpenNeighbors != 1) {
                                        deadEnds.remove(deadEnds.indexOf(elementCounter));
                                        numberEndsOpened++;
                                    }
                                }

                                elementCounter++;
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

        
        return ship;
    }

    public static void copyIntArray(int[] a, int[] b) {
        if (a.length != b.length) {
            // Handle arrays of different lengths if needed
            System.err.println("Arrays have different lengths.");
        } else {
            for (int i = 0; i < a.length; i++) {
                a[i] = b[i];
            }
        }
    }
    
    // Method to find the distance from every cell on ship to startCoords
    public static int[][] generateDistanceMatrix(int[][] ship, int[] startCoords) {
        int size = ship.length;
        int[][] distanceMatrix = new int[size][size];
        int start = convertCoordinatesToIndex(startCoords, size);

        // Run BFS from every open cell / leak cell on the ship to the startCoords
        // to find the distance from every open cell / leak cell on the ship to startCoords
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if ( (ship[row][col] == OPEN) || (ship[row][col] == LEAK) ) {
                    // Keep track of parents so that you can build path to startLocation
                    HashMap<Integer, Integer> parent = new HashMap<Integer, Integer>();
                    Queue<Integer> fringe = new LinkedList<Integer>();
                    ArrayList<Integer> closedSet = new ArrayList<Integer>();

                    int[] destinationCoords = new int[2];
                    destinationCoords[0] = row;
                    destinationCoords[1] = col;
                    int destination = convertCoordinatesToIndex(destinationCoords, size);
                    int distance = 0;

                    // Perform BFS starting from startLocation
                    parent.put(start, start);
                    fringe.add(start);
                    closedSet.add(start);
                    while (fringe.peek() != null) {
                        int currentState = fringe.remove();

                        if (currentState == destination) {
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
                                            if ( ( (ship[r-1][c] == OPEN) || (ship[r-1][c] == LEAK) ) && !closedSet.contains(elementCounter-size) ) {
                                                fringe.add(elementCounter-size);
                                                parent.put(elementCounter-size, currentState);
                                                closedSet.add(elementCounter-size);
                                            }
                                        }

                                        // check to see if bottom neighbor is valid child
                                        if (r < (size - 1)) {
                                            if ( ( (ship[r+1][c] == OPEN) || (ship[r+1][c] == LEAK) ) && !closedSet.contains(elementCounter+size) ) {
                                                fringe.add(elementCounter+size);
                                                parent.put(elementCounter+size, currentState);
                                                closedSet.add(elementCounter+size);
                                            }
                                        }

                                        // check to see if left neighbor is valid child
                                        if (c > 0) {
                                            if ( ( (ship[r][c-1] == OPEN) || (ship[r][c-1] == LEAK) ) && !closedSet.contains(elementCounter-1) ) {
                                                fringe.add(elementCounter-1);
                                                parent.put(elementCounter-1, currentState);
                                                closedSet.add(elementCounter-1);
                                            }
                                        }

                                        // check to see if right neighbor is valid child
                                        if (c < (size - 1)) {
                                            if ( ( (ship[r][c+1] == OPEN) || (ship[r][c+1] == LEAK) ) && !closedSet.contains(elementCounter+1) ) {
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

                    // Traverse parent HashMap and find the number of moves (distance) to the destination
                    int traversalStart = destination;
                    while (traversalStart != start) {
                        traversalStart = parent.get(traversalStart);
                        distance++;
                    }
                    distanceMatrix[row][col] = distance;
                } else {
                    distanceMatrix[row][col] = 0;
                }
            }
        }
    
        return distanceMatrix;
    }
    

    //Method to scrub ship set the leak as leak, open to open and eveything else to 0
    public static int[][] scrubShip(int[][] ship, int[] destinationCoordinates){
        int[][] scrubbedShip = new int[ship.length][ship.length];
        for(int i = 0; i < ship.length; i++){
            for(int j = 0; j < ship.length; j++){
                if(ship[i][j] == OPEN){
                    scrubbedShip[i][j] = -2;
                }else{
                    scrubbedShip[i][j] = 0;
                }
            }
        }
        scrubbedShip[destinationCoordinates[0]][destinationCoordinates[1]] = -1;
        return scrubbedShip;
    }

    //Method to calculate the probability of a cell being the leak
    public static double calculateDenominator(int[] currentCellCoordinates, double[][] newProbabilityMatrix){
        double denominator = 0;
        for(int i =0; i< newProbabilityMatrix.length; i++){
            for(int j = 0; j < newProbabilityMatrix.length; j++){
                    denominator = denominator + newProbabilityMatrix[i][j];
            }
        }
        return denominator;
    }
    
    public static double[][] updateProbability(int[] currentCellCoordinates, double[][] oldProbabilityMatrix){

        double[][] newProbabilityMatrix = oldProbabilityMatrix.clone();
        newProbabilityMatrix[currentCellCoordinates[0]][currentCellCoordinates[1]] = 0;

        double denominator = calculateDenominator(currentCellCoordinates, newProbabilityMatrix);

        for(int i = 0; i < newProbabilityMatrix.length; i++){
            for(int j = 0; j < newProbabilityMatrix.length; j++){
                if(newProbabilityMatrix[i][j] != 0){
                    newProbabilityMatrix[i][j] = newProbabilityMatrix[i][j] / denominator;
                }else{
                    newProbabilityMatrix[i][j] = 0;
                }
            }
        }

        return newProbabilityMatrix;
    }


    public static ArrayList<Integer> getOpenCells(int[][] ship, int size) {
        // Find all open cells in the ship
        ArrayList<Integer> openCells = new ArrayList<Integer>();
        int elementCounter = 1;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (ship[r][c] == OPEN) {
                    openCells.add(elementCounter);
                }

                elementCounter++;
            }
        }
        return openCells;   
    } 



    // Method to convert the index number of a cell to a set of coordinates
    public static int[] convertIndexToCoordinates(int cellNum, int size){   
        int row = 0;
        int col = 0;
        
        while (cellNum > size) {
            cellNum -= size;
            row++;
        }

        col = cellNum - 1;
        
        int[] coords = new int[2];
        coords[0] = row;
        coords[1] = col;

        return coords;
    }


    //Method that finds the distance between two spot
    public static int calculateDistance(int[] spot1, int[] spot2){
        int distance = 0;
        int row1 = spot1[0];
        int col1 = spot1[1];
        int row2 = spot2[0];
        int col2 = spot2[1];

        distance = Math.abs(row1 - row2) + Math.abs(col1 - col2);

        return distance;
    }

    //Method to convert the coordinates of a cell to its index number
    public static int convertCoordinatesToIndex(int[] coords, int size){
        int cellNum = 0;
        int row = coords[0];
        int col = coords[1];

        cellNum = (row * size) + col + 1;

        return cellNum;
    }


    public static int[] placeLeak(int[][] ship, ArrayList<Integer> openCells, boolean[][] detectionSquare) {

        int randomCellIndex;
        int leakCellNum;
        int[] leakCoords;
        int leakR;
        int leakC;

        // Choose random cell for leak that is not in detectionSquare
        do {
            randomCellIndex = (int) (Math.random() * openCells.size());
            leakCellNum = openCells.get(randomCellIndex);
            leakCoords = convertIndexToCoordinates(leakCellNum, ship.length);
            leakR = leakCoords[0];
            leakC = leakCoords[1];
        } while (detectionSquare[leakR][leakC] == false);

        openCells.remove(randomCellIndex);
        ship[leakR][leakC] = LEAK;

        return leakCoords;
    }


    public static int[] placeBot(int[][] ship, ArrayList<Integer> openCells, boolean[][] detectionSquare, int k) {
        //DEBUG
        //System.out.println("placeBot");
        // Choose random cell for bot
        int randomCellIndex = (int) (Math.random() * openCells.size());
        int botCellNum = openCells.get(randomCellIndex);
        openCells.remove(randomCellIndex);
        int[] botCoords = convertIndexToCoordinates(botCellNum, ship.length);

        updateDetectionSquare(ship, k, botCoords, detectionSquare);
        return botCoords;
    }


    //Generate a detection square of specificed size
    public static boolean[][] generateDetectionSquare(int size) {
        // Initialize the detection sqaure
        boolean[][] detectionSquare = new boolean[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                detectionSquare[r][c] = true;
            }
        }
        return detectionSquare;    

    }

    //Just copies one arrays values and returns it
    public static boolean[][] copyArray(boolean[][] detectionSquare) {
        boolean[][] copy = new boolean[detectionSquare.length][detectionSquare.length];
        for(int i = 0; i < detectionSquare.length; i++){
            for(int j = 0; j < detectionSquare.length; j++){
                copy[i][j] = detectionSquare[i][j];
            }
        }
        return copy;
    
    }

    // Method to update the detection square for the bot
    public static boolean[][] updateDetectionSquare(int[][] ship, int k, int[] botCoords, boolean[][] detectionSquare) {
        //VisualizationTools.visualizeDetectionArray(detectionSquare);

        boolean[][] oldDetectionSquare = copyArray(detectionSquare);

        //DEBUG
        //System.out.println("Enter Detection Square");

        int row = botCoords[0];
        int col = botCoords[1];

        int detectorRowSize;
        int detectorColSize;
        int startingRow;
        int startingCol;
        
        //Will the square every be bigger than the ship if so then we have to check both left and right
        if(col - k < 0 && (col + k > (ship.length-1))){
            startingCol = 0;
            detectorColSize = ship.length;
        // Check if the detection square is out of bounds to the left
        }else if(col - k < 0){
            startingCol = 0;
            detectorColSize = ((2*k) + 1) + (col - k);

        // Check if detection sqaure is out of bounds to the right
        }else if(col + k > (ship.length-1)){
            startingCol = col - k;
            detectorColSize = ((2*k) + 1) - ((col + k) - (ship.length-1));
        }else{
            //set values to default
            startingCol = col - k;
            detectorColSize = (2*k) + 1;
        }


        if(row + k > (ship.length-1) && row - k < 0){
            startingRow = 0;
            detectorRowSize = ship.length;
        //Check the upper bounds           
        }else if(row - k < 0){
            startingRow = 0;
            detectorRowSize = ((2*k)+1) + (row - k);
        // check the lower bounds
        }else if(row + k > (ship.length-1)){
            startingRow = row - k;
            detectorRowSize = ((2*k) + 1) - ((row + k) - (ship.length-1));
        }else{
            //set values to default
            startingRow = row - k;
            detectorRowSize = (2*k) + 1;
        }

        //Hold wether we found a leak
        boolean isFound = false; 

        for(int i = 0; i < detectorRowSize; i++){
            for(int j = 0; j < detectorColSize; j++){

                if(ship[i+startingRow][j+startingCol] == LEAK){
                    //DEBUG
                    //System.out.println("LEAK FOUND IN SHIPTOOLS");
                    isFound = true;
                }else{
                    //DEBUG
                    //System.out.println("false");
                    detectionSquare[i+startingRow][j+startingCol] = false;
                }
                
            }
        }
        
        if(isFound == true){
            //DEBUG
            //VisualizationTools.visualizeDetectionArray(detectionSquare);

            //Set all detection square values to false
            for(int i = 0; i < detectionSquare.length; i++){
                for(int j = 0; j < detectionSquare.length; j++){
                    detectionSquare[i][j] = false;
                }
            }

            //Set dector square area to true in detection square
            for(int i = 0; i < detectorRowSize; i++){
                for(int j = 0; j < detectorColSize; j++){
                    if(oldDetectionSquare[i+startingRow][j+startingCol] != false){
                        detectionSquare[i+startingRow][j+startingCol] = true;
                    }
                }
            }
            //DEBUG
            //VisualizationTools.visualizeDetectionArray(detectionSquare);
        }


        return detectionSquare;

    }

    public static double[][] generateProbabilityMatrix(int[][] ship, ArrayList<Integer> openCells, int numLeaks) {
        int size = ship.length;
        int numOpenCells = openCells.size() + numLeaks;
        double initialProbability = (1.0 / numOpenCells);
        double[][] probabilities = new double[size][size];

        int elementCounter = 1;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if ( openCells.contains(elementCounter) || ship[r][c] == LEAK) {
                    probabilities[r][c] = initialProbability;
                } else {
                    probabilities[r][c] = 0;
                }

                elementCounter++;
            }
        }

        return probabilities;
    }

    // Update the probability that there is a leak in any given cell based on
    // whether or not you receieved a beep
    public static double[][] listenForBeep(int[][] ship, double[][] probabilities, int[][] distanceMatrix, double alpha, int[] leakCoords, int[] botCoords) {
        int size = probabilities.length;
        double[][] updatedProbabilities = new double[size][size];

        // Get distance from leak to bot
        int leakR = leakCoords[0];
        int leakC = leakCoords[1];
        int d = distanceMatrix[leakR][leakC];

        
        double beep = Math.pow(Math.E, (-alpha * (d - 1)) );
        double randomNum = Math.random();
        // Bot receives a beep
        if (randomNum < beep) {
            double sumProbabilities = sumProbabilitiesForBeep(ship, probabilities, distanceMatrix, alpha);
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if ( (ship[r][c] == OPEN) || (ship[r][c] == LEAK) ) {
                        double probLeakInJ = probabilities[r][c];
                        int distanceToBot = distanceMatrix[r][c];
                        double probBeepGivenLeak = Math.pow(Math.E, (-alpha * (distanceToBot - 1)) );
                        // Updated probabilites --> p(leak in (r,c) | beep in botLocation)
                        updatedProbabilities[r][c] = ( (probLeakInJ * probBeepGivenLeak) / sumProbabilities);
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

            probabilities = updateProbability(botCoords, probabilities);

            double sumProbabilities = sumProbabilitiesNoBeep(ship, probabilities, distanceMatrix, alpha);
            for(int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if ( (ship[r][c] == OPEN) || (ship[r][c] == LEAK) ) {
                        double probLeakInJ = probabilities[r][c];
                        int distanceToBot = distanceMatrix[r][c];
                        double probNoBeepGivenLeak = 1 - Math.pow(Math.E, (-alpha * (distanceToBot - 1)) );
                        // Updated probabilities --> p(leak in (r,c) | no beep in botLocation)
                        updatedProbabilities[r][c] = ( (probLeakInJ * probNoBeepGivenLeak) / sumProbabilities);
                    }
                }
            }
        }

        return updatedProbabilities;
    }

    // Method to calculate the sum of p(leak in (r, c)) * p(beep in botLocation | leak in (r,c))
    // over all possible values of (r,c)
    public static double sumProbabilitiesForBeep(int[][] ship, double[][] probabilities, int[][] distanceMatrix, double alpha) {
        double sumProbabilities = 0;

        int size = probabilities.length;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if ( (ship[r][c] == OPEN) || (ship[r][c] == LEAK) ) {
                    double probLeakInJ = probabilities[r][c];
                    int distanceToBot = distanceMatrix[r][c];
                    double probBeepGivenLeak = Math.pow(Math.E, (-alpha * (distanceToBot - 1)) );
                    sumProbabilities = sumProbabilities + (probLeakInJ * probBeepGivenLeak);
                }
            }
        }

        return sumProbabilities;
    }

    // Method to calculate the sum of p(leak in (r, c)) * p(beep not in botLocation | leak in (r,c))
    // over all possible values of (r,c)
    public static double sumProbabilitiesNoBeep(int[][] ship, double[][] probabilities, int[][] distanceMatrix, double alpha) {
        double sumProbabilities = 0;

        int size = probabilities.length;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if ( (ship[r][c] == OPEN) || (ship[r][c] == LEAK) ) {
                    double probLeakInJ = probabilities[r][c];
                    int distanceToBot = distanceMatrix[r][c];
                    double probNoBeepGivenLeak = 1 - Math.pow(Math.E, (-alpha * (distanceToBot - 1)) );
                    sumProbabilities = sumProbabilities + (probLeakInJ * probNoBeepGivenLeak);
                }
            }
        }

        return sumProbabilities;
    }
 
    public static void printDetect(boolean[][] detectionSquare) {
        for (int r = 0; r < detectionSquare.length; r++) {
            for (int c = 0; c < detectionSquare.length; c++) {
                System.out.print(detectionSquare[r][c] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printShip(int[][] ship) {
        for (int r = 0; r < ship.length; r++) {
            for (int c = 0; c < ship.length; c++) {
                System.out.print(ship[r][c] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printProbabilities(double[][] probabilities) {
        double sum = 0;
        for (int r = 0; r < probabilities.length; r++) {
            for (int c = 0; c < probabilities.length; c++) {
                sum += probabilities[r][c];
                System.out.print(String.format("%.5f", probabilities[r][c]) + "\t");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println(sum);
    }


}