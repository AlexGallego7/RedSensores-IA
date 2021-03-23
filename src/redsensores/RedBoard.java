package redsensores;

import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;
import aima.util.Pair;

import java.util.HashMap;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class RedBoard {

    // <Sensor origen, <sensor destino, coste>

    private final HashMap<Integer, Pair> init;

    private static Sensores sens;
    private static CentrosDatos cds;

    public RedBoard(int nsens, int ncds) {

        // SOLUCION INICIAL

        /*

            1 -> Cada sensor transmite su información a su centro mas cercano.

         */

        init = new HashMap<>();
        sens = new Sensores(nsens, 1);
        cds = new CentrosDatos(ncds, 1);

        for(int j = 1; j <= sens.size(); ++j) {
            double min = Integer.MAX_VALUE;
            double cost = Double.MAX_VALUE;
            int n = -1;
            for(int i = 1; i <= cds.size(); ++i) {
                double dist = distance(sens.get(j-1).getCoordX(), sens.get(j-1).getCoordY(),
                        cds.get(i-1).getCoordX(), cds.get(i-1).getCoordY());
                if(dist < min) {
                    min = dist;
                    cost = cost(dist, sens.get(j-1).getCapacidad());
                    n = i;
                }
            }
            init.put(j, new Pair(n, cost));
        }
    }

    public void next_sensor(int i) {
        Sensor s = sens.get(i);
        Double cost = (Double) init.get(i).getSecond();
        int n = -1;
        for(int j = 0; j < init.size(); ++i) {
            Double new_cost = cost(distance(s.getCoordX(), s.getCoordY(), sens.get(j).getCoordX(),
                    sens.get(j).getCoordY()), s.getCapacidad());
            if(new_cost < cost) {
                cost = new_cost;
                n = j;
            }
        }
        init.put(n, new Pair(n, cost));
    }

    public Double cost(double distance, double data) {
        return pow(distance, 2) * data;
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1-x2), 2) + pow((y1-y2), 2));
    }

    public double getCostOfBoard() {
        double sum = 0;
        for(int i = 0; i < init.size(); ++i) {
            sum += (double) init.get(i).getSecond();
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < init.size(); ++i) {
            s.append("Sensor " + (i+1) + ": (" + init.get(i+1).getFirst() + ", " + init.get(i+1).getSecond() + ")\n");
        }
        return s.toString();
    }
}
