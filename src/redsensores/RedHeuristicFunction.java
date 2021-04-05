package redsensores;

import aima.search.framework.HeuristicFunction;

import static java.lang.Math.pow;

public class RedHeuristicFunction implements HeuristicFunction {
    @Override
    public double getHeuristicValue(Object state) {//sumamos el coste total + la perdida de datos
        RedState board = (RedState) state;
        double cost, recived;
        cost = board.recalculate_cost();
        recived = board.data_received();

        int lenghtCost = String.valueOf(cost).length();
        int lenghtRecived= String.valueOf(recived).length();
        int lenghtTotal = String.valueOf(board.getTotal_Data()).length();

        //data enviada por sensores - data recivida
        return -(cost - recived*(pow(10,lenghtCost - lenghtRecived)));
    }
}
