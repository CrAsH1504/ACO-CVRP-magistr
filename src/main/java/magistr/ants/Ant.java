package magistr.ants;

import magistr.adaptation.PartPath;
import magistr.cvrp.TestCVRP;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public abstract class Ant extends Observable implements Runnable {
    protected int numAntID;
    
    protected int curCapac;
    protected int curNode;
    protected int startNode;
    protected long pathValue;
    protected Observer observer;
    protected Vector pathVect;
    protected int intCounter;

    private static int numAntIDCounter = 0;
    private static PrintStream outs;

    protected static AntColony antColony;

    public static long bestPathValue = Long.MAX_VALUE;
    public static Vector bestPathVect = null;
    public static int lastBestPathIteration = 0;
    public static int maxCap;

    public static void setAntColony(AntColony antColony) {
        Ant.antColony = antColony;
    }

    public static void reset() {
        bestPathValue = Long.MAX_VALUE;
        bestPathVect = null;
        lastBestPathIteration = 0;
        outs = null;
    }

    public Ant(int nStartNode, Observer observer, int cap) {
        numAntIDCounter++;
        numAntID = numAntIDCounter;
        startNode = nStartNode;
        this.observer = observer;
        maxCap = cap;
        intCounter = 0;
    }

    public void init(PartPath partPath) {
        if (outs == null) {
            try {
                outs = new PrintStream(new FileOutputStream(TestCVRP.folderName + "\\" + antColony.getID() + "_" + antColony.getGraph().nodes() + "x" + antColony.getAnts() + "x" + antColony.getIterations() + "_ants.txt"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        final AntGraph graph = antColony.getGraph();
        curNode = partPath.getCurNode();

        //       m_path      = new int[graph.nodes()][graph.nodes()];
        pathVect = new Vector(graph.nodes());
        pathVect.addAll(partPath.getPartPathVect());
        pathValue = partPath.getPartPathValue();
        curCapac = partPath.getCurCup();
    }

    public void start(PartPath partPath) {
        init(partPath);  // начальные данные для муравья
        Thread thread = new Thread(this);
        thread.setName("Ant " + numAntID);
        thread.start();
    }

    public void run() {
        intCounter++;
        final AntGraph graph = antColony.getGraph();

        while (!end()) {
            int nNewNode;

            synchronized (graph) {
                nNewNode = stateTransitionRule(curNode);
                pathValue += graph.delta(curNode, nNewNode);
            }

            pathVect.addElement(new Integer(nNewNode));
            curNode = nNewNode;
        }

        synchronized (graph) {
            localUpdatingRule(pathVect, pathValue);
        }

        synchronized (graph) {
            if (better(pathValue, bestPathValue)) {
                bestPathValue = pathValue;
                bestPathVect = pathVect;
                lastBestPathIteration = intCounter;

                outs.println("Ant " + numAntID + ", Лучшая длина " + bestPathValue + ", Итерация " + lastBestPathIteration + ", длина " + bestPathVect.size() + ", маршрут" + bestPathVect);
            }
        }

        observer.update(this, null);

        if (antColony.done())
            outs.close();
    }

    protected abstract boolean better(double dPathValue, double dBestPathValue);

    public abstract int stateTransitionRule(int r);

    public abstract void localUpdatingRule(Vector path, double length);

    public abstract boolean end();

    public static int[] getBestPath() {
        int nBestPathArray[] = new int[bestPathVect.size()];
        for (int i = 0; i < bestPathVect.size(); i++) {
            nBestPathArray[i] = ((Integer) bestPathVect.elementAt(i)).intValue();
        }

        return nBestPathArray;
    }

    public String toString() {
        return "Ant " + numAntID + ":" + curNode;
    }
}

