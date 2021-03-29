package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class RedSuccessorFunction implements SuccessorFunction {
    @Override
    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedState board  = (RedState) aState;
        RedHeuristicFunction TSPHF  = new RedHeuristicFunction();

        for (int i = 0; i < board.getSens().size(); i++) {

            for (int j = i + 1; j < board.getSens().size(); j++) {
                RedState newBoard = new RedState(board);

                newBoard.swap_connections(i, j);

                double    v = TSPHF.getHeuristicValue(newBoard);
                String S = "Intercambio " + i + " " + j + " Coste(" + v + ") ---> " + newBoard.toString();

                retVal.add(new Successor(S, newBoard));
            }
        }

        return retVal;
    }
}
