package magistr.cvrp;

import magistr.ants.Ant;
import magistr.ants.AntGraph;

import java.util.*;

/**
 * Created by Mati on 2017-05-09.
 */
public class AntCVRP extends Ant {

    public static double A;
    public static double B;
    public static double Q;
    public static double R;
    private AntGraph graph;

    private static final Random s_randGen = new Random(System.currentTimeMillis());

    protected Hashtable m_nodesToVisitTbl;

    public AntCVRP(int nStartNode, Observer observer, int cap) {
        super(nStartNode, observer, cap);
    }

    public void init() {
        super.init();
        graph = s_antColony.getGraph();
        m_nodesToVisitTbl = new Hashtable(graph.nodes());
        for (int i = 0; i < graph.nodes(); i++)
            m_nodesToVisitTbl.put(i, i);
        m_nodesToVisitTbl.remove(m_nStartNode);
    }


    @Override
    public int stateTransitionRule(int r) {
        // graph = s_antColony.getGraph();
        /*if (s_randGen.nextDouble() <= Q0) {
            return exploitation();
        }
        return exploration();*/
        return chooseNext();
    }

    private int exploration() {
        int nMaxNode = 0;
        double dSum = 0;
        int nNode = 0;

        Enumeration en = m_nodesToVisitTbl.elements();
        while (en.hasMoreElements()) {
            nNode = ((Integer) en.nextElement());
//            if(graph.tau(m_nCurNode, nNode) == 0)
//                continue;

            dSum += hValue(nNode);
        }

//        if(dSum == 0)
//            throw new RuntimeException("SUM = 0");

        double dAverage = dSum / (double) m_nodesToVisitTbl.size();

        en = m_nodesToVisitTbl.elements();
        while (en.hasMoreElements()) {
            nNode = ((Integer) en.nextElement()).intValue();

            double p = hValue(nNode) / dSum;

            if (hValue(nNode) > dAverage && graph.demand(nNode) <= m_curCap) {
                nMaxNode = nNode;
                break;
            }
        }
        if (nMaxNode != 0) {
            m_nodesToVisitTbl.remove(new Integer(nMaxNode));
            m_curCap -= graph.demand(nMaxNode);
        } else {
            m_curCap = m_maxCap;
        }
        System.out.println("Ant: " + m_nAntID + " makes move: " + m_nCurNode + " -> " + nMaxNode + " iteration: " + m_iterationCounter);
        return nMaxNode;
    }

    private int exploitation() {
        int nMaxNode = 0;
        double dMaxVal = -1;
        double dVal;
        int nNode;

        Enumeration en = m_nodesToVisitTbl.elements();
        while (en.hasMoreElements()) {
            nNode = ((Integer) en.nextElement());

            dVal = hValue(nNode);

            if (dVal > dMaxVal && graph.demand(nNode) <= m_curCap) {
                dMaxVal = dVal;
                nMaxNode = nNode;
            }
        }
        if (nMaxNode != 0) {
            m_nodesToVisitTbl.remove(nMaxNode);
            m_curCap -= graph.demand(nMaxNode);
        } else {
            m_curCap = m_maxCap;
        }
        System.out.println("Ant: " + m_nAntID + " makes move: " + m_nCurNode + " -> " + nMaxNode + " iteration: " + m_iterationCounter);
        return nMaxNode;
    }

    private int chooseNext() {
        int nMaxNode = 0;
        double dSum = 0;
        int nNode;
        Hashtable nodesToPosibleVisitTbl = new Hashtable();
        Enumeration en = m_nodesToVisitTbl.elements();
        while (en.hasMoreElements()) {
            nNode = ((int) en.nextElement());
            if (graph.demand(nNode) <= m_curCap) {
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
            m_curCap -= graph.demand(nMaxNode);
        } else {
            m_curCap = m_maxCap;
        }

    //  System.out.println("Ant: " + m_nAntID + " makes move: " + m_nCurNode + " -> " + nMaxNode + " iteration: " + m_iterationCounter);


        return nMaxNode;
    }


    private double hValue(int nNode) {
        double value = Math.pow(graph.tau(m_nCurNode, nNode), A) * Math.pow(graph.etha(m_nCurNode, nNode), B);
        return value == 0 ? Double.MIN_VALUE : value;
    }

    @Override
    public void localUpdatingRule(Vector path, double length) {
        final AntGraph graph = s_antColony.getGraph();
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
        return m_nodesToVisitTbl.isEmpty() && m_nCurNode == m_nStartNode;
    }
}
