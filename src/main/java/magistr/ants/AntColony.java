package magistr.ants;

import magistr.adaptation.PartPath;
import magistr.cvrp.TestCVRP;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


import static magistr.ants.Ant.bestPathValue;
import static magistr.ants.Ant.lastBestPathIteration;
import static magistr.ants.Ant.bestPathVect;

public abstract class AntColony implements Observer {
    protected PrintStream outs;

    protected AntGraph graph;
    protected Ant[] ants;
    protected int numAnts;
    protected int numAntCounter;
    protected int iterCounter;
    protected int numIterat;
    protected int capacity;

    private int id;

    private static int iDCounter = 0;

    public AntColony(AntGraph graph, int nAnts, int nIterations, int capacity) {
        this.graph = graph;
        numAnts = nAnts;
        numIterat = nIterations;
        iDCounter++;
        id = iDCounter;
        this.capacity = capacity;
        iterCounter = 0;
    }

    public synchronized void start() {
        ants = createAnts(graph, numAnts);
        try {
            outs = new PrintStream(new FileOutputStream(TestCVRP.folderName + "\\" + id + "_" + graph.nodes() + "x" + ants.length + "x" + numIterat + "_colony.txt"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        PartPath dynamicAdaptaion = new PartPath(graph.nodes(), capacity);
        while (dynamicAdaptaion.exitCondition()) {
            // loop for all iterations
            iterCounter = 0;
            graph.resetTau();
            while (iterCounter < numIterat) {
                // run an iteration
                iteration(dynamicAdaptaion);
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                synchronized (graph) {
                    globalUpdatingRule();
                }
            }
            dynamicAdaptaion.routeDivision(bestPathVect, bestPathValue, graph);//тут вставить динамическую адаптацию
        }
        if (iterCounter == numIterat) {
            outs.close();
        }
    }

    private void iteration(PartPath partPath) {
        numAntCounter = 0;
        iterCounter++;
        outs.print("Итерация " + iterCounter);
        for (int i = 0; i < ants.length; i++) {
            ants[i].start(partPath);
        }
    }

    public AntGraph getGraph() {
        return graph;
    }

    public int getAnts() {
        return ants.length;
    }

    public int getIterations() {
        return numIterat;
    }

    public int getID() {
        return id;
    }

    public synchronized void update(Observable ant, Object obj) {
        outs.print("; " + ((Ant) ant).pathValue);
        numAntCounter++;

        if (numAntCounter == ants.length) {
            outs.println("; iteration: " + Ant.lastBestPathIteration + "; result: " + Ant.bestPathValue);
            System.out.println("---------------------------");
            System.out.println(iterCounter + " - Best Path: " + Ant.bestPathValue);
            System.out.println("---------------------------");
            System.out.println("Path seq: ");
            for (int i = 0; i < Ant.bestPathVect.size(); i++) {
                System.out.print(Ant.bestPathVect.get(i));
                System.out.print(" ");
            }
            System.out.println("\n");
            notify();

        }
    }

    public double getBestPathValue() {
        return Ant.bestPathValue;
    }

    public int getLastBestPathIteration() {
        return lastBestPathIteration;
    }

    public boolean done() {
        return iterCounter == numIterat;
    }

    protected abstract Ant[] createAnts(AntGraph graph, int ants);

    protected abstract void globalUpdatingRule();
}
