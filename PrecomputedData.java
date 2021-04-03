public class PrecomputedData {
    // N, S, E, W, NW, SE, NE, SW
    public int[] cardinalOffset = { -8, 8, 1, -1, -9, 9, -7, 7 };

    // SWW, NWW, SSW, NNW, SSE, NNE, SEE, NEE
    public int[] knightMovement = { -10, 6, -17, 15, -15, 17, -6, 10 };
    public static int[][] numSquaresToEdge;

    static void computeMoveData() {
        numSquaresToEdge = new int[64][8];

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                int north = rank;
                int south = 7 - rank;
                int east = 7 - file;
                int west = file;

                int[] squareDirections = { north, south, east, west, Math.min(north, west), Math.min(south, east),
                        Math.min(north, east), Math.min(south, west) };

                numSquaresToEdge[file + rank * 8] = squareDirections;
            }
        }
    }
}
