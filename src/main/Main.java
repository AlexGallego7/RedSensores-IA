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

public class Main {

    public static void main(String[] args) throws Exception {
        RedState board = new RedState(100, 4, 1234, 3);
        System.out.println(board);
        RedSensorHillClimbingSearch(board);
        //RedSimulatedAnnealingSearch(board);

    }

    private static void RedSensorHillClimbingSearch(RedState board) {
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

    private static void RedSimulatedAnnealingSearch(RedState board) {
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
