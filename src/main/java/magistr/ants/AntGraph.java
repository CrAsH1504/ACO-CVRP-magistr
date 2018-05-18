package magistr.ants;

import java.io.*;

public class AntGraph implements Serializable {
    private double[][] delta;
    private double[][] tau;
    private int[] demand;
    private int numNodes;
    private double tau0;

    public AntGraph(int nNodes, double[][] delta, double[][] tau, int[] demand) {
        if (delta.length != nNodes)
            throw new IllegalArgumentException("The number of nodes doesn't match with the dimension of delta matrix");
        if (demand.length != nNodes)
            throw new IllegalArgumentException("Every node must have it's demand (demand for staring node is 0)");

        numNodes = nNodes;
        this.delta = delta;
        this.tau = tau;
        this.demand = demand;
    }

    public AntGraph(int nodes, double[][] delta, int[] demand) {
        this(nodes, delta, new double[nodes][nodes], demand);

        resetTau();
    }

    public synchronized double delta(int r, int s) {
        return delta[r][s];
    }

    public synchronized double tau(int r, int s) {
        return tau[r][s];
    }

    public synchronized double etha(int r, int s) {
        return ((double) 1) / delta(r, s);
    }

    public synchronized int nodes() {
        return numNodes;
    }

    public synchronized int demand(int node) {
        return demand[node];
    }

    public synchronized void updateTau(int r, int s, double value) {
        tau[r][s] = value;
    }

    public void resetTau() {
        double dAverage = averageDelta();
        tau0 = (double) 1 / ((double) numNodes * (0.5 * dAverage));
        System.out.println("Average: " + dAverage);
        System.out.println("Tau0: " + tau0);
        for (int r = 0; r < nodes(); r++) {
            for (int s = 0; s < nodes(); s++) {
                tau[r][s] = tau0;
            }
        }
    }

    public double averageDelta() {
        return average(delta);
    }

    public String toString() {
        String str = "";
        String str1 = "";
        for (int r = 0; r < nodes(); r++) {
            for (int s = 0; s < nodes(); s++) {
                str += delta(r, s) + "\t";
                str1 += tau(r, s) + "\t";
            }

            str += "\n";
        }
        return str + "\n\n\n" + str1;
    }

    private double average(double matrix[][]) {
        double dSum = 0;
        for (int r = 0; r < numNodes; r++) {
            for (int s = 0; s < numNodes; s++) {
                dSum += matrix[r][s];
            }
        }

        double dAverage = dSum / (double) (numNodes * numNodes);

        return dAverage;
    }
}

