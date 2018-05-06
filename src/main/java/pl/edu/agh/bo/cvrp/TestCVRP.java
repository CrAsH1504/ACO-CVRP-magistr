package pl.edu.agh.bo.cvrp;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.edu.agh.bo.ants.AntGraph;
import pl.edu.agh.bo.charts.AntsLineChart;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by Mati on 2017-05-21.
 */
public class TestCVRP {
    private static Random s_ran = new Random(System.currentTimeMillis());

    public static double[] chet = new double[5];

    public static String folderName = null;

    static public double[][] create2DDoubleMatrixFromFile(Path path) throws IOException {
        return Files.lines(path)
                .map((l) -> l.trim().split("\\s+"))
                .map((sa) -> Stream.of(sa).mapToDouble(Double::parseDouble).toArray())
                .toArray(double[][]::new);
    }


    public static void main(String[] args) throws FileNotFoundException, IOException {
        double A;
        double B;
        double R;
        double Q;

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Parameters");

        Row row = sheet.createRow(0);

        Cell cellA = row.createCell(0);
        cellA.setCellValue("A");

        Cell cellB = row.createCell(1);
        cellB.setCellValue("B");

        Cell cellR = row.createCell(2);
        cellR.setCellValue("R");

        Cell cellQ = row.createCell(3);
        cellQ.setCellValue("Q");

        Cell cellResult;
        for (int i = 4; i < 9; i++) {
            cellResult = row.createCell(i);
            cellResult.setCellValue("Result " + (i - 3));
        }

        Cell cellMaxResult =  row.createCell(9);
        cellMaxResult.setCellValue("Max value");

        int i = 1;
        for (A = 0; A < 2; A += 0.1) {
            for (B = 0; B < 3; B += 0.1) {
                for (R = 0.4; R < 0.9; R += 0.01) {
                    for (Q = 1; Q < 9; Q += 1) {

                        AntCVRP.A = A;
                        AntCVRP.B = B;
                        AntCVRP.R = R;
                        AntCVRP.Q = Q;



                        start(new String[]{"-a", "10",
                                        "-i", "5000",
                                        "-r", "5",
                                        "-file", "E-n22-k4.vrp"
                        });





                        row = sheet.createRow(i++);
                        cellA = row.createCell(0, 0);
                        cellA.setCellValue(A);

                        cellB = row.createCell(1, 0);
                        cellB.setCellValue(B);

                        cellR = row.createCell(2, 0);
                        cellR.setCellValue(R);

                        cellQ = row.createCell(3, 0);
                        cellQ.setCellValue(Q);

                        double maxVal = Double.MAX_VALUE;
                        for (int j = 4; j < 9; j++) {
                            cellResult = row.createCell(j,0);
                            cellResult.setCellValue(chet[j-4]);
                            if (chet[j-4]< maxVal){
                                maxVal = chet[j-4];
                            }
                        }

                        cellMaxResult = row.createCell(9,0);
                        cellMaxResult.setCellValue(maxVal);






                    }
                }
            }
        }


        book.write(new FileOutputStream(("result.xlsx")));


      /*  start(new String[]{"-a", "10",
                        "-i", "5000",
                        "-r", "5",
                        "-file", "E-n22-k4.vrp"

        );

*/
    }

    static void start(String[] args) {
        // Print application prompt to console.
   //    System.out.println("AntColonySystem for TSP");

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
        boolean isShowGraph = false;
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-a")) {
                nAnts = Integer.parseInt(args[i + 1]);
            //    System.out.println("Ants: " + nAnts);
            } else if (args[i].equals("-i")) {
                nIterations = Integer.parseInt(args[i + 1]);
             //   System.out.println("Iterations: " + nIterations);
            } else if (args[i].equals("-r")) {
                nRepetitions = Integer.parseInt(args[i + 1]);
              //  System.out.println("Repetitions: " + nRepetitions);
            } else if (args[i].equals("-file")) {
                String fileName = args[i + 1];
                Generator generator = new Generator("test_src/" + fileName);
                generator.init();
                try {
                    d = create2DDoubleMatrixFromFile(Paths.get("test_src/input.txt"));
                    for (int g = 0; g < nNodes; g++) {
                        for (int h = 0; h < nNodes; h++) {
                  //          System.out.print(d[g][h] + " ");
                        }
                 //       System.out.print("\n");
                    }
                    nNodes = generator.Dimensions();
                    demand = generator.Demands();
                    capacity = generator.Capacity();
                 //   System.out.println("Nodes: " + nNodes);
                //    System.out.println("Capacity: " + capacity);
                 //   for (int g = 0; g < nNodes; g++) {
                 //       System.out.print(demand[g] + " ");
                  //  }
                //    System.out.println("\n");
                } catch (java.io.IOException ex) {
                    System.out.println("input file not found");
                }
            } else if (args[i].equals("-showgraph")) {
                isShowGraph = true;
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
            // PrintStream outs2 = new PrintStream(new FileOutputStream(folderName + "\\" + nNodes + "x" + nAnts + "x" + nIterations + "_results.txt"));

            long totalTime = 0;

            for (int i = 0; i < nRepetitions; i++) {

                graph.resetTau();
                AntColonyCVRP antColony = new AntColonyCVRP(graph, nAnts, nIterations, capacity);

                long begin = System.currentTimeMillis();
                antColony.start();
                //   outs2.println(i + "," + antColony.getBestPathValue() + "," + antColony.getLastBestPathIteration());
                chet[i] = antColony.getBestPathValue();
                totalTime += System.currentTimeMillis() - begin;

                if (isShowGraph) {
                    AntsLineChart.showGraph("" + (i + 1) + "_" + nNodes + "x" + nAnts + "x" + nIterations + "_colony.txt");
                }
            }
      //      System.out.printf("\nAverage time: %.2f", ((double) totalTime) / 1000.0);
            //  outs2.close();
        } catch (Exception ex) {
        }


    }
}

