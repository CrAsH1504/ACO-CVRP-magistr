package magistr.cvrp;

import magistr.ants.Ant;
import magistr.ants.AntColony;
import magistr.ants.AntGraph;

import java.util.Random;
import java.util.Vector;

import static magistr.ants.Ant.bestPathVect;
import static magistr.ants.Ant.bestPathValue;
import static magistr.cvrp.AntCVRP.P;
import static magistr.cvrp.AntCVRP.U;

public class AntColonyCVRP extends AntColony {

    public AntColonyCVRP(AntGraph graph, int nAnts, int nIterations, int capacity) {
        super(graph, nAnts, nIterations, capacity);
    }

    @Override
    protected Ant[] createAnts(AntGraph graph, int ants) {
        Random ran = new Random(System.currentTimeMillis());
        AntCVRP.reset();
        AntCVRP.setAntColony(this);
        AntCVRP ant[] = new AntCVRP[numAnts];
        for (int i = 0; i < numAnts; i++) {
            ant[i] = new AntCVRP(0, this, capacity);
        }

        return ant;
    }

    @Override
    protected void globalUpdatingRule() {
        double dEvaporation;
        for (int r = 0; r < graph.nodes(); r++) {
            for (int s = 0; s < graph.nodes(); s++) {
                if (r != s) {
                    dEvaporation = P * graph.tau(r, s);
                    graph.updateTau(r, s, dEvaporation);
                }
            }
        }
        Vector path = bestPathVect;
        final AntGraph graph = this.graph;
        for (int i = 1; i < path.size(); i++) {
            int currVertex = (int) path.get(i - 1);
            int nextVertex = (int) path.get(i);
            double val = graph.tau(currVertex, nextVertex) + U / bestPathValue;
            graph.updateTau(currVertex, nextVertex, val);
            double val2 = graph.tau( nextVertex,currVertex) + U / bestPathValue;
            graph.updateTau(nextVertex, currVertex , val2);
        }

    }
}
