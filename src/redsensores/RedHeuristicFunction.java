package redsensores;

import aima.search.framework.HeuristicFunction;

public class RedHeuristicFunction implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object state) {
        RedBoard board = (RedBoard) state;
        return board.getCostOfBoard();
    }
}
