package hospital;

/**
 * Feature 2 requirement: represents the hospital ward using a
 * two-dimensional array laid out as 4 rows x 5 columns = 20 beds.
 *
 *   B01 B02 B03 B04 B05
 *   B06 B07 B08 B09 B10
 *   B11 B12 B13 B14 B15
 *   B16 B17 B18 B19 B20
 */
public class Ward {

    private static final int ROWS = 4;
    private static final int COLS = 5;
    private static final int TOTAL_BEDS = ROWS * COLS;

    private final String[][] bedLabels;   // e.g. "B01"
    private final boolean[][] occupied;   // true = bed taken
    private final String[][] occupantId;  // patientId sitting in that bed, or null

    public Ward() {
        bedLabels = new String[ROWS][COLS];
        occupied = new boolean[ROWS][COLS];
        occupantId = new String[ROWS][COLS];

        int bedNum = 1;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                bedLabels[r][c] = String.format("B%02d", bedNum);
                bedNum++;
            }
        }
    }

    /** Finds the [row, col] of a bed label, or null if it doesn't exist. */
    private int[] locate(String bedNumber) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedLabels[r][c].equalsIgnoreCase(bedNumber)) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public void allocateBed(String bedNumber, String patientId)
            throws InvalidBedException, BedOccupiedException, WardFullException {

        if (getAvailableBedCount() == 0) {
            throw new WardFullException("Cannot allocate: all 20 beds in the ward are occupied.");
        }
        int[] pos = locate(bedNumber);
        if (pos == null) {
            throw new InvalidBedException("Bed " + bedNumber + " does not exist. Valid beds are B01-B20.");
        }
        if (occupied[pos[0]][pos[1]]) {
            throw new BedOccupiedException("Bed " + bedNumber + " is already occupied.");
        }
        occupied[pos[0]][pos[1]] = true;
        occupantId[pos[0]][pos[1]] = patientId;
    }

    public void releaseBed(String bedNumber) throws InvalidBedException {
        int[] pos = locate(bedNumber);
        if (pos == null) {
            throw new InvalidBedException("Bed " + bedNumber + " does not exist.");
        }
        occupied[pos[0]][pos[1]] = false;
        occupantId[pos[0]][pos[1]] = null;
    }

    public int getAvailableBedCount() {
        int count = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (!occupied[r][c]) count++;
            }
        }
        return count;
    }

    public int getOccupiedBedCount() {
        return TOTAL_BEDS - getAvailableBedCount();
    }

    public double getOccupancyPercentage() {
        return (getOccupiedBedCount() / (double) TOTAL_BEDS) * 100.0;
    }

    public int getTotalBeds() {
        return TOTAL_BEDS;
    }

    /** Displays the full ward layout using nested loops. */
    public void displayWardLayout() {
        System.out.println("\n=== Ward Layout ([bed:O]=Occupied  [bed:.]=Available) ===");
        for (int r = 0; r < ROWS; r++) {
            StringBuilder row = new StringBuilder();
            for (int c = 0; c < COLS; c++) {
                row.append(String.format("[%s:%s] ", bedLabels[r][c], occupied[r][c] ? "O" : "."));
            }
            System.out.println(row);
        }
    }

    public void displayAvailableBeds() {
        System.out.print("Available Beds: ");
        boolean any = false;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (!occupied[r][c]) {
                    System.out.print(bedLabels[r][c] + " ");
                    any = true;
                }
            }
        }
        System.out.println(any ? "" : "None");
    }

    public void displayOccupiedBeds() {
        System.out.print("Occupied Beds: ");
        boolean any = false;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (occupied[r][c]) {
                    System.out.print(bedLabels[r][c] + "(" + occupantId[r][c] + ") ");
                    any = true;
                }
            }
        }
        System.out.println(any ? "" : "None");
    }
}
