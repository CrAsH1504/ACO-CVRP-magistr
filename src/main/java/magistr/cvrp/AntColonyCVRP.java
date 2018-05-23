package magistr.cvrp;

import magistr.ants.Ant;
import magistr.ants.AntColony;
import magistr.ants.AntGraph;

import java.util.Random;
import java.util.Vector;

import static magistr.ants.Ant.s_bestPathVect;
import static magistr.ants.Ant.s_dBestPathValue;
import static magistr.cvrp.AntCVRP.R;
import static magistr.cvrp.AntCVRP.Q;

public class AntColonyCVRP extends AntColony {

    public AntColonyCVRP(AntGraph graph, int nAnts, int nIterations, int capacity) {
        super(graph, nAnts, nIterations, capacity);
    }

    @Override
    protected Ant[] createAnts(AntGraph graph, int ants) {
        Random ran = new Random(System.currentTimeMillis());
        AntCVRP.reset();
        AntCVRP.setAntColony(this);
        AntCVRP ant[] = new AntCVRP[m_nAnts];
        for (int i = 0; i < m_nAnts; i++) {
            ant[i] = new AntCVRP(0, this, m_capacity);
        }

        return ant;
    }

    @Override
    protected void globalUpdatingRule() {
        double dEvaporation = 0;

        for (int r = 0; r < m_graph.nodes(); r++) {
            for (int s = 0; s < m_graph.nodes(); s++) {
                if (r != s) {
                    dEvaporation = R * m_graph.tau(r, s);
                    m_graph.updateTau(r, s, dEvaporation);
                }
            }
        }
        Vector path = s_bestPathVect;
        final AntGraph graph = m_graph;
        for (int i = 1; i < path.size(); i++) {
            int currVertex = (int) path.get(i - 1);
            int nextVertex = (int) path.get(i);
            double val = graph.tau(currVertex, nextVertex) + Q / s_dBestPathValue;
            graph.updateTau(currVertex, nextVertex, val);
            double val2 = graph.tau( nextVertex,currVertex) + Q / s_dBestPathValue;
            graph.updateTau(nextVertex, currVertex , val2);
        }

    }
}
