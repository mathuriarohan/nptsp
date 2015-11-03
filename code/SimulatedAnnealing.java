/**
* @author Rohan Mathuria
*/

/*
NOTES TO THOSE RUNNING THIS FILE:
1) In the main method, there are four user-inputs near the beginning. These are, respectively:
OUTPUT_PATH: the desired path for output.
INPUT_PATH: the location of the input files.
T: the number of test cases
startCase: the test case to start from.
They can be modified on lines 372-375

2) There are three lines that choose a path for assign (390-392). Two should be commented out. The first
runs 1 billion iterations, the second 100 million, and the third 10 million. In most cases, the third
should be sufficient; however the second may have a few minor advantages. I have not noticed any
advantage of running 1 billion iterations over 100 million. Comment and uncomment the line that you wish.

3) The output is divided into 1 file for each input in order to make mistakes less punishing. These
files can be merged at the completion of the program.

4) Status updates have been turned on. To turn them off, mark STATUS_UPDATES as false on line 29
*/

import java.io.*;
import java.util.*;

public class SimulatedAnnealing {
    public static final boolean STATUS_UPDATES = true;

    //returns the cost given a set of weights and a path. Does not check whether the path is valid
    public static int findCost(int[][] weights, int[] path) {
        if (path == null) {
            return 4901;
        }
        int totalCost = 0;
        for (int i = 1; i < path.length; i++) {
            totalCost += weights[path[i-1]][path[i]];
        }
        return totalCost;
    }

    //returns true iff the path is valid
    public static boolean isValidPath (int[] path, char[] c) {
        if (path == null) {
            return false;
        }

        //every element only occurs once
        boolean[] visited = new boolean[path.length];
        for (int i = 0; i < path.length; i++) {
            if (visited[path[i]]) {
                return false;
            } visited[path[i]] = true;
        }

        //3 colors max in a row
        for (int i = 3; i < path.length; i++) {
            if ((c[path[i]] == c[path[i-1]]) && (c[path[i]] == c[path[i-2]]) && (c[path[i]] == c[path[i-3]])) {
                return false;
            }
        } return true;
    }

