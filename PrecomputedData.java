public class PrecomputedData {
    public int[] cardinalOffset = {8, -8, 1, -1, 7, -7, 9, -9};
    public int[] knightMovement = {-17, -15, -10, -6, 6, 10, 15, 17};
    public static int[][] numSquaresToEdge;

    static void computeMoveData() {
        numSquaresToEdge = new int[64][8];

        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {
                int north = 7 - row;
                int south = row;
                int east = 7 - file;
                int west = file;

                int[] squareDirections = {
                    north,
                    south,
                    east,
                    west,
                    Math.min(north, west),
                    Math.min(south, east),
                    Math.min(north, east),
                    Math.min(south, west)
                };
    
                numSquaresToEdge[file + row * 8] = squareDirections;
            }
        }
    }
}
