package main;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import redsensores.*;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static void showOptions() {
        System.out.println("1. Inicializadora senzilla");
        System.out.println("2. Inicializadora de grupos de 4 en 4");
        System.out.println("3. Inicializadora optima");
    }

    private static void showSuccessors() {
        System.out.println("1. Successor Swap");
        System.out.println("2. Successor Connect-TO ");
    }

    public static void main(String[] args) throws Exception {

        int nsensors, ncenters, seed, inicializadora, successor;

        Scanner in = new Scanner(System.in);

        System.out.println("Introduce NUMERO DE SENSORES");
        nsensors = in.nextInt();

        System.out.println("Introduce NUMERO DE CENTROS");
        ncenters = in.nextInt();

        System.out.println("Introduce SEMILLA");
        seed = in.nextInt();

        System.out.println("Elige el ALGORITMO PARA GENERAR EL ESTADO INICIAL");
        showOptions();
        inicializadora = in.nextInt();

        System.out.println("Introduce FUNCION SUCESORA");
        showSuccessors();
        successor = in.nextInt();

        RedState board = new RedState(nsensors, ncenters, seed, inicializadora);

        System.out.println(board);

        switch (successor) {
            case 1 -> {
                RedSensorHillClimbingSearch1(board);
                RedSimulatedAnnealingSearch1(board);
            }
            case 2 -> {
                RedSensorHillClimbingSearch2(board);
                RedSimulatedAnnealingSearch2(board);
            }
        }
    }

    private static void RedSensorHillClimbingSearch1(RedState board) {
        System.out.println("\nTSP HillClimbing  -->");
        try {
            Problem problem = new Problem(board, new RedFirstSuccessorFunction(), new RedGoalTest(), new RedHeuristicFunction());
            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RedSensorHillClimbingSearch2(RedState board) {
        System.out.println("\nTSP HillClimbing  -->");
        try {
            Problem problem = new Problem(board, new RedSecondSuccessorFunction(), new RedGoalTest(), new RedHeuristicFunction());
            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RedSimulatedAnnealingSearch1(RedState board) {
        System.out.println("\nTSP Simulated Annealing  -->");
        try {
            Problem problem = new Problem(board, new RedFirstSuccessorFunctionSA(), new RedGoalTest(), new RedHeuristicFunction());
            SimulatedAnnealingSearch search = new SimulatedAnnealingSearch(2000, 100, 5, 0.001);
            //search.traceOn();
            SearchAgent agent = new SearchAgent(problem, search);

            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RedSimulatedAnnealingSearch2(RedState board) {
        System.out.println("\nTSP Simulated Annealing  -->");
        try {
            Problem problem = new Problem(board, new RedSecondSuccessorFunctionSA(), new RedGoalTest(), new RedHeuristicFunction());
            SimulatedAnnealingSearch search = new SimulatedAnnealingSearch(2000, 100, 5, 0.001);
            //search.traceOn();
            SearchAgent agent = new SearchAgent(problem, search);

            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }

}
