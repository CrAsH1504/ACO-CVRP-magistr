package magistr.cvrp;

import magistr.adaptation.PartPath;
import magistr.adaptation.StabilitySolution;
import magistr.ants.AntGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;


public class TestCVRP {
    private static Random s_ran = new Random(System.currentTimeMillis());

    public static String folderName = null;

    static public double[][] create2DDoubleMatrixFromFile(Path path) throws IOException {
        return Files.lines(path)
                .map((l) -> l.trim().split("\\s+"))
                .map((sa) -> Stream.of(sa).mapToDouble(Double::parseDouble).toArray())
                .toArray(double[][]::new);
    }


    public static void main(String[] args) {
        start(new String[]{"-a", "76",
                "-i", "100",
                "-r", "2",
                "-file", "E-n76-k8.vrp",
                "-delta", "10",
                "-stability", "10"

        });
    }

    static void start(String[] args) {
        // Print application prompt to console.
        System.out.println("AntColonySystem for TSP");

        if (args.length < 4) {
            System.out.println("Wrong number of parameters");
            return;
        }

        int nAnts = 0;
        int nNodes = 0;
        int nIterations = 0;
        int nRepetitions = 0;
        int capacity = 0;
        int[] demand = null;
        double d[][] = null;
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-a")) {
                nAnts = Integer.parseInt(args[i + 1]);
                System.out.println("Ants: " + nAnts);
            } else if (args[i].equals("-i")) {
                nIterations = Integer.parseInt(args[i + 1]);
                System.out.println("Iterations: " + nIterations);
            } else if (args[i].equals("-r")) {
                nRepetitions = Integer.parseInt(args[i + 1]);
                System.out.println("Repetitions: " + nRepetitions);
            } else if (args[i].equals("-file")) {
                String fileName = args[i + 1];
                Generator generator = new Generator("test_src/" + fileName);
                generator.init();
                try {
                    d = create2DDoubleMatrixFromFile(Paths.get("test_src/input.txt"));
                    for (int g = 0; g < nNodes; g++) {
                        for (int h = 0; h < nNodes; h++) {
                            System.out.print(d[g][h] + " ");
                        }
                        System.out.print("\n");
                    }
                    nNodes = generator.Dimensions();
                    demand = generator.Demands();
                    capacity = generator.Capacity();
                    System.out.println("Nodes: " + nNodes);
                    System.out.println("Capacity: " + capacity);
                    for (int g = 0; g < nNodes; g++) {
                        System.out.print(demand[g] + " ");
                    }
                } catch (java.io.IOException ex) {
                    System.out.println("input file not found");
                }
            } else if (args[i].equals("-delta")) {
                PartPath.setDelta(Integer.parseInt(args[i + 1]));
                StabilitySolution.setDelta(Integer.parseInt(args[i + 1]));
            } else if (args[i].equals("-stability")) {
                StabilitySolution.setRepeat(Integer.parseInt(args[i + 1]));
            }

        }

        if (nAnts == 0 || nNodes == 0 || nIterations == 0 || nRepetitions == 0) {
            System.out.println("One of the parameters is wrong");
            return;
        }


        AntGraph graph = new AntGraph(nNodes, d, demand);

        try {
            ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream("" + nNodes + "_antgraph.bin"));
            outs.writeObject(graph);
            outs.close();


            FileOutputStream outs1 = new FileOutputStream("" + nNodes + "_antgraph.txt");

            for (int i = 0; i < nNodes; i++) {
                for (int j = 0; j < nNodes; j++) {
                    outs1.write((graph.delta(i, j) + ",").getBytes());
                }
                outs1.write('\n');
            }

            outs1.close();

            folderName = nNodes + "x" + nAnts + "x" + nIterations + "x" + nRepetitions + "_results";
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdir();
            }
            PrintStream outs2 = new PrintStream(new FileOutputStream(folderName + "\\" + nNodes + "x" + nAnts + "x" + nIterations + "_results.txt"));

            long totalTime = 0;

            for (int i = 0; i < nRepetitions; i++) {

                graph.resetTau();
                AntColonyCVRP antColony = new AntColonyCVRP(graph, nAnts, nIterations, capacity);
                StabilitySolution.setFullPathValue(Long.MAX_VALUE);
                StabilitySolution.setFullPathVect(null);
                PartPath.setFullPathValue(Long.MAX_VALUE);
                PartPath.setFullPathVect(null);

                long begin = System.currentTimeMillis();
                StabilitySolution.flagStart = false;
                antColony.start();
                outs2.println(i + "," + PartPath.getFullPathValue() + "," + antColony.getLastBestPathIteration() + ", " + StabilitySolution.printTable());
                totalTime += System.currentTimeMillis() - begin;

            }
            System.out.printf("\nAverage time: %.2f", ((double) totalTime) / 1000.0);
            outs2.close();
        } catch (Exception ex) {
        }


    }
}

