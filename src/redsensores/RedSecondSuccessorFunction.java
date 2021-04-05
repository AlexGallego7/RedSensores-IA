package redsensores;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class RedSecondSuccessorFunction implements SuccessorFunction {
    @Override
    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedState board = (RedState) aState;
        RedHeuristicFunction RHF = new RedHeuristicFunction();

        for (int i = 0; i < board.getSens().size(); i++) {
            // Sensor j es el destino
            for (int j = 0; j < board.getSens().size() + board.getCds().size(); j++) {

                // No se envie a él mismo, el sensor j esté disponible.
                // Por eficiencia: Comprobar que ya no este conectado al sensor j ya, y que no se cree un ciclo entre estos dos.
                if (i != j && board.isAvailable(j) && board.getConnections()[i] != j && board.TwoSensGDA(i, j)) {

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
            }
        }
        return (retVal);
    }
}
