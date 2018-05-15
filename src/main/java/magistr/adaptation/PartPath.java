package magistr.adaptation;

import magistr.ants.AntGraph;

import java.util.Hashtable;
import java.util.Vector;

public class PartPath {

    private static long fullPathValue = Long.MAX_VALUE;
    private static Vector fullPathVect = null;
    private static Hashtable nodesToVisitTbl;

    private long partPathValue;
    private Vector partPathVect;
    private int curNode;
    private int posintionOnPath;
    private int curCup;
    private int maxCup;

    private static int delta;
    private static int part;
    private static int iterate;


    public PartPath(int nodes, int capacity) {
        part = 0;
        iterate = 0;
        partPathVect = new Vector();
        posintionOnPath = 1;
        partPathValue = 0;
        curNode = 0;
        maxCup = capacity;
        curCup = capacity;
        nodesToVisitTbl = new Hashtable();
        for (int i = 0; i < nodes; i++)
            nodesToVisitTbl.put(i, i);
        partPathVect.add(0);
        nodesToVisitTbl.remove(0);
    }

    public static PartPath prepareCheckStability(int nodes, int capacity, AntGraph graph){
        PartPath startDate = new PartPath(nodes,capacity);
        fullPathValue = StabilitySolution.getFullPathValue();
        fullPathVect = StabilitySolution.getFullPathVect();
        startDate.dynamicStep(graph);
        return startDate;
    }

    public void checkStabilitySolution(long solvePathValue, AntGraph graph) {
        if (solvePathValue < fullPathValue) { //если не устойчиво отмечаем шаг и сбрасываем текущую попытку
            StabilitySolution.updateSchet(part);
            PartPath.part = PartPath.delta;
        } else {
            dynamicStep(graph); //продолжаем проверку устойчивости
        }

    }

    public void routeDivision(Vector solvePathVect, long solvePathValue, AntGraph graph) {
        if (solvePathValue < fullPathValue) {
            fullPathValue = solvePathValue;
            fullPathVect = solvePathVect;
        }
        dynamicStep(graph);
    }

    void dynamicStep(AntGraph graph) {
        ricePart();
        if (part >= delta) {
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

    public boolean exitCondition() {
        return part < delta;
    }

    ;

    public static void setDelta(int delta) {
        PartPath.delta = delta;
    }

    static void ricePart() {
        iterate++;
        part = iterate;  //равномерное деление
        // part = (1 + iterate) * iterate / 2;  // по арифмитической прогррессии
    }

    public Hashtable getNodesToVisitTbl() {
        return nodesToVisitTbl;
    }

    public long getPartPathValue() {
        return partPathValue;
    }

    public Vector getPartPathVect() {
        return partPathVect;
    }

    public int getCurNode() {
        return curNode;
    }

    public int getCurCup() {
        return curCup;
    }

    public int getMaxCup() {
        return maxCup;
    }

    public Vector getFullPathVect() {
        return fullPathVect;
    }

    public static long getFullPathValue() {
        return fullPathValue;
    }

    public static void setFullPathValue(long fullPathValue) {
        PartPath.fullPathValue = fullPathValue;
    }

    public static void setFullPathVect(Vector fullPathVect) {
        PartPath.fullPathVect = fullPathVect;
    }
}
