package redsensores;

import aima.search.framework.HeuristicFunction;

public class RedHeuristicFunction implements HeuristicFunction {


    @Override
    public double getHeuristicValue(Object state) {//sumamos el coste total + la perdida de datos
        RedState board = (RedState) state;
        return board.recalculate_cost() + board.recalculate_data() - board.data_recived();//rehaxer las funciones
    }
}
