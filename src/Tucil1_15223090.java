import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Tucil1_15223090 {

    private static int N, M, P;                 // Deklarasi N, M, dan P sebagai Integer
    private static String S;                    // Deklarasi DEFAULT = S sebagai String
    private static List<PuzzleBlock> blocks;    // List Array Daftar Blok Puzzle

    static class PuzzleBlock {
        char letter;                            // Huruf yang digunakan pada blok
        List<boolean[][]> transformations;      // List Array Transformasi Matriks Boolean

        PuzzleBlock(char letter) {
            this.letter = letter;
            this.transformations = new ArrayList<>();
        }
    }

    // Papan 
    private static char[][] board; 

    // Banyak Percobaan
    private static long iterationCount = 0; 

    // MAIN PROGRAM

    public static void main(String[] args) {
        while (true) {
            File inputFile = promptForFile();
            if (inputFile == null) {
                System.out.println(" File tidak valid atau tidak ditemukan. Mohon mencoba kembali input.");
                continue;
            }

            if (!parseInput(inputFile)) {
                System.out.println("Terjadi kesalahan format input. Mohon coba kembali.");
                continue;
            }
            else

            // Deklarasi board N sebagai kolom
            board = new char[N][M];
            for (int r = 0; r < N; r++) {
                Arrays.fill(board[r], '.');
            }

            // Melakukan Brute Force
            long startTime = System.currentTimeMillis();
            boolean solved = backtrack(0);
            long endTime = System.currentTimeMillis();

            long elapsed = endTime - startTime; 

            // Memberikan Hasil Algoritma
            if (solved) {
                printBoard();
            } else {
                System.out.print("Tidak ada solusi yang dapat ditemukan."); 
            }
            System.out.println("\n");
            System.out.println("Waktu pencarian: " + elapsed + "ms");
            System.out.println("\n");
            System.out.println("Banyak kasus yang ditinjau: " + iterationCount);
            System.out.println("\n");
            
            // Menanyakan apakah ingin menyimpan solusi dalam bentuk file?
            promptToSaveSolution(solved);

            System.out.println("Program selesai. Program dihentikan.");
        }
        
    }


    // Fungsi-Fungsi yang digunakan
    private static File promptForFile() {
        Scanner myReader = new Scanner(System.in);
        System.out.print("Masukkan path .txt: ");
        String path = myReader.nextLine().trim();
        File file = new File(path);
        // myReader.close() -- tidak diclose supaya bisa membaca input kembali.
        if (file.exists() && file.isFile()) {
            return file;
        }
        return null;
    }
    private static boolean parseInput(File inputFile) {
        blocks = new ArrayList<>();
        try (Scanner myReader = new Scanner(inputFile)) {
            // Membaca N, M, dan P
            if (!myReader.hasNextInt()) return false;
            N = myReader.nextInt();
            if (!myReader.hasNextInt()) return false;
            M = myReader.nextInt();
            if (!myReader.hasNextInt()) return false;
            P = myReader.nextInt();
            myReader.nextLine();

            // Membaca Type DEFAULT sebagai String
            if (!myReader.hasNextLine()) return false;

            S = myReader.nextLine().trim();
            if (!S.equals("DEFAULT")) {
                return false;
            }

            // Membaca Blok Puzzle.
            List<String> currentBlockLines = new ArrayList<>();
            Character currentLetter = null; 

            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                char firstChar = line.charAt(0);
                // Membaca Blok Puzzle
                if (currentLetter == null) {
                    currentLetter = firstChar;
                    currentBlockLines.add(line);
                } else if (firstChar == currentLetter) {
                    currentBlockLines.add(line);
                } else {
                    PuzzleBlock pb = convertBlockLinesToPuzzleBlock(currentLetter, currentBlockLines);
                    blocks.add(pb);

                    // Mulai Membaca Blok Puzzle Baru
                    currentBlockLines = new ArrayList<>();
                    currentLetter = firstChar;
                    currentBlockLines.add(line);

                    // Batasan P
                    if (blocks.size() == P) {
                        break;
                    }
                    
                    

                }
            }

            // Blok Terakhir
            if (!currentBlockLines.isEmpty() && currentLetter != null) {
                PuzzleBlock pb = convertBlockLinesToPuzzleBlock(currentLetter, currentBlockLines);
                blocks.add(pb);
            }
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Konversi Baris menjadi pb PuzzleBlock
    private static PuzzleBlock convertBlockLinesToPuzzleBlock(char letter, List<String> lines) {
        // Ukuran Matriks Boolean
        int rows = lines.size();
        int cols = 0;
        for (String s : lines) {
            cols = Math.max(cols, s.length());
        }

        // Matriks Boolean
        boolean[][] original = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            String s = lines.get(r);
            for (int c = 0; c < s.length(); c++) {
                if (s.charAt(c) == letter) {
                    original[r][c] = true;
                } else {
                    original[r][c] = false;
                }
            }
        }

        // Buat PuzzleBlock
        PuzzleBlock block = new PuzzleBlock(letter);

        // Transformasi matriks Boolean
        block.transformations = allTransformations(original);

        return block;
    }

    private static List<boolean[][]> allTransformations(boolean[][] original) {
        List<boolean[][]> list = new ArrayList<>();


        boolean[][] rot0 = original;
        boolean[][] rot90 = rotate(rot0);
        boolean[][] rot180 = rotate(rot90);
        boolean[][] rot270 = rotate(rot180);

        boolean[][] flip0 = flip(rot0);
        boolean[][] flip90 = flip(rot90);
        boolean[][] flip180 = flip(rot180);
        boolean[][] flip270 = flip(rot270);

        addIfNotExist(list, rot0);
        addIfNotExist(list, rot90);
        addIfNotExist(list, rot180);
        addIfNotExist(list, rot270);
        addIfNotExist(list, flip0);
        addIfNotExist(list, flip90);
        addIfNotExist(list, flip180);
        addIfNotExist(list, flip270);

        return list;
    }

    private static boolean[][] rotate(boolean[][] shape) {
        int h = shape.length;
        int w = shape[0].length;
        boolean[][] result = new boolean[w][h];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                result[c][h - 1 - r] = shape [r][c];
            }
        }
        return result;
    }

    private static boolean[][] flip(boolean[][] shape) {
        int h = shape.length;
        int w = shape[0].length;
        boolean[][] result = new boolean[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                result[r][w - 1 - c] = shape[r][c];
            }
        }
        return result;
    }

    private static void addIfNotExist(List<boolean[][]> list, boolean[][] matrix) {
        for (boolean[][] item : list) {
            if (isSame(item, matrix)) {
                return;
            }
        }
        list.add(matrix); 
    }

    private static boolean isSame(boolean[][] a, boolean[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) return false;
        for (int r = 0; r < a.length;r++) {
            for (int c = 0; c < a[0].length; c++) {
                if (a[r][c] != b[r][c]) return false;
            }
        }
        return true;
    }

    private static void printBoard() {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < M; c++) {
                System.out.print(board[r][c]);
            }
            System.out.println();
        }
    }

    // BRUTE FORCE ALGORITHM
    private static boolean backtrack(int blockIndex) {
        if (blockIndex == P) {
            return true;
        }

        PuzzleBlock block = blocks.get(blockIndex);

        for (boolean[][] shape : block.transformations) {

            for (int row = 0; row < N; row++) {
                for (int col = 0; col < M; col++) {
                    iterationCount++; 

                    if (canPlace(shape, row, col)) {

                        placeBlock(shape, row, col, block.letter);

                        if (backtrack(blockIndex + 1)) {
                            return true;
                        }

                        // Gagal, Lepas
                        removeBlock(shape, row, col);
                    }
                }
            }
        }

        // Jika semuanya gagal, return false, balik ke awal
        return false;
    }

    private static boolean canPlace(boolean[][] shape, int startRow, int startCol) {
        int h = shape.length;
        int w = shape[0].length;
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (shape[r][c]) {
                    int rr = startRow + r;
                    int cc = startCol + c;
                    // Cek Batas papan
                    if (rr < 0 || rr >= N || cc < 0 || cc >= M) {
                        return false;
                    }
                    // Cek Isi
                    if (board[rr][cc] != '.') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeBlock(boolean[][] shape, int startRow, int startCol, char blockLetter) {
        int h = shape.length;
        int w = shape[0].length;
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (shape[r][c]) {
                    board[startRow + r][startCol + c] = blockLetter;
                }
            }
        }
    }


    private static void removeBlock(boolean[][] shape, int startRow, int startCol) {
        int h = shape.length;
        int w = shape[0].length;
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (shape[r][c]) {
                    board[startRow + r][startCol + c] = '.';
                }
            }
        }
    }

    private static void promptToSaveSolution   (boolean solved) {
        Scanner myReaderInput = new Scanner(System.in);
        System.out.println("Apakah Anda ingin menyimpan solusi? (ya/tidak): ");
        String answer = myReaderInput.nextLine().trim().toLowerCase();

        if (answer.equals("ya")) {
            System.out.print("Masukkan nama file output: ");
            String fileOutput = myReaderInput.nextLine().trim();
            saveSolution(fileOutput, solved);
        }
        else if (answer.equals("tidak")) {
            System.out.print("Solusi tidak disimpan.");
        }
        myReaderInput.close();
    }

    private static void saveSolution(String filename, boolean solved) {
        try (FileWriter myWriter = new FileWriter(filename)) {
            if (!solved) {
                myWriter.write("Tidak ada solusi.");
            } else {
                for (int r = 0; r < N; r++) {
                    for (int c = 0; c < M; c++) {
                        myWriter.write(board[r][c]);
                    }
                    myWriter.write("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}