package redsensores;

import IA.Red.CentrosDatos;
import IA.Red.Sensores;

import java.util.Arrays;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class RedState {

    private static Sensores sens;
    private static CentrosDatos cds;

        /*

        S1  S2  S3  S4  C1  C2
    S1  0   1   0   0   0   0
    S2  ...
    S3
    S4
     */

    private int[][] sparse_matrix;

    private int[] map;

    // Cantidad de información transmitida
    private double total_data;

    // Coste total
    private double total_cost;


    public RedState(int nsens, int ncds, int seed, int option) {

        sens = new Sensores(nsens, seed);
        cds = new CentrosDatos(ncds, seed);

        sparse_matrix = new int[sens.size()][sens.size()+cds.size()];

        map = new int[sens.size()];

        if (option == 1) {
            initial_solution_1();
        } else {
            initial_solution_2();
        }
    }

    public RedState(RedState oldState) {

        sens = oldState.getSens();
        cds = oldState.getCds();

        sparse_matrix = oldState.getSparse_matrix();
        map = oldState.getMap();

    }

    private void initial_solution_1(){

        // SOLUCION INICIAL

        // 1 -> Cada sensor transmite su información a su centro con menos coste.

        //mirar las restricciones

        for(int i = 0; i < sens.size(); ++i) {
            double min = Double.MAX_VALUE;
            double _cost = Double.MAX_VALUE;
            int n = 0;
            for(int j = 0; j < cds.size(); ++j) {
                if(valid(j)) {
                    double dist = distance(sens.get(i).getCoordX(), sens.get(i).getCoordY(),
                            cds.get(j).getCoordX(), cds.get(j).getCoordY());
                    _cost = cost(dist, sens.get(i).getCapacidad());

                    if (_cost < min) {
                        min = _cost;
                        n = j;
                    }
                }
            }
            if(valid(n))
                sparse_matrix[i][n+sens.size()] = (int) sens.get(i).getCapacidad();
        }
    }

    private boolean valid(int j) {
        int n = 0;
        for(int i = 0; i < sparse_matrix.length; ++i) {
            if(sparse_matrix[i][sens.size() + j] > 0)
                ++n;
            if(n >= 25) {
                return false;
            }
        }
        return true;
    }

    private void initial_solution_2() {

        int nsens = sens.size();
        for(int i = 0; i < nsens; ++i){
            boolean collocated = false;
            //se puede colocar a un centro random en vez de color los 25 primeros en el primer centro.
            for(int j = 0; !collocated && j < cds.size(); ++j){
                if(valid(j)){
                    total_data += sens.get(i).getCapacidad();

                    double dist = distance(sens.get(i).getCoordX(), sens.get(i).getCoordY(),
                            cds.get(j).getCoordX(), cds.get(j).getCoordY());

                    total_cost += cost(dist, sens.get(i).getCapacidad());
                    collocated = true;
                }
            }
        }

    }

    // Operadores


    public void swap_connections(int i, int j) {
        int tmp;
        tmp = map[i];
        map[i] = map[j];
        map[j] = tmp;
    }

    public double recalcular_cost() {
        double cost = 0;
        for(int i = 0; i < sparse_matrix.length; ++i) {
            for(int j = 0; j < sparse_matrix[0].length; ++j) {
                if(sparse_matrix[i][j] == 1) {
                    int x1 = sens.get(i).getCoordX();
                    int y1 = sens.get(i).getCoordY();
                    int x2, y2;
                    if(j < sens.size()) {
                        x2 = sens.get(j).getCoordX();
                        y2 = sens.get(j).getCoordY();
                    }
                    else {
                        x2 = cds.get(j - sens.size()).getCoordX();
                        y2 = cds.get(j - sens.size()).getCoordY();
                    }
                    cost += cost(distance(x1, y1, x2, y2), sens.get(i).getCapacidad());
                }
            }
        }
        return cost;
    }

    public double recalcular_data() {
        double data = 0;
        for(int i = 0; i <  sparse_matrix.length; ++i) {
            data += Arrays.stream(sparse_matrix[i]).sum();
        }
        return data;
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1-x2), 2) + pow((y1-y2), 2));
    }

    public Double cost(double distance, double data) {
        return pow(distance, 2) * data;
    }

    // GETTERS

    public Sensores getSens() { return sens; }

    public CentrosDatos getCds() { return cds; }

    public int[][] getSparse_matrix() { return sparse_matrix; }

    public int[] getMap() { return map; }

    // OUTPUT

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("  ");
        for(int i = 0; i < sens.size(); ++i)
            s.append("S"+i+" ");
        for(int j = 0; j < cds.size(); ++j)
            s.append("C"+j+" ");

        s.append("\n");

        for(int i = 0; i < sparse_matrix.length; ++i) {
                s.append("S"+i+" ");
            for(int j = 0; j < sparse_matrix[0].length; ++j) {
                s.append(sparse_matrix[i][j] + "  ");
            }
            s.append("\n");
        }
        s.append("Coste total: " + recalcular_cost() + "\n");
        s.append("Datos transferidos: " + recalcular_data() + "\n");
        return s.toString();
    }
}
