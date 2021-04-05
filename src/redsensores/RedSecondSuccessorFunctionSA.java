package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedSecondSuccessorFunctionSA implements SuccessorFunction {
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

        if (board.isAvailable(j) && board.getConnections()[i] != j && board.TwoSensGDA(i, j)) {

            RedState newBoard = new RedState(board);

            newBoard.connectTo(i, j);

            //restriccion GDA + sensor llega a un centro directamente o inderectamente.
            if (newBoard.isValid()) {

                double v = RHF.getHeuristicValue(newBoard);
                String S;
                if (j < board.getSens().size()) {
                    S = "---> Enviar " + i + " al Sensor " + j + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();
                } else {
                    S = "---> Enviar " + i + " al Centro " + (j - board.getSens().size()) + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();
                }
                //System.out.println(S);
                retVal.add(new Successor(S, newBoard));
            }
        }

        return retVal;
    }
}
