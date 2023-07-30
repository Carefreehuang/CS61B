package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> N = new AList<Integer>();
        AList<Integer> opc = new AList<Integer>();
        N.addLast(1000);
        N.addLast(4000);
        N.addLast(8000);
        N.addLast(160000);
        AList<Double> times = new AList<Double>();
        SLList<Integer> list1 = new SLList<Integer>();
        SLList<Integer> list2 = new SLList<Integer>();
        SLList<Integer> list3 = new SLList<Integer>();
        SLList<Integer> list4 = new SLList<Integer>();
        //创建不同大小的sllist
        for (int j = 0; j < N.get(0); j++) {
                list1.addLast(1);
        }
        for (int j = 0; j < N.get(1); j++) {
            list2.addLast(1);
        }
        for (int j = 0; j < N.get(2); j++) {
            list3.addLast(1);
        }
        for (int j = 0; j < N.get(3); j++) {
            list4.addLast(1);
        }
        opc.addLast(10000);
        opc.addLast(10000);
        opc.addLast(10000);
        opc.addLast(10000);
        times.addLast(getNTime(list1));
        times.addLast(getNTime(list2));
        times.addLast(getNTime(list3));
        times.addLast(getNTime(list4));
        printTimingTable(N,times,opc);
    }
    public static double getNTime(SLList<Integer> product){
        double startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            product.getLast();
        }
        double endTime = System.nanoTime();
        return  ((endTime - startTime)/1000000000);
    }
}
