package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> N = new AList<Integer>();
        N.addLast(100);
        N.addLast(1600);
        N.addLast(94000);
        N.addLast(28000000);
        AList<Double> times = new AList<Double>();
        for (int i = 0; i < N.size(); i++) {
            times.addLast(addNTime(N.get(i)));
        }
        printTimingTable(N,times,N);
    }
    public static double addNTime(int N){
        AList<Integer> product = new AList<Integer>();
        double startTime = System.nanoTime();
        for (int i = 0; i < N; i++) {
            product.addLast(i);
        }
        double endTime = System.nanoTime();
        return  ((endTime - startTime)/1000000000);
    }

}
