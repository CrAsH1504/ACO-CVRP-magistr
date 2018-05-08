/**
 * Created by Mati on 2017-05-09.
 */
package magistr.ants;

import magistr.cvrp.TestCVRP;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public abstract class Ant extends Observable implements Runnable
{
    protected int m_nAntID;

    protected int[][]  m_path;
    protected int m_curCap;
    protected int      m_nCurNode;
    protected int      m_nStartNode;
    protected double   m_dPathValue;
    protected Observer m_observer;
    protected Vector   m_pathVect;
    protected int      m_iterationCounter;

    private static int s_nAntIDCounter = 0;
    private static PrintStream s_outs;

    protected static AntColony s_antColony;

    public static double    s_dBestPathValue = Double.MAX_VALUE;
    public static Vector    s_bestPathVect  = null;
    public static int[][]   s_bestPath      = null;
    public static int       s_nLastBestPathIteration = 0;
    public static int m_maxCap;

    public static void setAntColony(AntColony antColony)
    {
        s_antColony = antColony;
    }

    public static void reset()
    {
        s_dBestPathValue = Double.MAX_VALUE;
        s_bestPathVect = null;
        s_bestPath = null;
        s_nLastBestPathIteration = 0;
        s_outs = null;
    }

    public Ant(int nStartNode, Observer observer, int cap)
    {
        s_nAntIDCounter++;
        m_nAntID    = s_nAntIDCounter;
        m_nStartNode = nStartNode;
        m_observer  = observer;
        m_maxCap = cap;
        m_iterationCounter = 0;
    }

    public void init()
    {
        if(s_outs == null)
        {
            try
            {
                s_outs = new PrintStream(new FileOutputStream(TestCVRP.folderName + "\\" + s_antColony.getID()+ "_" + s_antColony.getGraph().nodes() + "x" + s_antColony.getAnts() + "x" + s_antColony.getIterations() + "_ants.txt"));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        final AntGraph graph = s_antColony.getGraph();
        m_nCurNode   = m_nStartNode;

        m_path      = new int[graph.nodes()][graph.nodes()];
        m_pathVect  = new Vector(graph.nodes());

        m_pathVect.addElement(new Integer(m_nStartNode));
        m_dPathValue = 0;
        m_curCap = m_maxCap;
    }

    public void start()
    {
        init();  // начальные данные для муравья
        Thread thread = new Thread(this);
        thread.setName("Ant " + m_nAntID);
        thread.start();
    }

    public void run()
    {
        m_iterationCounter++;
        final AntGraph graph = s_antColony.getGraph();

        // repeat while End of Activity Rule returns false
        while(!end())
        {
            int nNewNode;

            // synchronize the access to the graph
            synchronized(graph)
            {
                // apply the State Transition Rule
                nNewNode = stateTransitionRule(m_nCurNode);

                // update the length of the path
                m_dPathValue += graph.delta(m_nCurNode, nNewNode);
            }

            // add the current node the list of visited nodes
            m_pathVect.addElement(new Integer(nNewNode));
            m_path[m_nCurNode][nNewNode] = 1;



            // update the current node
            m_nCurNode = nNewNode;
        }

        synchronized(graph)
        {
            // apply the Local Updating Rule
            localUpdatingRule(m_pathVect, m_dPathValue);
        }

        synchronized(graph)
        {
            // update the best tour value
            if(better(m_dPathValue, s_dBestPathValue))
            {
//                localUpdatingRule(m_pathVect);
                s_dBestPathValue        = m_dPathValue;
                s_bestPath              = m_path;
                s_bestPathVect          = m_pathVect;
                s_nLastBestPathIteration = m_iterationCounter;

                s_outs.println("Ant " + m_nAntID + ", Лучшая длина " + s_dBestPathValue + ", Итерация " + s_nLastBestPathIteration + ", длина " + s_bestPathVect.size() + ", маршрут" + s_bestPathVect);
            }
        }

        // update the observer
        m_observer.update(this, null);

        if(s_antColony.done())
            s_outs.close();
    }

    protected abstract boolean better(double dPathValue, double dBestPathValue);

    public abstract int stateTransitionRule(int r);

    public abstract void localUpdatingRule(Vector path, double length);

    public abstract boolean end();

    public static int[] getBestPath()
    {
        int nBestPathArray[] = new int[s_bestPathVect.size()];
        for(int i = 0; i < s_bestPathVect.size(); i++)
        {
            nBestPathArray[i] = ((Integer)s_bestPathVect.elementAt(i)).intValue();
        }

        return nBestPathArray;
    }

    public int getM_nAntID() {
        return m_nAntID;
    }

    public int getM_nCurNode() {
        return m_nCurNode;
    }

    public int getM_iterationCounter() {
        return m_iterationCounter;
    }

    public String toString()
    {
        return "Ant " + m_nAntID + ":" + m_nCurNode;
    }
}
