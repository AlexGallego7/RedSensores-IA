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

            for (int j = 0; j < board.getSens().size(); j++) {
                if (i != j) {

                    RedState newBoard = new RedState(board);

                    // isSwappable es el mismo que GDA.
                    if (newBoard.isSwappable(i, j) && newBoard.isGDA()) {

                        newBoard.swap_connections(i, j);

                        double v = TSPHF.getHeuristicValue(newBoard);
                        String s = "---> Intercambio " + i + " " + j + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();

                        System.out.println(s);

                        retVal.add(new Successor(s, newBoard));
                    }
                }
            }
        }
        return retVal;
    }
}
