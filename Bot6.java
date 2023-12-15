
import java.util.*;


public class Bot6{




    private static final int OPEN = 0;
    private static final int BOT = -1;
    private static final int LEAK = -5;

    private static boolean detectSensedLeak = false;

    // Method to update the detection square for bot5
    public static boolean[][] updateDetectionSquare2Leaks(int[][] ship, int k, int[] botCoords, boolean[][] detectionSquare, int numLeaksFound) {

        boolean[][] oldDetectionSquare = ShipTools.copyArray(detectionSquare);

        //DEBUG
        //System.out.println("Enter Detection Square");

        int row = botCoords[0];
        int col = botCoords[1];

        int detectorRowSize;
        int detectorColSize;
        int startingRow;
        int startingCol;
        //Will the square every be bigger than the ship if so then we have to check both left and right
        // Check if the detection square is out of bounds to the left
        if(col - k < 0){
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

        //Check the upper bounds
        if(row - k < 0){
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
                //check if spot has leak, if i does update all the squares 
                if(ship[i+startingRow][j+startingCol] == LEAK){
                    //DEBUG
                    //System.out.println("LEAK FOUND");
                    // System.out.printf("Leak found at %d, %d\n", i+startingRow, j+startingCol);
                    isFound = true;
                }else{
                    //DEBUG
                    //System.out.println("false");
                }
                
            }
        }

        if(isFound && (numLeaksFound == 0)){
            detectSensedLeak = true;
            return detectionSquare;

        }else if(isFound && (numLeaksFound == 1)){
            detectSensedLeak = true;
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
        } else {
            // Leak has not been found so set all cells in radius
            // of detectionSquare to be false
            for(int i = 0; i < detectorRowSize; i++){
                for(int j = 0; j < detectorColSize; j++){
                    detectionSquare[i+startingRow][j+startingCol] = false;
                }
            }
        }

        //DEBUG
        //VisualizationTools.visualizeDetectionArray(detectionSquare);
        return detectionSquare;

    }


    //Method will return a list of neighbors that are open or have a leak
    public static ArrayList<Integer[]> getNeighbors(int[] coordinates, int[][] ship){

        ArrayList<Integer[]> neighbors = new ArrayList<Integer[]>();
        int shipSize = ship.length;
        int row = coordinates[0];
        int col = coordinates[1];

        if(row > 0 && (ship[row-1][col] == OPEN || ship[row-1][col] == LEAK)){
            Integer[] coords = new Integer[2];
            coords[0] = row-1;
            coords[1] = col;
            neighbors.add(coords);
        }

        if(row < shipSize-1 && (ship[row+1][col] == OPEN || ship[row+1][col] == LEAK)){
            Integer[] coords = new Integer[2];
            coords[0] = row+1;
            coords[1] = col;
            neighbors.add(coords);
        }

        if(col > 0 && (ship[row][col-1] == OPEN || ship[row][col-1] == LEAK)){
            Integer[] coords = new Integer[2];
            coords[0] = row;
            coords[1] = col-1;
            neighbors.add(coords);
        }

        if(col < shipSize-1 && (ship[row][col+1] == OPEN || ship[row][col+1] == LEAK)){
            Integer[] coords = new Integer[2];
            coords[0] = row;
            coords[1] = col+1;
            neighbors.add(coords);

        }

        return neighbors;
        
    }

