package redsensores;

import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;
import aima.util.Pair;

import java.util.Arrays;
import java.util.HashMap;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class RedState {

    private static Sensores sens;
    private static CentrosDatos cds;

    // Cantidad de información transmitida
    private static int total_data;

    // Coste total
    private static double total_cost;

    // cada valor del indice es el indice del sensor receptor i si el receptor es un CentroDeDatos el indice es nsens+indice
    private int[] connection_map;

    // Por cada sensor se dice cuanto MB transmite o recibe
    private int[] sensors_data;

    //Un vector para decir el coste de cada sensor envez del de arriba?
    //Un vector para indicar cuantos MB recibe un centro de datos?

    //las siguientes son para cache. se puede calcular atraves de connection_map

    // Cada indice indica cuantos conexiones tiene conectado al sensor.
    private int[] sensors_connections;

    // Index = index de CentroDatos cds, valor de cada indice son el numero de sensores conectados al centro de dato
    private int[] centers_connections;

    public RedState(int nsens, int ncds, int seed, int option) {

        sens = new Sensores(nsens, seed);
        cds = new CentrosDatos(ncds, seed);

        total_data = 0;
        total_cost = 0;

        sensors_data = new int[nsens];
        Arrays.fill(sensors_data, 0);

        connection_map = new int[nsens];

        sensors_connections = new int[nsens];
        Arrays.fill(sensors_connections, 0);

        centers_connections = new int[ncds];
        Arrays.fill(centers_connections, 0);

        if (option == 1) {
            initial_solution_1();
        } else {
            initial_solution_2();
        }
    }

    private void initial_solution_1(){

        // SOLUCION INICIAL

        // 1 -> Cada sensor transmite su información a su centro mas cercano.

        //mirar las restricciones

        /*for(int j = 1; j <= sens.size(); ++j) {
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
            //init.put(j, new Pair(n, cost));
        }*/
    }

    private void initial_solution_2() {

        int nsens = sens.size();
        for(int i = 0; i < nsens; ++i){
            boolean collocated = false;
            //se puede colocar a un centro random en vez de color los 25 primeros en el primer centro.
            for(int j = 0; !collocated && j < cds.size(); ++j){
                if(centers_connections[j] < 25){
                    connection_map[i] = nsens + j;
                    centers_connections[j] += 1;
                    sensors_data[i] = (int) sens.get(i).getCapacidad();
                    total_data += sens.get(i).getCapacidad();

                    double dist = distance(sens.get(i).getCoordX(), sens.get(i).getCoordY(),
                            cds.get(j).getCoordX(), cds.get(j).getCoordY());

                    total_cost += cost(dist, sens.get(i).getCapacidad());
                    collocated = true;
                }
            }
        }

    }

    public int[] getConnection_map() {
        return connection_map;
    }

    public int[] getSensors_data() {
        return sensors_data;
    }

    public int[] getSensors_connections() {
        return sensors_connections;
    }

    public int[] getCenters_connections() {
        return centers_connections;
    }

    public double getDataOfBoard() {
        return total_data;
    }

    public double getCostOfBoard() {
        return total_cost;
    }

    // Operadores

    /*public void next_sensor(int i) { // Evitar ciclos
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
    }*/

    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1-x2), 2) + pow((y1-y2), 2));
    }

    public Double cost(double distance, double data) {
        return pow(distance, 2) * data;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < sens.size(); ++i) {
            s.append("Sensor " + (i+1) + ": (Receptor: " + (connection_map[i]-sens.size()+1) + ", transmite: " + sensors_data[i] + ")\n");
        }
        for(int i = 0; i < cds.size(); ++i) {
            s.append("CentroDeDatos " + (i+1) + ": (" + centers_connections[i]+")\n");
        }

        s.append("Coste total: " + total_cost + ")\n");
        s.append("Datos transferidos: " + total_data + ")\n");
        return s.toString();
    }
}
