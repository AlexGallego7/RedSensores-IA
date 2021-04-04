package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class RedFirstSuccessorFunction implements SuccessorFunction {
    @Override
    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedState board = (RedState) aState;
        RedHeuristicFunction RHF = new RedHeuristicFunction();

        for (int i = 0; i < board.getSens().size(); i++) {

            for (int j = i+1; j < board.getSens().size(); j++) {

                RedState newBoard = new RedState(board);

                // isSwappable es el mismo que GDA.
                if(newBoard.isSwappable(i, j)){

                    newBoard.swap_connection(i, j);

                    //restriccion GDA + sensor llega a un centro directamente o inderectamente.
                    if (newBoard.isValid()) {

                        double v = RHF.getHeuristicValue(newBoard);
                        String S = "---> Intercambio " + i + " " + j + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();

                        //System.out.println(S);

                        retVal.add(new Successor(S, newBoard));
                    }
                }
            }
        }
        return (retVal);
    }
}
