package matrixer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HelperThread extends Thread {

    private int[][] a;
    private int[][] b;
    private int[][] c;
    private int row, col;
    private int N;

    public HelperThread(int[][] a, int[][] b, int[][] c, int row, int col, int N) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.row = row;
        this.col = col;
        this.N = N;
    }

    public void run() {

        for (int i = 0; i < N; i++) {
            c[row][col] += a[row][i] * b[i][col];

        }

        if (!Matrixer.isMatrixEqual(Matrixer.c, Matrixer.cParallel, row, col)) {

            Matrixer.isClosed = true;
            try {
                Matrixer.threadKiller();

            } catch (InterruptedException ex) {
                Logger.getLogger(HelperThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
