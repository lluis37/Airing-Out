import java.util.ArrayList;


public class AiringOut {

    public static int[][] duplicateShip(int[][] ship) {
        int[][] newShip = new int[ship.length][ship.length];
        for (int r = 0; r < ship.length; r++) {
            for (int c = 0; c < ship.length; c++) {
                newShip[r][c] = ship[r][c];
            }
        }

        return newShip;
    }

    public static boolean[][] duplicateDetectionSquare(boolean[][] detectionSquare) {
        boolean[][] newDetectionSquare = new boolean[detectionSquare.length][detectionSquare.length];
        for (int r = 0; r < detectionSquare.length; r++) {
            for (int c = 0; c < detectionSquare.length; c++) {
                newDetectionSquare[r][c] = detectionSquare[r][c];
            }
        }

        return newDetectionSquare;
    }

    public static int[] duplicateBotCoords(int[] botCoords) {
        int[] newBotCoords = new int[botCoords.length];
        for (int i = 0; i < botCoords.length; i++) {
            newBotCoords[i] = botCoords[i];
        }

        return newBotCoords;
    }

     public static int[] duplicateLeakCoords(int[] leakCoords) {
        int[] newLeakCoords = new int[leakCoords.length];
        for (int i = 0; i < leakCoords.length; i++) {
            newLeakCoords[i] = leakCoords[i];
        }

        return newLeakCoords;
    }

    public static void main (String[] args) {
        int size = 30;
        int k = 10;
        double alpha = 2;

        // Keep track of the total number of actions each bot made over 100 tests
        int bot1Actions = 0;
        int bot2Actions = 0;
        int bot3Actions = 0;
        int bot4Actions = 0;
        int bot5Actions = 0;
        int bot6Actions = 0;
        int bot7Actions = 0;
        int bot8Actions = 0;
        int bot9Actions = 0;
        
        // Run 100 tests on each bot for the input k and alpha values
        for (int i = 1; i <= 100; i++) {
            int[][] ship1 = ShipTools.generateShip(size);
            ArrayList<Integer> openCells = ShipTools.getOpenCells(ship1, size);
            boolean[][] detectionSquare1 = ShipTools.generateDetectionSquare(size);
            
            // Make sure all bots begin at the same location on the ship
            int[] botCoords1 = ShipTools.placeBot(ship1, openCells, detectionSquare1, k);
            int[] botCoords2 = duplicateBotCoords(botCoords1);
            int[] botCoords3 = duplicateBotCoords(botCoords1);
            int[] botCoords4 = duplicateBotCoords(botCoords1);
            int[] botCoords5 = duplicateBotCoords(botCoords1);
            int[] botCoords6 = duplicateBotCoords(botCoords1);
            int[] botCoords7 = duplicateBotCoords(botCoords1);
            int[] botCoords8 = duplicateBotCoords(botCoords1);
            int[] botCoords9 = duplicateBotCoords(botCoords1);

            int[] leakCoords = ShipTools.placeLeak(ship1, openCells, detectionSquare1);
            // Create the remaining ships which have one leak
            int[][] ship2 = duplicateShip(ship1);
            int[][] ship3 = duplicateShip(ship1);
            int[][] ship4 = duplicateShip(ship1);

            // Create the ships which have two leaks
            int[][] ship5 = duplicateShip(ship1);
            int[] leak2Coords = ShipTools.placeLeak(ship5, openCells, detectionSquare1);
            int[][] ship6 = duplicateShip(ship5);
            int[][] ship7 = duplicateShip(ship5);
            int[][] ship8 = duplicateShip(ship5);
            int[][] ship9 = duplicateShip(ship5);

            // Create the remaining detection squares for each bot which 
            // uses a detection square
            boolean[][] detectionSquare2 = duplicateDetectionSquare(detectionSquare1);
            boolean[][] detectionSquare5 = duplicateDetectionSquare(detectionSquare1);
            boolean[][] detectionSquare6 = duplicateDetectionSquare(detectionSquare1);

            // Make copies of the leakCoords to use for bots 7 and 8 since those bots
            // modify the array values of leakCoords and leak2Coords
            int[] leakCoordsFor7 = duplicateLeakCoords(leakCoords);
            int[] leak2CoordsFor7 = duplicateLeakCoords(leak2Coords);
            int[] leakCoordsFor8 = duplicateLeakCoords(leakCoords);
            int[] leak2CoordsFor8 = duplicateLeakCoords(leak2Coords);
            int[] leakCoordsFor9 = duplicateLeakCoords(leakCoords);
            int[] leak2CoordsFor9 = duplicateLeakCoords(leak2Coords);

            // Run test on each bot
            bot1Actions += Bot1.run(ship1, botCoords1, detectionSquare1, k);
            bot2Actions += Bot2.run(ship2, botCoords2, detectionSquare2, k);
            bot3Actions += Bot3.run(ship3, botCoords3, leakCoords, alpha, openCells);
            bot4Actions += Bot4.run(ship4, botCoords4, leakCoords, alpha, openCells);
            bot5Actions += Bot5.run(ship5, botCoords5, detectionSquare5, k);
            bot6Actions += Bot6.run(ship6, botCoords6, detectionSquare6, k);
            bot7Actions += Bot7.run(ship7, botCoords7, leakCoordsFor7, leak2CoordsFor7, alpha, openCells);
            bot8Actions += Bot8.run(ship8, botCoords8, leakCoordsFor8, leak2CoordsFor8, alpha, openCells);
            bot9Actions += Bot9.run(ship9, botCoords9, leakCoordsFor9, leak2CoordsFor9, alpha, openCells);
        }

        // Print average number of moves for each bot
        System.out.println("Average number of moves for Bot1 = " + (bot1Actions / 100.0) );
        System.out.println("Average number of moves for Bot2 = " + (bot2Actions / 100.0) );
        System.out.println("Average number of moves for Bot3 = " + (bot3Actions / 100.0) );
        System.out.println("Average number of moves for Bot4 = " + (bot4Actions / 100.0) );
        System.out.println("Average number of moves for Bot5 = " + (bot5Actions / 100.0) );
        System.out.println("Average number of moves for Bot6 = " + (bot6Actions / 100.0) );
        System.out.println("Average number of moves for Bot7 = " + (bot7Actions / 100.0) );
        System.out.println("Average number of moves for Bot8 = " + (bot8Actions / 100.0) );
        System.out.println("Average number of moves for Bot9 = " + (bot9Actions / 100.0) );
    }
    
}
