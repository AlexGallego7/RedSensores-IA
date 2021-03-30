package redsensores;

import aima.search.framework.HeuristicFunction;

public class RedHeuristicFunction implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object state) {
        RedState board = (RedState) state;

        return board.recalculate_cost() - board.recalculate_data();
    }
}
