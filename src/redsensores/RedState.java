package redsensores;

import IA.Red.CentrosDatos;
import IA.Red.Sensores;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class RedState {

    private static Sensores sens;
    private static CentrosDatos cds;
    private static int[] SortedByDist;

        /*

        S1  S2  S3  S4  C1  C2
    S1  0   1   0   0   0   0
    S2  ...
    S3
    S4
     */

    private int[][] sparse_matrix;

    private int[] connections;


    // Cantidad de información transmitida
    private double total_data;

    public RedState(int nsens, int ncds, int seed, int option) {

        sens = new Sensores(nsens, seed);
        cds = new CentrosDatos(ncds, seed);

        sparse_matrix = new int[sens.size()][sens.size() + cds.size()];

        connections = new int[sens.size()];

        switch (option){
            case 1:
                initial_solution_1();
                break;
            case 2:
                initial_solution_2();
                break;
            case 3:
                initial_solution_3();
                break;
            default:
                initial_solution_1();
        }

    }

    public RedState(RedState oldState) {

        sens = oldState.getSens();
        cds = oldState.getCds();
        sparse_matrix = oldState.getSparse_matrix().clone();
        connections = oldState.getConnections().clone();
    }



    private void initial_solution_1() {

        // SOLUCION INICIAL

        // 1 -> Cada sensor transmite su información a su centro con menos coste.

        //mirar las restricciones

        for (int i = 0; i < sens.size(); ++i) {
            double min = Double.MAX_VALUE;
            double cost;
            int n = 0;
            for (int j = 0; j < cds.size(); ++j) {
                if (centerIsFree(j)) {
                    double dist = distance(sens.get(i).getCoordX(), sens.get(i).getCoordY(),
                            cds.get(j).getCoordX(), cds.get(j).getCoordY());
                    cost = cost(dist, sens.get(i).getCapacidad());

                    if (cost < min) {
                        min = cost;
                        n = j;
                    }
                }
            }
            if (centerIsFree(n)) {
                sparse_matrix[i][sens.size() + n] = (int) sens.get(i).getCapacidad();
                connections[i] = sens.size() + n;
            }
        }

    }

    private void initial_solution_2() {
        int nsens = sens.size();
        for (int i = 0; i < nsens; ++i) {
            boolean collocated = false;
            //se puede colocar a un centro random en vez de color los 25 primeros en el primer centro.
            for (int j = 0; !collocated && j < cds.size(); ++j) {
                if (centerIsFree(j)) {
                    total_data += sens.get(i).getCapacidad();
                    sparse_matrix[i][sens.size() + j] = (int) sens.get(i).getCapacidad();
                    connections[i] = sens.size() + j;

                    double dist = distance(sens.get(i).getCoordX(), sens.get(i).getCoordY(),
                            cds.get(j).getCoordX(), cds.get(j).getCoordY());
                    collocated = true;
                }
            }
        }
    }

    //cada sensor envia a su punto mas cercano available, assegurando el GDA y que todas las conexiones llegan a un centro
    private void initial_solution_3(){

        SortedByDist =  new int[sens.size() + cds.size()];
        fillSorted();

        return;
    }


    // Operadores

    // PRE: i & j < sens.size()
    // a connection hi ha el index del sensor desti, mentre a sparse_matrix hi ha la data que es transmet
    public void swap_connections(int i, int j) {
        int targetI, targetJ;
        targetI = connections[i];
        targetJ = connections[j];

        connections[i] = targetJ;
        connections[j] = targetI;

        sparse_matrix[i][targetI] = 0;
        sparse_matrix[i][targetJ] = (int) sens.get(i).getCapacidad();

        sparse_matrix[j][targetJ] = 0;
        sparse_matrix[j][targetI] = (int) sens.get(j).getCapacidad();
    }

// otras funciones


    private void fillSorted(){
        Boolean visited[] = new Boolean[sens.size() + cds.size()];
        Arrays.fill(visited,false);

        int[] coords = new int[2];
        SortedByDist[0] = findClosestAvailable(0,0,visited);
        visited[SortedByDist[0]] = true;

        for(int i = 1; i < sens.size() + cds.size();++i){

            SortedByDist[i] = findClosestAvailable(coords[0],coords[1],visited);
            coords = getElemCoords(coords[0],coords[1],i);
            visited[SortedByDist[i]] = true;
        }
    }

    private int[] getElemCoords(int x, int y, int elem){//pone en x e y las coordenadas del elmento, sea sens o cds.
        int[] coords = new int[2];
        if(isSensor(elem)){
            x = sens.get(elem).getCoordX();
            y = sens.get(elem).getCoordY();
        }else {
            x = cds.get(elem - sens.size()).getCoordX();
            y = cds.get(elem - sens.size()).getCoordY();
        }
         coords[0] = x;
        coords[1] = y;
        return coords;
    }

    private int findClosestAvailable(int x, int y, Boolean visited[]){//dadas unas coordenadas, retorna el elemento mas cercano i free
                                                    // en caso de no haber elementos free, debuelve -1
        double dist = Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        int elem = -1;
        int[] coords = new int[2];
        for (int i = 0; i < sens.size() + cds.size(); ++i){
            if (!visited[i]) { // and isAvailable
                coords = getElemCoords(coords[0], coords[1], i);
                dist = distance(x, y, coords[0], coords[1]);
                if (dist < min) {
                    min = dist;
                    elem = i; //// mejor elem hasta ahora
                }
            }

        }
        return elem;
    }

    private Boolean isSensor(int elem){//true => is Sesnor || false => is Center
        if(elem < sens.size()) return true;
        return false;
    }


    private boolean isAvailabe(int elem){
        if(isSensor(elem)) return sensorIsFree(elem);
        return centerIsFree(elem - sens.size());
    }

    private boolean centerIsFree(int j) {
        int n = 0;
        for (int i = 0; i < sparse_matrix.length; ++i) {
            if (sparse_matrix[i][sens.size() + j] > 0) ++n;
            if (n >= 25) return false;
        }
        return true;
    }

    private boolean sensorIsFree(int j) {
        int n = 0;
        for (int i = 0; i < sparse_matrix.length; ++i) {
            if (sparse_matrix[i][j] > 0) ++n;
            if (n >= 3) return false;
        }
        return true;
    }

    public boolean isGDA() {
        //si no hay perdida de datos seguro que no hay ciclos
        if(recalculate_data() - data_recived() == 0)  return true;

        return true;

    }

    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2));
    }

    public Double cost(double distance, double data) {
        return pow(distance, 2) * data;
    }

    public double recalculate_cost() {
        double cost = 0;
        for (int i = 0; i < sparse_matrix.length; ++i) {
            for (int j = 0; j < sparse_matrix[0].length; ++j) {
                if (sparse_matrix[i][j] == 1) {
                    int x1 = sens.get(i).getCoordX();
                    int y1 = sens.get(i).getCoordY();
                    int x2, y2;
                    if (j < sens.size()) {
                        x2 = sens.get(j).getCoordX();
                        y2 = sens.get(j).getCoordY();
                    } else {
                        x2 = cds.get(j - sens.size()).getCoordX();
                        y2 = cds.get(j - sens.size()).getCoordY();
                    }
                    cost += cost(distance(x1, y1, x2, y2), sens.get(i).getCapacidad());
                }
            }
        }
        return cost;
    }

    public double recalculate_data() {
        double data = 0;
        for (int i = 0; i < sparse_matrix.length; ++i) {
            data += Arrays.stream(sparse_matrix[i]).sum();
        }
        total_data = data;
        return data;
    }
    public double data_recived(){//los datos recividos por los centros
        double data = 0;
        for (int i = sens.size(); i < sparse_matrix[0].length; ++i){
            for(int j = 0; j < sparse_matrix.length; ++j){
                data += sparse_matrix[j][i];
            }
        }
        return data;
    }

    // GETTERS

    public Sensores getSens() {
        return sens;
    }

    public CentrosDatos getCds() {
        return cds;
    }

    public int[][] getSparse_matrix() {
        return sparse_matrix;
    }

    public int[] getConnections() {
        return connections;
    }

    // OUTPUT

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("  ");
        for (int i = 0; i < sens.size(); ++i)
            s.append("S" + i + " ");
        for (int j = 0; j < cds.size(); ++j)
            s.append("C" + j + " ");

        s.append("\n");

        for (int i = 0; i < sparse_matrix.length; ++i) {
            s.append("S" + i + " ");
            for (int j = 0; j < sparse_matrix[0].length; ++j) {
                s.append(sparse_matrix[i][j] + "  ");
            }
            s.append("\n");
        }
        s.append("Connection map: ");
        for (int i = 0; i < connections.length; ++i) {
            s.append("S" + i + " -> " + connections[i]);
            if (i < connections.length - 1) s.append(", ");
        }
        s.append("\n");
        s.append("Coste total: " + recalculate_cost() + "\n");
        s.append("Datos transferidos: " + recalculate_data() + "\n");
        s.append("datos perdidos: " + (recalculate_data() - data_recived()) + "\n");


        return s.toString();
    }
}