    //Method will take a hashmap of child : parents and return the list of moves to get to the finalCoords passed in
    public static ArrayList<int[]> chainOfMoves(HashMap<Integer, Integer> parentTable, int[] finalCoords, int size){
        ArrayList<int[]> chain = new ArrayList<int[]>();
        int currentNode = ShipTools.convertCoordinatesToIndex(finalCoords, size);
        chain.add(ShipTools.convertIndexToCoordinates(currentNode, size));
        while(currentNode != parentTable.get(currentNode)){
            currentNode = parentTable.get(currentNode);
            chain.add(ShipTools.convertIndexToCoordinates(currentNode, size));
        }
        return chain;

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

    //Think function sets the bounds of the detection square and returns the size of the square, in the order of rowSize, colSize, startingRow, startingCol
    public static int[] setDetectorStartingPointsAndSizes(int[][] ship, int k, int[] botCoords){
        
        int row = botCoords[0];
        int col = botCoords[1];

        int detectorRowSize;
        int detectorColSize;
        int startingRow;
        int startingCol;
        
        //Will the squares columns be bigger than the ship if so then we have to attach both left and right bounds
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

        //Will the hieght be bigger than the ship if so we have to check upper and lower bounds
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

        int[] answer = new int[4];
        answer[0] = detectorRowSize;
        answer[1] = detectorColSize;
        answer[2] = startingRow;
        answer[3] = startingCol;
        
        return answer;


    }


    // Method to update the detection square for the bot, this will turn all the newly sensed areas to false if there is no leak and to true if there was a loak and they were not previously false
    public static boolean[][] updateDetectionSquare(int[][] ship, int k, int[] botCoords, boolean[][] detectionSquare) {
        //VisualizationTools.visualizeDetectionArray(detectionSquare);

        boolean[][] oldDetectionSquare = copyArray(detectionSquare);

        //DEBUG
        //System.out.println("Enter Detection Square");

        //Think function sets the bounds of the detection square and returns the size of the square, in the order of rowSize, colSize, startingRow, startingCol
        int[] sizeSetter = setDetectorStartingPointsAndSizes(ship, k, botCoords);

        int detectorRowSize = sizeSetter[0];
        int detectorColSize = sizeSetter[1];
        int startingRow = sizeSetter[2];
        int startingCol = sizeSetter[3];


        //Hold wether we found a leak
        boolean isFound = false; 

        for(int i = 0; i < detectorRowSize; i++){
            for(int j = 0; j < detectorColSize; j++){

                if(ship[i+startingRow][j+startingCol] == LEAK){
                    //DEBUG
                    //System.out.println("LEAK FOUND IN BOT2 Update Detection");
                    isFound = true;

                    //Lets the bot know globally that there was a leak in the area
                    detectSensedLeak = true;
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

            //Set dector square area to true in detection square, and if the value was perviously false keep it false
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


    //Check if a list has another array in it
    public static boolean containsIntArray(List<int[]> list, int[] targetArray) {
        for (int[] array : list) {
            if (Arrays.equals(array, targetArray)) {
                return true; // Found a matching int[]
            }
        }
        return false; // No matching int[] found
    }

    public static ArrayList<int[]> bfs(int[][] ship, int[] botCoords, int[] destination){

        //Creates a queue
        Queue<Integer> fringe = new LinkedList<Integer>();
        //Creates a visited array to avoid visiting same node twice
        List<Integer> visited = new ArrayList<Integer>();
        //hashTable to holdParent And Child
        HashMap<Integer, Integer> parentTable = new HashMap<Integer, Integer>();
        //get our bots coordinates and converts it to index form, which is how to nodes are stored
        int shipLength = ship.length;
        //Stores it as a index value
        int botIndex = ShipTools.convertCoordinatesToIndex(botCoords, shipLength);
        //puts out head at the front of the queue
        fringe.add(botIndex);
        //putsNodeInParentQueue
        parentTable.put(botIndex, botIndex);
        

        while(fringe.size() != 0){
            //DEBUG
            //System.out.println("Searching..");

            //Take a node from the from the front of the queue
            int currentNode = fringe.remove();

            //gets its coordinates to check ship and detection square conditions
            int[] currentNodesCoords = ShipTools.convertIndexToCoordinates(currentNode, shipLength);
            int currentNodeRow = currentNodesCoords[0];
            int currentNodeCol = currentNodesCoords[1];
            //DEBUG
            //System.out.printf("Current Row %d, Current Col %d\n", currentNodeRow, currentNodeCol);

            
            if(currentNodeRow == destination[0] && currentNodeCol == destination[1]){
                return  chainOfMoves(parentTable, currentNodesCoords, ship.length);
            }
            
          
            //get nodes children
            ArrayList<Integer[]> neighbors = getNeighbors(currentNodesCoords, ship);
            //Collections.shuffle(neighbors);

            //DEBUG
            //System.out.printf("Current Element %d\n", currentNode);

            //DEBUG
            /*
            for (Integer[] arr : neighbors) {
                int row = arr[0];
                int col = arr[1];
                int[] childCoordsint = {row, col};
                int indexOfChild = ShipTools.convertCoordinatesToIndex(childCoordsint, shipLength);
                System.out.printf("Neighbor is %d\n", indexOfChild);
            }
            */

            for (Integer[] childCoordsInteger : neighbors) {

                    int row = childCoordsInteger[0];
                    int col = childCoordsInteger[1];
                    int[] childCoordsint = {row, col};
                    int indexOfChild = ShipTools.convertCoordinatesToIndex(childCoordsint, shipLength);

                //If Node has already been visited skip it
                if(visited.contains(indexOfChild) == false){
                    parentTable.put(indexOfChild, currentNode);
                    //Adds the node to the visitied map
                    visited.add(currentNode);
                    fringe.add(indexOfChild);

                    //DEBUG
                    //System.out.printf("Current Element child %d\n", indexOfChild);
                }
            }

            neighbors.clear();
            //DEBUG
            /* 
            System.out.printf("Neighbor after clear \n");
            for (Integer[] arr : neighbors) {
                int row = arr[0];
                int col = arr[1];
                int[] childCoordsint = {row, col};
                int indexOfChild = ShipTools.convertCoordinatesToIndex(childCoordsint, shipLength);
                System.out.printf("Neighbor is %d\n", indexOfChild);
            }
            */
            //DEBUG
            //System.out.printf("\n\n");

        }

        return null;
    }


    //Pass in bot Coords returns closest Potential Leak 
    public static ArrayList<int[]> findClosestPotentialLeak(int[][] ship, boolean[][] detectionSquare, int[] botCoords, ArrayList<int[]> previous_places){

        //Creates a queue
        Queue<Integer> fringe = new LinkedList<Integer>();
        //Creates a visited array to avoid visiting same node twice
        List<Integer> visited = new ArrayList<Integer>();
        //hashTable to holdParent And Child
        HashMap<Integer, Integer> parentTable = new HashMap<Integer, Integer>();
        //get our bots coordinates and converts it to index form, which is how to nodes are stored
        int shipLength = ship.length;
        //Stores it as a index value
        int botIndex = ShipTools.convertCoordinatesToIndex(botCoords, shipLength);
        //puts out head at the front of the queue
        fringe.add(botIndex);
        //putsNodeInParentQueue
        parentTable.put(botIndex, botIndex);
        

        while(fringe.size() != 0){
            //DEBUG
            //System.out.println("Searching..");

            //Take a node from the from the front of the queue
            int currentNode = fringe.remove();

            //gets its coordinates to check ship and detection square conditions
            int[] currentNodesCoords = ShipTools.convertIndexToCoordinates(currentNode, shipLength);
            int currentNodeRow = currentNodesCoords[0];
            int currentNodeCol = currentNodesCoords[1];
            //DEBUG
            //System.out.printf("Current Row %d, Current Col %d\n", currentNodeRow, currentNodeCol);

            
            //Check if the node is a potentialLeak 
            if(detectionSquare[currentNodeRow][currentNodeCol] == true && (containsIntArray(previous_places, currentNodesCoords) == false)){
                return  chainOfMoves(parentTable, currentNodesCoords, shipLength);
            }
            
          
            //get nodes children
            ArrayList<Integer[]> neighbors = getNeighbors(currentNodesCoords, ship);
            Collections.shuffle(neighbors);

            //DEBUG
            //System.out.printf("Current Element %d\n", currentNode);

            //DEBUG
            /*
            for (Integer[] arr : neighbors) {
                int row = arr[0];
                int col = arr[1];
                int[] childCoordsint = {row, col};
                int indexOfChild = ShipTools.convertCoordinatesToIndex(childCoordsint, shipLength);
                System.out.printf("Neighbor is %d\n", indexOfChild);
            }
            */

            for (Integer[] childCoordsInteger : neighbors) {

                    int row = childCoordsInteger[0];
                    int col = childCoordsInteger[1];
                    int[] childCoordsint = {row, col};
                    int indexOfChild = ShipTools.convertCoordinatesToIndex(childCoordsint, shipLength);

                //If Node has already been visited skip it
                if(visited.contains(indexOfChild) == false){
                    //Adds the node to the visitied map
                    visited.add(currentNode);
                    parentTable.put(indexOfChild, currentNode);
                    fringe.add(indexOfChild);

                    //DEBUG
                    //System.out.printf("Current Element child %d\n", indexOfChild);
                }
            }

            neighbors.clear();
            //DEBUG
            /* 
            System.out.printf("Neighbor after clear \n");
            for (Integer[] arr : neighbors) {
                int row = arr[0];
                int col = arr[1];
                int[] childCoordsint = {row, col};
                int indexOfChild = ShipTools.convertCoordinatesToIndex(childCoordsint, shipLength);
                System.out.printf("Neighbor is %d\n", indexOfChild);
            }
            */
            //DEBUG
            //System.out.printf("\n\n");

        }

        return null;
    }

    public static ArrayList<int[]> bestEfficentSpot(int cutoffHorizontal, int cutoffVertical, int[][]ship, int[]botCoords){
        int[] efficentSpot = new int[2];
        int row = botCoords[0];
        int col = botCoords[1];
        int cutoffHorizontalABS = Math.abs(cutoffHorizontal);
        int cutoffVerticalABS = Math.abs(cutoffVertical);
        int totalMovement = cutoffHorizontalABS + cutoffVerticalABS;
        int bestPathLength = 0;
        ArrayList<int[]> efficentSpotsPath = new ArrayList<int[]>();
        efficentSpotsPath.add(botCoords);

        if(cutoffHorizontal == 0 && cutoffVertical == 0){
            efficentSpotsPath.add(botCoords);
            return efficentSpotsPath;
        }
        //Can only move right
        if(cutoffHorizontal > 0 && cutoffVertical == 0){
            for(int j = 0; j <= cutoffHorizontalABS; j++){
                if(row < ship.length && j+col < ship.length){
                    if(ship[row][j+col] == OPEN || ship[row][j+col] == LEAK){
                        int[] dest = {row, j+col};
                        ArrayList<int[]> path = bfs(ship, botCoords, dest);
                        int totalSize = path.size()-1; 
                        if(totalSize <= totalMovement && totalSize >= bestPathLength){
                            efficentSpotsPath = path;
                            efficentSpot[0] = row;
                            efficentSpot[1] = j+col;
                            bestPathLength = totalSize;
                        }
                    }
                }
            }
            return efficentSpotsPath;

        }

        //can only move left
        if(cutoffHorizontal < 0 && cutoffVertical == 0){
            for(int j = 0; j <= cutoffHorizontalABS; j++){
                if(row < ship.length && col-j >= 0){
                    if(ship[row][col-j] == OPEN || ship[row][col-j] == LEAK){
                        int[] dest = {row, col-j};
                        ArrayList<int[]> path = bfs(ship, botCoords, dest);
                        int totalSize = path.size()-1; 
                        if(totalSize <= totalMovement && totalSize >= bestPathLength){
                            efficentSpotsPath = path;
                            efficentSpot[0] = row;
                            efficentSpot[1] = col-j;
                            bestPathLength = totalSize;
                        }
                    }
                }
            }
            return efficentSpotsPath;
        }

        //can only move up
        if(cutoffHorizontal == 0 && cutoffVertical < 0){
            for(int i = 0; i <= cutoffVerticalABS; i++){
                if(i-row >= 0 && col < ship.length){
                    if(ship[i-row][col] == OPEN || ship[i-row][col] == LEAK){
                        int[] dest = {i-row, col};
                        ArrayList<int[]> path = bfs(ship, botCoords, dest);
                        int totalSize = path.size()-1; 
                        if(totalSize <= totalMovement && totalSize >= bestPathLength){
                            efficentSpotsPath = path;
                            efficentSpot[0] = i-row;
                            efficentSpot[1] = col;
                            bestPathLength = totalSize;
                        }
                    }
                }
            }
            return efficentSpotsPath;
        }

        //can only move down
        if(cutoffHorizontal == 0 && cutoffVertical > 0){
            for(int i = 0; i <= cutoffVerticalABS; i++){
                if(i+row < ship.length && col < ship.length){
                    if(ship[i+row][col] == OPEN || ship[i+row][col] == LEAK){
                        int[] dest = {i+row, col};
                        ArrayList<int[]> path = bfs(ship, botCoords, dest);
                        int totalSize = path.size()-1; 
                        if(totalSize <= totalMovement && totalSize >= bestPathLength){
                            efficentSpotsPath = path;
                            efficentSpot[0] = i+row;
                            efficentSpot[1] = col;
                            bestPathLength = totalSize;
                        }
                    }
                }
            }
            return efficentSpotsPath;
        }

        //can move right and down
        if(cutoffHorizontal >= 0 && cutoffVertical >= 0){
            for(int i = 0; i <= cutoffVerticalABS; i++){
                for(int j = 0; j <= cutoffHorizontalABS; j++){
                    if(i+row < ship.length && j+col < ship.length){
                        if(ship[i+row][j+col] == OPEN || ship[i+row][j+col] == LEAK){
                            int[] dest = {i+row, j+col};
                            ArrayList<int[]> path = bfs(ship, botCoords, dest);
                            int totalSize = path.size()-1; 
                            if(totalSize <= totalMovement && totalSize >= bestPathLength){
                                efficentSpotsPath = path;
                                efficentSpot[0] = i+row;
                                efficentSpot[1] = j+col;
                                bestPathLength = totalSize;
                            }
                        }
                    }
                }

            }
            return efficentSpotsPath;
        //Can move right and up
        }else if(cutoffHorizontal >= 0 && cutoffVertical <= 0){
            for(int i = 0; i <= cutoffVerticalABS; i++){
                for(int j = 0; j <= cutoffHorizontalABS; j++){
                    if(i-row >= 0 && j+col < ship.length){
                        if(ship[i-row][j+col] == OPEN || ship[i-row][j+col] == LEAK){
                            int[] dest = {i-row, j+col};
                            ArrayList<int[]> path = bfs(ship, botCoords, dest);
                            int totalSize = path.size()-1; 
                            if(totalSize <= totalMovement && totalSize >= bestPathLength){
                                efficentSpotsPath = path;
                                efficentSpot[0] = i-row;
                                efficentSpot[1] = j+col;
                                bestPathLength = totalSize;
                            }
                        }
                    }
                }

            }
            return efficentSpotsPath;
        //can move left and down
        }else if(cutoffHorizontal <= 0 && cutoffVertical >= 0){
            for(int i = 0; i <= cutoffVerticalABS; i++){
                for(int j = 0; j <= cutoffHorizontalABS; j++){
                    if(i+row < ship.length && j-col >= 0){
                        if(ship[i+row][j-col] == OPEN || ship[i+row][j-col] == LEAK){
                            int[] dest = {i+row, j-col};
                            ArrayList<int[]> path = bfs(ship, botCoords, dest);
                            int totalSize = path.size()-1; 
                            if(totalSize <= totalMovement && totalSize >= bestPathLength){
                                efficentSpotsPath = path;
                                efficentSpot[0] = i+row;
                                efficentSpot[1] = j-col;
                                bestPathLength = totalSize;
                            }
                        }
                    }
                }

            }
            return efficentSpotsPath;
        //can move left and up
        }else if(cutoffHorizontal <= 0 && cutoffVertical <= 0){
            for(int i = 0; i <= cutoffVerticalABS; i++){
                for(int j = 0; j <= cutoffHorizontalABS; j++){
                    if(i-row >= 0 && j-col >= 0){
                        if(ship[i-row][j-col] == OPEN || ship[i-row][j-col] == LEAK){
                            int[] dest = {i-row, j-col};
                            ArrayList<int[]> path = bfs(ship, botCoords, dest);
                            int totalSize = path.size()-1; 
                            if(totalSize <= totalMovement && totalSize >= bestPathLength){
                                efficentSpotsPath = path;
                                efficentSpot[0] = i-row;
                                efficentSpot[1] = j-col;
                                bestPathLength = totalSize;
                            }
                        }
                    }
                }

            }
            return efficentSpotsPath;
        }
        return efficentSpotsPath;
    
    }

    //Will find how much overlap the detection square has and more to a poistion that will minimize the amount of overlap
    public static ArrayList<int[]> efficentSpot(int k, int[]botCoords, boolean[][]detectionSquare, int[][]ship){

        //get the bots coordinates
        int col = botCoords[1];
        int row = botCoords[0];

        //Initialize the starting points
        int colStartRight;
        int colStartLeft;
        int rowStartUp;
        int rowStartDown;

        //set the starting points for the right half of detection square inspection
        if(col + k > (ship.length-1)){
            colStartRight = ship.length-1;
        }else{
            colStartRight = col + k;
        }

        //set the starting points for the left half of detection square inspection
        if(col - k < 0){
            colStartLeft = 0;
        }else{
            colStartLeft = col - k;
        }

        //set the starting points for the upper half of detection square inspection
        if(row - k < 0){
            rowStartUp = 0;
        }else{
            rowStartUp = row - k;
        }

        //set the starting points for the lower half of detection square inspection
        if(row + k > (ship.length-1)){
            rowStartDown = ship.length-1;
        }else{
            rowStartDown = row + k;
        }

        //Find the starting and ending I value for the right half of the detection square
        int startingRowValue;
        int endingRowValue;

        if(row - k < 0){
            startingRowValue = 0;
        }else{
            startingRowValue = row - k;
        }

        if(row + k > (ship.length-1)){
            endingRowValue = ship.length-1;
        }else{
            endingRowValue = row + k;
        }

        //initialize the colCutOffValues which is the amount that we can move to the left or right because the detecor has a column of all false in it
        int colCutoffValueRight = 0;
        int colCutoffValueLeft = 0;

        boolean breakAgain = false;
        //set the colCutOffValueRight
        int j;
        for(j = colStartRight; j > col; j--){
            for(int i = startingRowValue; i <= endingRowValue; i++){
                if(detectionSquare[i][j] == true){
                    colCutoffValueRight = (col+k) - j;
                    breakAgain = true;
                    break;
                }
            }
            if(breakAgain == true){
                break;
            }
            colCutoffValueRight = (col+k) - (j);
        }
        colCutoffValueRight = (colStartRight) - j;

        //DEBUG
        /* 
        System.out.printf("colStartRight %d\n", colStartRight);
        System.out.printf(" startingRowValue %d\n", startingRowValue);
        */

        //DEBUG
        /*
        System.out.printf("colCutoffValueRight %d\n", colCutoffValueRight);
        */

        breakAgain = false;
        //set the colCutOffValueLeft
        for(j = colStartLeft; j < col; j++){
            for(int i = startingRowValue; i <= endingRowValue; i++){
                if(detectionSquare[i][j] == true){
                    breakAgain = true;
                    break;
                }
            }
            if(breakAgain == true){
                break;
            }
        }
        colCutoffValueLeft = (j) - (colStartLeft);


        //DEBUG
        /*
        System.out.printf("colCutoffValueLeft %d\n", colCutoffValueLeft);
        */

        //Find the starting and ending J value for the upper half of the detection square
        int startingColValue;
        int endingColValue;

        if(col - k < 0){
            startingColValue = 0;
        }else{
            startingColValue = col - k;
        }

        if(col + k > (ship.length-1)){
            endingColValue = ship.length-1;
        }else{
            endingColValue = col + k;
        }

        //initialize the rowCutOffValues which is the amount that we can move to the up or down because the detecor has a row of all false in it
        int rowCutoffValueUp = 0;
        int rowCutoffValueDown = 0;

        breakAgain = false;
        //set the rowCutOffValueUp
        int i;
        for(i = rowStartUp; i < row; i++){
            for( j = startingColValue; j <= endingColValue; j++){
                if(detectionSquare[i][j] == true){
                    breakAgain = true;
                    break;
                }
            }
            if(breakAgain == true){
                break;
            }
        }
        rowCutoffValueUp = (i) - (rowStartUp);

        //DEBUG
        /* 
        System.out.printf("rowCutoffValueUp %d\n", rowCutoffValueUp);
        */

        breakAgain = false;
        //set the rowCutOffValueDown
        for(i = rowStartDown; i > row; i--){
            for(j = startingColValue; j <= endingColValue; j++){
                if(detectionSquare[i][j] == true){
                    rowCutoffValueDown = (row+k) - (i);
                    breakAgain = true;
                    break;
                }
            }
            if(breakAgain == true){
                break;
            }
            rowCutoffValueDown = (rowStartDown) - (i);
        }

        //DEBUG
        /*
        System.out.printf("rowCutoffValueDown %d\n", rowCutoffValueDown);
        */


        //factorByWhichToMove
        int factorByWhichToMoveHorizontal;
        int factorByWhichToMoveVertical;

        //check if the right side of square has more overlap than the right if so subtract that much from position
        if(colCutoffValueRight >= colCutoffValueLeft){

            //check if the adjusted amount will take you out of bounds
            if(col - colCutoffValueRight < 0){
                factorByWhichToMoveHorizontal = (col) * -1;
            }else{
                factorByWhichToMoveHorizontal = colCutoffValueRight * -1;
            }

        }else{
            //check if the adjusted amount will take you out of bounds
            if(col + colCutoffValueLeft > (ship.length-1)){
                factorByWhichToMoveHorizontal = ((ship.length-1) - col);
            }else{
            factorByWhichToMoveHorizontal = colCutoffValueLeft;
            }
        }   
        //debug
        /*
        System.out.printf("factorByWhichToMoveHorizontal %d\n", factorByWhichToMoveHorizontal);
        */

        //check if the upper side is more efficent
        if(rowCutoffValueUp > rowCutoffValueDown){
            //check if the adjusted amount will take you out of bounds
            if(row - rowCutoffValueUp < 0){
                factorByWhichToMoveVertical = (row) * -1;
            }else{
                factorByWhichToMoveVertical = rowCutoffValueUp * -1;
            }

        }else{

            //check if the adjusted amount will take you out of bounds
            if(row + rowCutoffValueDown > (ship.length-1)){
                factorByWhichToMoveVertical = ((ship.length-1) - row);
            }else{
                factorByWhichToMoveVertical = rowCutoffValueDown;
            }
        }
    
        
        //Find the most efficent spot to move to
        int[] efficentSpot = new int[2];

        //debug
        /*
        System.out.printf("factorByWhichToMoveVertical %d\n", factorByWhichToMoveVertical);
        */

        //check if the vertical is more efficent
        int possibleEfficentSpotRow = row;
        int possibleEfficentSpotCol = col;
        efficentSpot[0] = possibleEfficentSpotRow;
        efficentSpot[1] = possibleEfficentSpotCol;
        ArrayList<int[]> bestEfficentSpotPath = bestEfficentSpot(factorByWhichToMoveHorizontal, factorByWhichToMoveVertical, ship, botCoords);
        return bestEfficentSpotPath;

    }


    public static int run(int[][]ship, int[]botCoords, boolean[][]detectionSquare, int k){
        int leaksFound = 0;
        int size = ship.length;

        int leakElementNumber = -1;

        int moves = 0;
        int scans = 0;

        //DEBUG     
        // VisualizationTools.visualizeShip(ship);

        boolean locationOfLeakFound = false;

        ArrayList<int[]> previous_places = new ArrayList<int[]>();


        while (locationOfLeakFound == false) {


                //Find Closest Potential Leak
                ArrayList<int[]> pathToPotentialLeak = findClosestPotentialLeak(ship, detectionSquare, botCoords, new ArrayList<int[]>());

                //Closest Potential Leak
                int[] potentialLeakCoords = pathToPotentialLeak.get(0);
                
                
                //Possible more efficent spot
                ArrayList<int[]> pathToEfficentSpot = efficentSpot(k, potentialLeakCoords, detectionSquare, ship);
                
                //DEBUG
                /*
                System.out.printf("closest Leak %d, %d\n", potentialLeakCoords[0], potentialLeakCoords[1]);
                System.out.printf("efficentSpot %d, %d\n", efficentSpot[0], efficentSpot[1]);
                */

                //update botCoordsxs
                int[] efficentSpot = pathToEfficentSpot.get(0);

                //path from bot to efficent spot
                pathToEfficentSpot = bfs(ship, botCoords, efficentSpot);

                int distToEfficentSpot = pathToEfficentSpot.size()-1;

                //Move bot to the effiecent spot
                botCoords = efficentSpot;

                //update moves
                moves = moves + distToEfficentSpot;

                //detect at current Location
                detectionSquare = updateDetectionSquare2Leaks(ship, k, botCoords, detectionSquare, leaksFound);


                //new scan so update scan
                scans+=1;


                //DEBUG
                 
                //If where we are standing is true i means that there is a leak near us in the detection square
                while(detectSensedLeak == true){
                    

                    boolean[][] detectionSquare2 = AiringOut.duplicateDetectionSquare(detectionSquare);

                    detectionSquare2 = updateDetectionSquare(ship, k, botCoords, detectionSquare2);
                    
                    int []coords = {botCoords[0], botCoords[1]};

                    previous_places.add(coords);

                    if(ship[botCoords[0]][botCoords[1]] == LEAK){

                        detectSensedLeak = false;
                        ship[botCoords[0]][botCoords[1]] = OPEN;
                        detectionSquare[botCoords[0]][botCoords[1]] = false;
                        leakElementNumber = ShipTools.convertCoordinatesToIndex(botCoords, ship.length);
                       // System.out.printf("Leak %d is %d, %d\n", leaksFound, botCoords[0], botCoords[1]);
                        leaksFound++;
                        previous_places = new ArrayList<int[]>();
                        if (leaksFound == 2){
                            locationOfLeakFound = true;
                        }
                        break;

                    }
                    

                    //Find Closest Potential Leak
                    pathToPotentialLeak = findClosestPotentialLeak(ship, detectionSquare2, botCoords, previous_places);

                    //Closest Potential Leak
                    potentialLeakCoords = pathToPotentialLeak.get(0);
                   

                    //updateMoves amount
                    moves = moves + (pathToPotentialLeak.size()-1);

                    //update botCoordsxs
                    botCoords = potentialLeakCoords;

        
                    //detect at current Location
                    detectionSquare2 = updateDetectionSquare(ship, k, botCoords, detectionSquare2);
                    
                    //DEBUG
                    /*
                    VisualizationTools.visualizeDetectionArray(detectionSquare);
                    VisualizationTools.printBooleanArray(detectionSquare);
                    */
                    //new scan so update scan                    
                    scans+=1;

                }

        }
        int[] leakCoords = ShipTools.convertIndexToCoordinates(leakElementNumber, size);
        //DEBUG
        //System.out.printf("Leak Location by detector is %d, %d\n", leakCoords[0], leakCoords[1]);
        int amountOfActions = moves+scans;
        
        //DEBUG
        // System.out.printf("Bot 6 Amount of actions is %d\n", amountOfActions);


        return amountOfActions;


        //DEBUG
        //VisualizationTools.visualizeShipElements(ship);

        //DEBUG 
        //VisualizationTools.visualizeShip(ship);

        
    }
}

    
