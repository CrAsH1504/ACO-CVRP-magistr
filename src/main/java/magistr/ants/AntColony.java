package magistr.ants;

import magistr.adaptation.PartPath;
import magistr.adaptation.StabilitySolution;
import magistr.cvrp.TestCVRP;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import static magistr.ants.Ant.s_bestPathVect;
import static magistr.ants.Ant.s_dBestPathValue;
import static magistr.ants.Ant.s_nLastBestPathIteration;


public abstract class AntColony implements Observer {
    protected PrintStream m_outs;

    protected AntGraph m_graph;
    protected Ant[] m_ants;
    protected int m_nAnts;
    protected int m_nAntCounter;
    protected int m_nIterCounter;
    protected int m_nIterations;
    protected int m_capacity;

    private int m_nID;

    private static int s_nIDCounter = 0;

    public AntColony(AntGraph graph, int nAnts, int nIterations, int capacity) {
        m_graph = graph;
        m_nAnts = nAnts;
        m_nIterations = nIterations;
        s_nIDCounter++;
        m_nID = s_nIDCounter;
        m_capacity = capacity;
        m_nIterCounter = 0;
    }

    public synchronized void start() {
        // creates all ants
        m_ants = createAnts(m_graph, m_nAnts);

        try {
           // m_outs = new PrintStream(new FileOutputStream(TestCVRP.folderName + "\\" + m_nID + "_" + m_graph.nodes() + "x" + m_ants.length + "x" + m_nIterations + "_colony.txt"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        PartPath dynamicAdaptaion = new PartPath(m_graph.nodes(), m_capacity);
        StabilitySolution.createSchet(); //создаем счетсчик для не устойчивых решений
        for (int i = 0; i < StabilitySolution.repeat + 1; i++) { //счетсчик провер одного решения решения
            if (StabilitySolution.getFullPathVect() != null) {
                dynamicAdaptaion = PartPath.prepareCheckStability(m_graph.nodes(), m_capacity, m_graph);
                 //сброс феромонов
                s_dBestPathValue = StabilitySolution.getFullPathValue();
                s_bestPathVect = StabilitySolution.getFullPathVect();
            }

            StabilitySolution.flagReset = false;
            while (dynamicAdaptaion.exitCondition()) {
                // loop for all iterations
                m_nIterCounter = 0;
                m_graph.resetTau();
                while (m_nIterCounter < m_nIterations) {
                    // run an iteration
                    iteration(dynamicAdaptaion);
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    // synchronize the access to the graph
                    synchronized (m_graph) {
                        // apply global updating rule
                        globalUpdatingRule();
                    }
                    if (StabilitySolution.flagReset){
                        m_nIterCounter = m_nIterations;
                    }
                }
                if (!StabilitySolution.flagStart) {
                    dynamicAdaptaion.routeDivision(s_bestPathVect, s_dBestPathValue, m_graph); //при первом прогоне получаем одно решение и запоминаем
                } else {
                    dynamicAdaptaion.checkStabilitySolution(s_dBestPathValue, m_graph); //провери устойчивость решения
                }

            }
            if (StabilitySolution.getFullPathVect() == null){
                StabilitySolution.flagStart = true;
                StabilitySolution.setFullPathValue(s_dBestPathValue);
                StabilitySolution.setFullPathVect(s_bestPathVect);
            }
        }
        //m_outs.close();
    }

    private void iteration(PartPath partPath) {
        m_nAntCounter = 0;
        m_nIterCounter++;
        //m_outs.print("Итерация " + m_nIterCounter);
        for (int i = 0; i < m_ants.length; i++) {
            m_ants[i].start(partPath);
        }
    }

    public AntGraph getGraph() {
        return m_graph;
    }

    public int getAnts() {
        return m_ants.length;
    }

    public int getIterations() {
        return m_nIterations;
    }

    public int getIterationCounter() {
        return m_nIterCounter;
    }

    public int getID() {
        return m_nID;
    }

    public synchronized void update(Observable ant, Object obj) {
        //m_outs.print("; " + ((Ant) ant).m_dPathValue);
        m_nAntCounter++;

        if (m_nAntCounter == m_ants.length) {
            //m_outs.println("; iteration: " + Ant.s_nLastBestPathIteration + "; result: " + Ant.s_dBestPathValue);

            System.out.println("---------------------------");
            System.out.println(m_nIterCounter + " - Best Path: " + Ant.s_dBestPathValue);
            System.out.println("---------------------------");
            System.out.println("Path seq: ");
            for (int i = 0; i < Ant.s_bestPathVect.size(); i++) {
                System.out.print(Ant.s_bestPathVect.get(i));
                System.out.print(" ");
            }
            System.out.println("\n");


            notify();

        }
    }

    public double getBestPathValue() {
        return Ant.s_dBestPathValue;
    }

    public int[] getBestPath() {
        return Ant.getBestPath();
    }

    public Vector getBestPathVector() {
        return s_bestPathVect;
    }

    public int getLastBestPathIteration() {
        return s_nLastBestPathIteration;
    }

    public boolean done() {
        return m_nIterCounter == m_nIterations;
    }

    protected abstract Ant[] createAnts(AntGraph graph, int ants);

    protected abstract void globalUpdatingRule();
}
