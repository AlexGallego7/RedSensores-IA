package redsensores;

import aima.search.framework.HeuristicFunction;

public class RedHeuristicFunction implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object state) {
        RedState board = (RedState) state;

        return board.recalcular_cost() - board.recalcular_data();
    }
}
