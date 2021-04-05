package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedFirstSuccessorFunctionSA implements SuccessorFunction {
    @Override
    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedState board = (RedState) aState;
        RedHeuristicFunction RHF = new RedHeuristicFunction();
        Random myRandom = new Random();
        int i, j;

        i = myRandom.nextInt(board.getSens().size());

        do {
            j = myRandom.nextInt(board.getSens().size());
        } while (i == j);

        RedState newBoard = new RedState(board);

        if (newBoard.isSwappable(i, j)) {
            newBoard.swap_connection(i, j);
            if (newBoard.isValid()) {
                double v = RHF.getHeuristicValue(newBoard);
                String S = "---> Intercambio " + i + " " + j + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();

                System.out.println(S);

                retVal.add(new Successor(S, newBoard));
            }
        }
        return retVal;
    }
}
