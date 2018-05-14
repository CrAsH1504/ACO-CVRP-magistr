package magistr.adaptation;

public class StabilitySolution {
    public static int repeat;
    private static int[] unstabilitySolution;
    private static int delta;
    public static boolean flagStart;
    public static  boolean flagReset;

    public static void createSchet(){
        unstabilitySolution = new int[delta + 1];
    }

    public static void updateSchet(int i){
        unstabilitySolution[i]++;
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
}
