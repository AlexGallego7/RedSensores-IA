package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedSuccessorFunctionSA implements SuccessorFunction {
    @Override
    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedState board  = (RedState) aState;
        RedHeuristicFunction RHF  = new RedHeuristicFunction();
        Random myRandom=new Random();
        int i,j;

        i=myRandom.nextInt(board.getSens().size());

        do{
            j=myRandom.nextInt(board.getSens().size());
        } while (i==j);


        RedState newBoard = new RedState(board);

        newBoard.swapRaro(i, j);

        double v = RHF.getHeuristicValue(newBoard);
        String S = "---> Intercambio " + i + " " + j + " HEURISTICA: " + v + " <--- " + "\n" + newBoard.toString();

        retVal.add(new Successor(S, newBoard));

        return retVal;
    }
}
