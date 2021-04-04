package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class RedThirdSuccessorFunction implements SuccessorFunction {
    @Override
    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedState board = (RedState) aState;
        RedHeuristicFunction RHF = new RedHeuristicFunction();

        for (int i = 0; i < board.getSens().size(); i++) {

            for (int j = 0; j < board.getSens().size(); j++) {
                if (i != j) {

                    RedState newBoard = new RedState(board);

                    // isSwappable es el mismo que GDA.
                    if(newBoard.isSwappable(i, j)){

                        newBoard.swapRaro(i, j);

                        //restriccion GDA + sensor llega a un centro directamente o inderectamente.
                        if (newBoard.isValid()) {

                            double v = RHF.getHeuristicValue(newBoard);
                            String S = "---> Intercambio " + i + " " + j + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();

                            //System.out.println(s);

                            retVal.add(new Successor(S, newBoard));
                        }
                    }
                }
            }
        }
        return (retVal);
    }
}