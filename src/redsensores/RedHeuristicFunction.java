package redsensores;


import aima.search.framework.HeuristicFunction;

import static java.lang.Math.pow;

public class RedHeuristicFunction implements HeuristicFunction {


    @Override
    public double getHeuristicValue(Object state) {//sumamos el coste total + la perdida de datos
        RedState board = (RedState) state;
        double cost, recived;
        cost = board.recalculate_cost();
        recived = board.data_recived();

        int lenghtCost = String.valueOf(cost).length();
        int lenghtRecived= String.valueOf(recived).length();
        int lenghtTotal = String.valueOf(board.getTotal_Data()).length();

        return -(cost + board.getTotal_Data()*(pow(10,lenghtCost - lenghtTotal)) - recived*(pow(10,lenghtCost - lenghtRecived)));//data enviada por sensores - data recivida
    }
}
