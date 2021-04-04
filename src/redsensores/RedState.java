package redsensores;

import IA.Red.CentrosDatos;
import IA.Red.Sensores;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.*;


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

    private int[] connections;

    private int[] dataSent;


    public RedState(int nsens, int ncds, int seed, int option) {

        sens = new Sensores(nsens, seed);
        cds = new CentrosDatos(ncds, seed);

        sparse_matrix = new int[sens.size()][sens.size() + cds.size()];
        connections = new int[sens.size()];
        dataSent = new int[sens.size()];

        switch (option) {
            case 2 -> initial_solution_4in4();
            case 3 -> initial_solution_3();
            default -> initial_solution_easy();
        }
    }

    public RedState(RedState oldState) {

        sens = oldState.getSens();
        cds = oldState.getCds();

        sparse_matrix = new int[sens.size()][sens.size() + cds.size()];
        for (int i = 0; i < sens.size(); ++i) {
            sparse_matrix[i] = oldState.sparse_matrix[i].clone();
        }
        connections = oldState.getConnections().clone();
        dataSent = oldState.getDataSent().clone();

    }

    private void initial_solution_easy() {

        //llenar al el primer centro, luego el siguente. SI TODOS los centros estan llenos, EL SENSOR sin ser assignado LE ENVIARA AL PRIMER SENSOR que este llibre

        int nsens = sens.size();
        for (int i = 0; i < nsens; ++i) {
            boolean collocated = false;
            for (int j = 0; !collocated && j < cds.size(); ++j) {
                if (centerIsFree(j)) {
                    sparse_matrix[i][sens.size() + j] = (int) sens.get(i).getCapacidad();
                    connections[i] = sens.size() + j;

                    collocated = true;
                }
            }
            if (!collocated) {
                for (int j = 0; j < sens.size() && !collocated; ++j) {
                    if (sensorIsFree(j) && j != i) {
                        sparse_matrix[i][j] = (int) sens.get(i).getCapacidad();
                        connections[i] = j;
                        collocated = true;
                    }
                }
            }
        }
    }

    private void initial_solution_4in4() {
        boolean fi = false;
        for (int i = 0; i < sens.size() && !fi; i += 4) {
            boolean collocated = false;
            for (int j = 0; !collocated && j < cds.size(); ++j) {
                if (centerIsFree(j)) {
                    sparse_matrix[i][sens.size() + j] = (int) sens.get(i).getCapacidad();
                    connections[i] = sens.size() + j;

                    collocated = true;
                }
            }
            if (!collocated) {
                fi = true;
                for (int k = i; k < sens.size(); ++k) {
                    collocated = false;
                    for (int j = 0; j < sens.size() && !collocated; ++j) {
                        if (sensorIsFree(j) && j != k) {
                            sparse_matrix[k][j] = (int) sens.get(k).getCapacidad();
                            connections[k] = j;
                            collocated = true;
                        }
                    }
                }
            } else {
                for (int j = i + 1; j < sens.size() && j < i + 4; ++j) {
                    sparse_matrix[j][i] = (int) sens.get(j).getCapacidad();
                    connections[j] = i;
                }
            }
        }
    }

    private void initial_solution_3() {

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


    // Operadores
    public void swap_connection(int i, int j) {
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

    // PRE: i & j < sens.size()
    // a connection hi ha el index del sensor desti, mentre a sparse_matrix hi ha la data que es transmet

    public void swapRaro(int i, int j) {
        int targetI, targetJ;
        targetI = connections[i];
        targetJ = connections[j];

        connections[i] = targetJ;
        connections[j] = i;

        sparse_matrix[i][targetI] = 0;
        sparse_matrix[i][targetJ] = (int) sens.get(i).getCapacidad();

        sparse_matrix[j][targetJ] = 0;
        sparse_matrix[j][i] = (int) sens.get(j).getCapacidad();
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

    //First Successor funcion
    public boolean isSwappable(int i, int j) {
        return connections[j] != i && connections[i] != j && connections[j] != connections[i];
    }
    //SECOND Successor funcion
    public boolean isAvailable(int i, int j) {
        if(j < sens.size()){
            //return SensIsFree(j) + 1 < 4
        }
        else{
            //return (SensIsFree(j) + 1) < 26
        }
        return true;
    }

    /*public boolean isSwappableRaro(int i, int j) {
        return connections[j] != i && sensorIsFree(i);
    }*/

    public boolean isValid() {

        int[] arrivaAunCentre = new int[sens.size()];
        for (int i = 0; i < sens.size() && arrivaAunCentre[i] == 0; ++i) {
            int[] visited = new int[sens.size()];
            visited[i] = 1;
            int target = connections[i];
            while (true) {
                if (target >= sens.size()) {
                    arrivaAunCentre[i] = 1;
                    for (int j = 0; j < visited.length; ++j) {
                        if (visited[j] == 1) {
                            arrivaAunCentre[j] = 1;
                        }
                    }
                    break;
                }
                if (visited[target] == 1) return false;
                if (arrivaAunCentre[target] == 1) {
                    for (int j = 0; j < visited.length; ++j) {
                        if (visited[j] == 1) {
                            arrivaAunCentre[j] = 1;
                        }
                    }
                    break;
                }
                visited[target] = 1;
                target = connections[target];
            }
        }
        return true;
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2));
    }

    public Double cost(double distance, double data) {
        return pow(distance, 2) * data;
    }

    public double recalculate_cost() {

        double x = recalculate_data();

        double cost = 0;
        for (int i = 0; i < connections.length; ++i) {
            int x1 = sens.get(i).getCoordX();
            int y1 = sens.get(i).getCoordY();
            int y = connections[i];
            int x2, y2;
            if (y < sens.size()) {
                x2 = sens.get(y).getCoordX();
                y2 = sens.get(y).getCoordY();
            } else {
                x2 = cds.get(y - sens.size()).getCoordX();
                y2 = cds.get(y - sens.size()).getCoordY();
            }
            cost += cost(distance(x1, y1, x2, y2), dataSent[i]);
        }
        return cost;
    }

    public int howMuchTransfer(int x) {
        int data = 0;
        int count = 0;
        int capX = (int) sens.get(x).getCapacidad();
        for (int i = 0; count < 4 && i < connections.length; ++i) {
            if (connections[i] == x) {
                ++count;
                int data2 = howMuchTransfer(i);
                if (data + data2 <= capX * 2) {
                    data += data2;
                } else data = capX * 2;
            }
        }
        dataSent[x] = data + capX;
        return data + capX;
    }

    public double recalculate_data() {
        double data = 0;
        int count;
        for (int i = 0; i < cds.size(); ++i) {
            count = 0;
            for (int j = 0; count < 26 && j < connections.length; ++j) {
                if (connections[j] == sens.size() + i) {
                    ++count;
                    int data2 = howMuchTransfer(j);
                    if (data2 <= 150) {
                        data += data2;
                        if (data > 150) data = 150;
                    }
                }
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

    public int[] getDataSent() {
        return dataSent;
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
        return s.toString();
    }
}