    //takes two paths and returns a mixture of the two.
    public static int[] crossOver(int[] path1, int[] path2, int[][] weights, char[] c) {
        if ((path1 == null) || (path2 == null)){
            return path1;
        }
        Random rand = new Random();
        int[] crossedPaths = new int[weights.length];
        int a = rand.nextInt(weights.length);
        int b = rand.nextInt(weights.length) + 1;
        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }
        boolean[] visited = new boolean[weights.length];
        if (rand.nextDouble() < .5) {
            for (int i = a; i < b; i++) {
                crossedPaths[i] = path2[i];
                visited[path2[i]] = true;
            }
        }
        else {
            for (int i = a; i < b; i++) {
                crossedPaths[i] = path2[b-1-i+a];
                visited[path2[i]] = true;
            }
        }
        int addIndex = 0;
        for (int i = 0; i < weights.length; i++) {
            if (addIndex == a) {
                addIndex = b;
            }
            if (!(visited[path1[i]])) {
                crossedPaths[addIndex] = path1[i];
                addIndex += 1;
                visited[path1[i]] = true;
            }
        }
        if (SimulatedAnnealing.isValidPath(crossedPaths, c)) {
            return crossedPaths;
        }
        return path1;
    }

    //takes a path and returns a shuffled version of that path
    public static int[] twoOp(int[] path, int[][] weights, char[] c) {
        Random rand = new Random();
        if (path == null) {
            return null;
        }
        int[] newPath = new int[path.length];
        int a = rand.nextInt(weights.length);
        int b = rand.nextInt(weights.length) + 1;
        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }
        int index = 0;
        for (int i = 0; i < a; i++) {
            newPath[index] = path[i];
            index += 1;
        }
        for (int i = b; i < path.length; i++) {
            newPath[index] = path[i];
            index += 1;
        }
        for (int i = a; i < b; i++) {
            newPath[index] = path[i];
            index += 1;
        }
        if (SimulatedAnnealing.isValidPath(newPath, c)) {
            return newPath;
        }
        return path;
    }
    
    public static int[] kOp(int[] path, int[][] weights, char[] c, int k) {
        Random rand = new Random();
        int[] newPath = new int[path.length];
        int[] loc = new int[k+2];
        for (int i = 1; i < loc.length; i++) {
            loc[i] = rand.nextInt(weights.length);
        }
        loc[k+1] = weights.length;
        Arrays.sort(loc);
        int index = 0;
        boolean[] visited = new boolean[k+1];
        while (index < weights.length) {
            int start = rand.nextInt(k+1);
            if (visited[start]) {
                continue;
            } visited[start] = true;
            if (rand.nextDouble() < .5) {
                for (int i = loc[start]; i < loc[start + 1]; i++) {
                    newPath[index] = path[i];
                    index += 1;
                }
            } else {
                for (int i = loc[start + 1] - 1; i >= loc[start]; i--) {
                    newPath[index] = path[i];
                    index += 1;
                }
            }
        }
        if (SimulatedAnnealing.isValidPath(newPath, c)) {
            return newPath;
        }
        return path;
    }

    //mutate was previously used to change paths; however two-opt is used now
    public static int[] mutate(int[] path, int[][] weights, char[] c, double branchProb, double temperature) {
        int[] pathCopy = path.clone();
        Random rand = new Random();
        int branchNum = 2;
        while ((branchNum <= weights.length - 1) && (branchNum <= 10) && (branchProb > rand.nextDouble())) {
            branchNum += 1;
        }
        int[] randElem = new int[branchNum];
        boolean[] visited = new boolean[weights.length];
        for (int i = 0; i < branchNum; i++) {
            int next = rand.nextInt(weights.length);
            if (visited[next] == true) {
                i -= 1;
            } else {
                visited[next] = true;
                randElem[i] = next;
            }
        }
        int[] randElemCopy = randElem.clone();
        SimulatedAnnealing.shuffleArray(randElem);
        for (int i = 0; i < branchNum; i++) {
            pathCopy[randElem[i]] = path[randElemCopy[i]];
        }
        if (SimulatedAnnealing.isValidPath(pathCopy, c)) {
            int pathScore = SimulatedAnnealing.findCost(weights, path);
            int copyScore = SimulatedAnnealing.findCost(weights, pathCopy);
            if ((copyScore <= pathScore) || (Math.exp(((double) (copyScore - pathScore))/temperature) > rand.nextDouble())) {
                return pathCopy;
            } else {
                return path;
            }
        }
        return path;
    }

    //a helper method to randomly shuffle arrays
    private static void shuffleArray(int[] arr) {
        Random rand = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    //modified simulated annealing that intersperses mutation of the path and intersection with the best seen path
    public static int[] SimulatedAnnealing(int numIter, int[][] weights, char[] c, 
        double startTemp, double decayRate, int testcaseNumber) {
        Random rand = new Random();
        double temperature = startTemp;
        int[] path = null;
        while (path == null) {
            path = SimulatedAnnealing.getSemiGreedyPath(weights, c);
        }
        int[] bestPath = path;
        int bestCost = SimulatedAnnealing.findCost(weights, path);
        int cost = bestCost;
        for (int i = 0; i <= numIter; i++) {
            if (i == (numIter * 9 / 10)) {
                temperature = 0.00001;
            }
            if (rand.nextDouble() < (temperature)) {
                path = SimulatedAnnealing.kOp(path, weights, c, rand.nextInt(4) + 2);
                //bestPath = path;
            } else {
                //path = bestPath;
                path = SimulatedAnnealing.crossOver(path, bestPath, weights, c);
            }
            //path = SimulatedAnnealing.mutate(path, weights, c, .8, temperature);
            cost = SimulatedAnnealing.findCost(weights, path);
            if ((cost <= bestCost)){ //|| (Math.exp((cost - bestCost)/temperature/10) < rand.nextDouble())){
                bestPath = path;
                bestCost = cost;
            }
            //path = SimulatedAnnealing.crossOver(path, bestPath, weights, c);
            temperature = temperature * decayRate;
            //decayRate = Math.pow(decayRate, decayChange);
            if ((i % (numIter / 10) == 0) && (SimulatedAnnealing.STATUS_UPDATES)) {
                System.out.println("Testcase Number " + testcaseNumber);
                System.out.print(Math.round(((double) i)/((double) numIter) * 100));
                System.out.println("% Done");
                System.out.println(temperature);
                System.out.println(bestCost);
                System.out.println(Arrays.toString(bestPath));
                System.out.println("");
            }
        }
        return bestPath;
    }

    //a helper that returns the best greedy path after a number of iterations. Useful on poorly crafted inputs
    public static int[] getBest(int numPaths, int[][] weights, char[] c) {
        int[] bestPath = null;
        int lowestCost = 4901;
        for (int i = 0; i < numPaths; i++) {
            int[] newPath = SimulatedAnnealing.getSemiGreedyPath(weights, c);
            int cost = SimulatedAnnealing.findCost(weights, newPath);
            if (cost <= lowestCost) {
                bestPath = newPath;
                lowestCost = cost;
            }
        }
        return bestPath;
    }

    //gets a random greedy path. Useful on poorly crafted inputs or as a starting point for other algorithms
    public static int[] getSemiGreedyPath(int[][] weights, char[] c) {
        Random rand = new Random();
        int size = weights.length;
        int[] path = new int[size];
        boolean[] visited = new boolean[size];

        //Randomly choose a starting vertex
        path[0] = rand.nextInt(weights.length);
        visited[path[0]] = true;
        int numRedSeen = 0;
        int numBlueSeen = 0;
        int redStreak = 0;
        int blueStreak = 0;

        if (c[path[0]] == 'R') {
            numRedSeen += 1;
            redStreak += 1;
        } else {
            numBlueSeen += 1;
            blueStreak += 1;
        }

        for (int i = 1; i < size; i++) {
            boolean redNec = false;
            boolean blueNec = false;

            if ((redStreak == 3) || ((size/2 - numBlueSeen - 3) > (size/2 - numRedSeen - 1) * 3)) {
                blueNec = true;   
            }  if ((blueStreak == 3) || ((size/2 - numRedSeen - 3) > (size/2 - numBlueSeen - 1) * 3)) {
                redNec = true;
            } if ((redNec == true) && (blueNec == true)) {
                return null;
            }

            int[] nextVertexWeights = new int[size];

            for (int j = 0; j < size; j++) {
                if (visited[j] == false) {
                    if (((c[j] == 'R') && (!(blueNec))) || ((c[j] == 'B') && (!(redNec)))) {
                        nextVertexWeights[j] = 20000 - (weights[i-1][j] * weights[i-1][j]);
                    }
                } 
            }

            
            int sumWeights = 0;
            for (int j = 0; j < size; j++) {
                sumWeights += nextVertexWeights[j];
            }

            int nextVertexOrder = rand.nextInt(sumWeights);
            int nextVertex = 0;
            for (int j = 0; j < size; j++) {
                nextVertexOrder -= nextVertexWeights[j];
                if (nextVertexOrder < 0) {
                    nextVertex = j;
                    break;
                }
            }
            path[i] = nextVertex; 
            visited[path[i]] = true;
            
            if (c[path[i]] == 'R') {
                numRedSeen += 1;
            } else {
                numBlueSeen += 1;
            }
            if (c[path[i-1]] == c[path[i]]) {
                if (c[path[i]] == 'R') {
                    redStreak += 1;  
                } else {
                    blueStreak += 1;
                }
            } else {
                if (c[path[i]] == 'R') {
                    redStreak = 1;
                    blueStreak = 0;
                } else {
                    redStreak = 0;
                    blueStreak = 1;
                }
            }

        }
        return path;
    }

    public static void main (String[] args) throws IOException {
        String OUTPUT_PATH = "output2/answer"; // where do you want the output. Note any directories must be pre-created.
        String INPUT_PATH = "instances/"; // where is the input?
        int T = 495; // number of test cases
        int startCase = 467; // where to start at

        for (int t = startCase; t <= T; t++) {
            PrintWriter fout = new PrintWriter (new FileWriter (new File (OUTPUT_PATH + t + ".out")));
            Scanner fin = new Scanner (new File (INPUT_PATH + t + ".in"));
            int N = fin.nextInt();
            int[][] d = new int[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    d[i][j] = fin.nextInt();
                }
            }
            char[] c = fin.next().toCharArray();

            long startTime = System.nanoTime();
            //int[] assign = SimulatedAnnealing.SimulatedAnnealing(1000000000, d, c, 1, .999999997, 1, t);
            int[] assign = SimulatedAnnealing.SimulatedAnnealing(100000000, d, c, 1, .99999997, t);
            //int[] assign = SimulatedAnnealing.SimulatedAnnealing(10000000, d, c, 1, .9999997, t);

            long endTime = System.nanoTime();

            if (SimulatedAnnealing.STATUS_UPDATES) {
                System.out.print( (endTime - startTime)/1000000);
                System.out.println(" ms");
                System.out.println(SimulatedAnnealing.findCost(d, assign) + "\n\n");
            }

            for (int i = 0; i < N; i++) {
                assign[i] = assign[i]+1;
            }

            fout.print(assign[0]);
            for (int i = 1; i < N; i++)
                fout.print (" " + assign[i]);
           fout.println();
           fout.close();
        }
       //fout.close();
    }
}
