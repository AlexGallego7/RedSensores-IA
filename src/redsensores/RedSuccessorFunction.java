package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class RedSuccessorFunction implements SuccessorFunction {
    @Override
    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedState board = (RedState) aState;
        RedHeuristicFunction TSPHF = new RedHeuristicFunction();

        for (int i = 0; i < board.getSens().size(); i++) {

            for (int j = i + 1; j < board.getSens().size(); j++) {
                RedState newBoard = new RedState(board);

                System.out.println("-----AVANS DEL SWAP------");

                System.out.println(newBoard.toString());

                newBoard.swap_connections(i, j);

                if (newBoard.isGDA()) {
                    double v = TSPHF.getHeuristicValue(newBoard);
                    String s = "---> Intercambio " + i + " " + j + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();

                    System.out.println(s);

                    retVal.add(new Successor(s, newBoard));
                }
            }
        }
        return retVal;
    }
}