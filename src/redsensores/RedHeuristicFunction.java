package redsensores;


import aima.search.framework.HeuristicFunction;

import static java.lang.Math.pow;

public class RedHeuristicFunction implements HeuristicFunction {


    @Override
    public double getHeuristicValue(Object state) {//sumamos el coste total + la perdida de datos


        RedState board = (RedState) state;
        Double maxCost, maxData;
        maxCost = board.getSens().size()*20000*board.getTotal_Data();
        maxData = board.getTotal_Data();
        return (((board.recalculate_cost()*100)/maxCost) - ((board.data_recived()*100)/maxData));
    }
}
