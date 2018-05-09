package magistr.adaptation;

import magistr.ants.AntGraph;

import java.util.Hashtable;
import java.util.Vector;

public class PartPath {

    long fullPathValue = Long.MAX_VALUE;
    Vector fullPathVect = null;
    Hashtable nodesToVisitTbl;

    long partPathValue;
    Vector partPathVect;
    int curNode;
    int posintionOnPath;
    int curCup;
    int maxCup;

    static int delta;
    static int part;


    PartPath(int delta, int nodes, int capacity) {
        this.delta = delta;
        part = 0;
        partPathVect = new Vector();
        posintionOnPath = 0;
        partPathValue = 0;
        curNode = 0;
        maxCup = capacity;
        for (int i = 0; i < nodes; i++)
            nodesToVisitTbl.put(i, i);
        nodesToVisitTbl.remove(0);


    }

    public void routeDivision(Vector solvePathVect, long solvePathValue, AntGraph graph) {
        if (solvePathValue < fullPathValue) {
            fullPathValue = solvePathValue;
            fullPathVect = solvePathVect;
        }
        part++;
        if (part > delta) {
            return;
        }
        double measure = fullPathVect.size() * part * 1.0 / delta;
        while (posintionOnPath < measure) {
            int newNode = (int) fullPathVect.get(posintionOnPath);
            partPathVect.addElement(newNode);
            partPathValue += graph.delta(curNode, newNode);
            if (newNode == 0) {
                curCup = maxCup;
            } else {
                curCup -= graph.demand(newNode);
                nodesToVisitTbl.remove(newNode);
            }
            curNode = newNode;
            posintionOnPath++;

        }


    }


}
