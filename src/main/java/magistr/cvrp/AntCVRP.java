package magistr.cvrp;

import magistr.adaptation.PartPath;
import magistr.ants.AntGraph;
import magistr.ants.Ant;

import java.util.*;

public class AntCVRP extends Ant {

    private static final double A = 1.04;
    private static final double B = 1.5;
    public static final double Q = 4;
    public static final double R = 0.74;
    private AntGraph graph;

    private static final Random s_randGen = new Random(System.currentTimeMillis());

    protected Hashtable m_nodesToVisitTbl;

    public AntCVRP(int nStartNode, Observer observer, int cap) {
        super(nStartNode, observer, cap);
    }

    public void init(PartPath partPath) {
        super.init(partPath);
        graph = antColony.getGraph();
        m_nodesToVisitTbl = new Hashtable(partPath.getNodesToVisitTbl());
    }


    @Override
    public int stateTransitionRule(int r) {
        // graph = antColony.getGraph();
        /*if (s_randGen.nextDouble() <= Q0) {
            return exploitation();
        }
        return exploration();*/
        return chooseNext();
    }

    private int chooseNext() {
        int nMaxNode = 0;
        double dSum = 0;
        int nNode;
        Hashtable nodesToPosibleVisitTbl = new Hashtable();
        Enumeration en = m_nodesToVisitTbl.elements();
        while (en.hasMoreElements()) {
            nNode = ((int) en.nextElement());
            if (graph.demand(nNode) <= curCapac) {
                nodesToPosibleVisitTbl.put(nNode, nNode);
                dSum += hValue(nNode);
            }
        }

        if (dSum != 0) {
            // генерация выбора маршрута
            double rand = Math.random();
            double segment = 0;

            en = nodesToPosibleVisitTbl.elements();
            while (en.hasMoreElements()) {
                nNode = (Integer) en.nextElement();
                segment += hValue(nNode) / dSum;

                if (rand < segment) {
                    nMaxNode = nNode;
                    break;
                }
            }
        }

        if (nMaxNode != 0) {
            m_nodesToVisitTbl.remove(new Integer(nMaxNode));
            curCapac -= graph.demand(nMaxNode);
        } else {
            curCapac = maxCap;
        }

        //  System.out.println("Ant: " + numAntID + " makes move: " + curNode + " -> " + nMaxNode + " iteration: " + intCounter);


        return nMaxNode;
    }


    private double hValue(int nNode) {
        double value = Math.pow(graph.tau(curNode, nNode), A) * Math.pow(graph.etha(curNode, nNode), B);
        return value == 0 ? Double.MIN_VALUE : value;
    }

    @Override
    public void localUpdatingRule(Vector path, double length) {
        final AntGraph graph = antColony.getGraph();
        for (int i = 1; i < path.size(); i++) {
            int currVertex = (int) path.get(i - 1);
            int nextVertex = (int) path.get(i);
            double val = graph.tau(currVertex, nextVertex) + Q / (R * length);
            graph.updateTau(currVertex, nextVertex, val);
            double val2 = graph.tau( nextVertex,currVertex) + Q / (R * length);
            graph.updateTau(nextVertex, currVertex , val2);
        }
    }

    @Override
    public boolean better(double dPathValue1, double dPathValue2) {
        return dPathValue1 < dPathValue2;
    }

    @Override
    public boolean end() {
        return m_nodesToVisitTbl.isEmpty() && curNode == startNode;
    }
}
