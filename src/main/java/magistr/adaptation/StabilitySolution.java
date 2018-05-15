package magistr.adaptation;

import java.util.Vector;

public class StabilitySolution {
    public static int repeat;
    private static int[] unstabilitySolution;
    private static int delta;
    public static boolean flagStart;
    public static  boolean flagReset;
    private static long fullPathValue = Long.MAX_VALUE;
    private static Vector fullPathVect = null;

    public static void createSchet(){
        unstabilitySolution = new int[delta + 1];
    }

    public static void updateSchet(int i){
        unstabilitySolution[i]++;
    }

    public static long getFullPathValue() {
        return fullPathValue;
    }

    public static Vector getFullPathVect() {
        return fullPathVect;
    }

    public static String printTable(){
        String msg = "";
        for (int i = 0; i < delta + 1; i++){
            msg += String.valueOf(i) + ":" + String.valueOf(unstabilitySolution[i] + ", ");
        }
        return msg;
    }

    public static void setRepeat(int repeat) {
        StabilitySolution.repeat = repeat;
    }

    public static void setDelta(int delta) {
        StabilitySolution.delta = delta;
    }

    public static void setFullPathValue(long fullPathValue) {
        StabilitySolution.fullPathValue = fullPathValue;
    }

    public static void setFullPathVect(Vector fullPathVect) {
        StabilitySolution.fullPathVect = fullPathVect;
    }
}
