package matrixer;

/**
 *
 * @author Selam Adın Esra
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Matrixer {

    // a-> A matrix (NXM), b-> B matrix(MXN), c-> AxB(NXN)
    // c stores the serial production
    // cParallel stores the parallel production
    // N is a variable input from user, M is given by assignment
    // R stands for Random
    // thread[N][N] will contain c's each sells individual calculations
    // isClosed is false by default, if inequality of c and cParallel is 
    // observed, threads will be stopped.
    public static int[][] a;
    public static int[][] b;
    public static int[][] c;
    public static int[][] cParallel;
    public static int N;
    public static int M = 16;
    // R stands for Random
    public static int R;
    public static HelperThread[][] thread;
    public static boolean isClosed = false;
    public static boolean isEqual = true;

    public static long startTimeSerial = System.nanoTime();
    public static long startTimeParallel = System.nanoTime();
    public static long startTimeChecking = System.nanoTime();
    public static long startTimeTotal = System.nanoTime();

    public static long endTimeSerial = System.nanoTime();
    public static long endTimeParallel = System.nanoTime();
    public static long endTimeChecking = System.nanoTime();
    public static long endTimeTotal = System.nanoTime();

    public static long totalTimeSerial = System.nanoTime();
    public static long totalTimeParallel = System.nanoTime();
    public static long totalTimeChecking = System.nanoTime();
    public static long totalTime = System.nanoTime();

    public static void threadKiller() throws InterruptedException, NullPointerException {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // It will quit the program
                thread[i][j].interrupt();
                //but you can use .stop() as well
                //https://10kloc.wordpress.com/2013/03/03/java-multithreading-steeplechase-stopping-threads/
                //according to this lecture
                // Also, using stop() may cause UnknownException Handler bug
                // https://bugs.openjdk.java.net/browse/JDK-8132548

            }
        }
    }

    public static boolean isMatrixEqual(int[][] cS, int[][] cP, int i, int j) {
        if (!isEqual || cS[i][j] != cP[i][j]) {
            isEqual = false;
            endTimeChecking = System.nanoTime();
            return false;
        } else {
            return true;
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException, NullPointerException {

        // Serial multiplication code from: 
        // https://github.com/SahilMadridista/Matrix_Multiplication_Java/blob/master/MatrixMulti.java
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the number of rows as N: ");
        if (!scan.hasNextInt()) {
            N = 16;
            //A passive aggresive information to user, who entered wrong kind of input.
            System.out.println("Ok, it is not an integer. I set the number of "
                    + "N to" + 16);
        } else {
            N = scan.nextInt();
        }

        Scanner scan2 = new Scanner(System.in);

        System.out.println("Enter the the range for the numbers which "
                + "will be provided in the matrix by Random Generator");

        if (!scan2.hasNextInt()) {
            R = 176;
            //A passive aggresive information to user, who entered wrong kind of input.
            System.out.println("Ok, it is not an integer. I set the number of "
                    + "R to" + 176);
        } else {
            R = scan2.nextInt();
        }

        startTimeTotal = System.nanoTime();

        a = new int[N][M];
        b = new int[M][N];
        c = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                //if the ASCII code is mandatory use the comment below:
                //a[i][j] = (int) (Math.random() * 177);

                a[i][j] = (int) (Math.random() * (R + 1));

            }
        }

        // Removing the file from directory if exists
        String currentDirectory = System.getProperty("user.dir");

        try {
            Files.deleteIfExists(Paths.get(currentDirectory + "\\A.txt"));
            Files.deleteIfExists(Paths.get(currentDirectory + "\\B.txt"));
            Files.deleteIfExists(Paths.get(currentDirectory + "\\CSerial.txt"));
            Files.deleteIfExists(Paths.get(currentDirectory + "\\CParallel.txt"));
        } catch (NoSuchFileException e) {
            System.out.println("No such file/directory exists");
        }
        System.out.println("Deletion of previous record is successful.");

        // File writing method from https://stackoverflow.com/questions/32235900/writing-array-to-txt-java
        try {
            FileWriter writer = new FileWriter("A.txt", false);
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {

                    writer.write(a[i][j] + " ");
                    //if you want to write an ASCII form of matrix it is
                    //writer.write((char)a[i][j]+" ");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();

            // B Matrix
        }
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                b[i][j] = (int) (Math.random() * (R + 1));
            }
        }
        try {
            FileWriter writer = new FileWriter("B.txt", true);
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b[i].length; j++) {
                    writer.write(b[i][j] + " ");
                    //if you want to write an ASCII form of matrix it is
                    //writer.write((char)b[i][j] + " ");

                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startTimeSerial = System.nanoTime();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < M; k++) {
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
            }
        }
        endTimeSerial = System.nanoTime();
        long totalTimeSerial = TimeUnit.NANOSECONDS.toMillis(endTimeSerial - startTimeSerial);
        System.out.println("Serial calculation took " + totalTimeSerial + "ms.");

        System.out.println("Product matrix from serial calculation: ");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(c[i][j] + " ");
            }
            System.out.println();
        }

        try {
            FileWriter writer = new FileWriter("CSerial.txt", true);
            for (int i = 0; i < c.length; i++) {
                for (int j = 0; j < c[i].length; j++) {
                    writer.write(c[i][j] + " ");
                    //if you want to write an ASCII form of matrix it is
                    //writer.write((char)c[i][j]+" ");

                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cParallel = new int[N][N];
        int M1 = N;

        thread = new HelperThread[N][N];

        startTimeParallel = System.nanoTime();
        startTimeChecking = System.nanoTime();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (isClosed) {
                    System.out.println("Stopping the threads...");
                } else {
                    thread[i][j] = new HelperThread(a, b, cParallel, i, j, M);
                    thread[i][j].start();

                }

            }
        }

        endTimeChecking = System.nanoTime();
        totalTimeChecking = TimeUnit.NANOSECONDS.toMillis(endTimeChecking - startTimeChecking);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M1; j++) {
                if (!isClosed) {
                    try {
                        thread[i][j].join();
                    } catch (InterruptedException e) {
                        e.getStackTrace();
                    }
                }
            }
        }

        endTimeParallel = System.nanoTime();
        totalTimeParallel = TimeUnit.NANOSECONDS.toMillis(endTimeParallel - startTimeParallel);

        System.out.println();

        if (!isClosed) {
            System.out.println("Product matrix from multithreaded calculation: ");
            //Displaying the cParallel
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    System.out.print(cParallel[i][j] + " ");
                }
                System.out.println();
            }
            try {
                FileWriter writer = new FileWriter("CParallel.txt", false);
                for (int i = 0; i < cParallel.length; i++) {
                    for (int j = 0; j < cParallel[i].length; j++) {
                        writer.write(cParallel[i][j] + " ");
                    }
                    writer.write("\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            endTimeTotal = System.nanoTime();
            long totalTime = TimeUnit.NANOSECONDS.toMillis(endTimeTotal - startTimeTotal);
            System.out.println("Successfully done!");
            System.out.println("isEqual's value is " + isEqual);
            System.out.println("Serial calculation took " + totalTimeSerial + "ms.");
            System.out.println("Parallel calculation including checking took " + totalTimeParallel + "ms.");
            System.out.println("Equality checking took " + totalTimeChecking + "ms");
            System.out.println("In total, the program including threads joining took " + totalTime + "ms.");
            System.out.println("It is expected parallel to take longer time than serial "
                    + "computation in this implementation, do not be surprised.");
        } else {
            System.out.println("Threads are interrupted.");
            System.out.println("isEqual's value " + isEqual);
            endTimeParallel = System.nanoTime();
            endTimeTotal = System.nanoTime();
            totalTimeParallel = TimeUnit.NANOSECONDS.toMillis(endTimeParallel - startTimeParallel);
            totalTimeChecking = TimeUnit.NANOSECONDS.toMillis(endTimeChecking - startTimeChecking);
            totalTime = TimeUnit.NANOSECONDS.toMillis(endTimeTotal - startTimeTotal);
            System.out.println("Serial calculation took " + totalTimeSerial + "ms.");
            System.out.println("Parallel calculation took " + totalTimeParallel + "ms.");
            System.out.println("Checking equality took " + totalTimeChecking + "ms.");
            System.out.println("Total operations including thread interruptions took " + totalTime + "ms.");

        }
    }
}
