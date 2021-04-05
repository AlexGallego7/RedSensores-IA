package redsensores;

import IA.Red.CentrosDatos;
import IA.Red.Sensores;

import java.util.Arrays;

import static java.lang.Math.*;


public class RedState {

    private static Sensores sens;
    private static CentrosDatos cds;
    private static double total_Data;

    private int[] connections;

    private int[] dataSent;


    public RedState(int nsens, int ncds, int seed, int option) {

        sens = new Sensores(nsens, seed);
        cds = new CentrosDatos(ncds, seed);
        total_Data = data_default();

        connections = new int[sens.size()];
        Arrays.fill(connections, -1);
        dataSent = new int[sens.size()];

        switch (option) {
            case 2 -> initial_solution_4in4();
            case 3 -> initial_solution_efficient();
            default -> initial_solution_easy();
        }
    }

    public RedState(RedState oldState) {

        sens = oldState.getSens();
        cds = oldState.getCds();
        total_Data = oldState.getTotal_Data();

        connections = oldState.getConnections().clone();
        dataSent = oldState.getDataSent().clone();

    }

    private void initial_solution_easy() {

        //llenar al el primer centro, luego el siguente. SI TODOS los centros estan llenos, EL SENSOR sin ser assignado LE ENVIARA AL PRIMER SENSOR que este llibre

        int nsens = sens.size();
        for (int i = 0; i < nsens; ++i) {
            boolean collocated = false;
            for (int j = 0; !collocated && j < cds.size(); ++j) {
                if (centerConnections(j) < 25) {
                    connections[i] = sens.size() + j;
                    collocated = true;
                }
            }
            if (!collocated) {
                for (int j = 0; j < sens.size() && !collocated; ++j) {
                    if (sensorConnections(j) < 3 && j != i) {
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
                if (centerConnections(j) < 25) {
                    connections[i] = sens.size() + j;

                    collocated = true;
                }
            }
            if (!collocated) {
                fi = true;
                for (int k = i; k < sens.size(); ++k) {
                    collocated = false;
                    for (int j = 0; j < sens.size() && !collocated; ++j) {
                        if (sensorConnections(j) < 3 && j != k) {
                            connections[k] = j;
                            collocated = true;
                        }
                    }
                }
            } else {
                for (int j = i + 1; j < sens.size() && j < i + 4; ++j) {
                    connections[j] = i;
                }
            }
        }
    }

    private void initial_solution_efficient() {

        int[] SortedByDist = new int[sens.size() + cds.size()];

        fillSorted(SortedByDist);
        conectSorted(SortedByDist);
    }

    // Operadores

    public void swap_connection(int i, int j) {
        //PRE: i & j < sens.size()
        int targetI = connections[i];

        connections[i] = connections[j];
        connections[j] = targetI;
    }

    public void connectTo(int i, int j) {
        connections[i] = j;
    }

    // otras funciones

    private void conectSorted(int[] SortedByDist) {
        int centersPos[] = new int[cds.size()];
        int c = 0;
        for (int i = 0; i < SortedByDist.length; ++i) {//encontramos las posiciones de los centros en el vector Sorted
            if (!isSensor(SortedByDist[i])) {
                centersPos[c] = i;
                c++;
            }
        }
        for (c = 0; c < centersPos.length; c++) {//dividimos el vector en partes, para conectar los mas cercanos
            if (c == 0 && c < cds.size() - 1) { // si es el primer centro pero hay mas
                if (centersPos[c] > 0) {
                    conectGroup(SortedByDist, 0, centersPos[c] - 1, centersPos[c]);// izq
                }
                if (centersPos[c] + 1 < SortedByDist.length) {
                    conectGroup(SortedByDist, (centersPos[c] + centersPos[c + 1]) / 2, centersPos[c] + 1, centersPos[c]); //der
                }
            } else if (c == 0 && c == cds.size() - 1) { // solo hay un centro, conecto todo
                if (centersPos[c] > 0) {
                    conectGroup(SortedByDist, 0, centersPos[c] - 1, centersPos[c]);//izq
                }
                if (centersPos[c] + 1 < SortedByDist.length) {
                    conectGroup(SortedByDist, SortedByDist.length - 1, centersPos[c] + 1, centersPos[c]);//derecha
                }
            } else if (c == cds.size() - 1) { // si es el ultimo centro
                if (centersPos[c] + 1 < SortedByDist.length) {//der
                    conectGroup(SortedByDist, SortedByDist.length - 1, centersPos[c] + 1, centersPos[c]);//derecha
                }
                if (centersPos[c] > 0) {
                    conectGroup(SortedByDist, ((centersPos[c] + centersPos[c - 1]) / 2) + 1, centersPos[c] - 1, centersPos[c]);//izq
                }
            } else { // es un centro del medio
                if (centersPos[c] > 0) {
                    conectGroup(SortedByDist, ((centersPos[c] + centersPos[c - 1]) / 2) + 1, centersPos[c] - 1, centersPos[c]);//izq
                }
                if (centersPos[c] + 1 < SortedByDist.length) {
                    conectGroup(SortedByDist, ((centersPos[c] + centersPos[c + 1]) / 2), centersPos[c] + 1, centersPos[c]);//der
                }
            }

        }

    }

    private void conectGroup(int[] SortedByDist, int i, int j, int c) {//conecta en fila los sensores desde i a j, i el ultimo al centro c
        if (i < j) {//izq
            for (; i < c; ++i) {
                if (isSensor(SortedByDist[i])) {
                    connections[SortedByDist[i]] = SortedByDist[i + 1];
                }
            }
        } else if (i > j && j < SortedByDist.length) {//der
            for (; i > c; --i) {
                if (isSensor(SortedByDist[i])) {
                    connections[SortedByDist[i]] = SortedByDist[i - 1];
                }
            }
        }
    }

    private void fillSorted(int[] SortedByDist) {
        Boolean visited[] = new Boolean[sens.size() + cds.size()];
        Arrays.fill(visited, false);

        int[] coords = new int[2];
        SortedByDist[0] = findClosestAvailable(0, 0, visited);
        visited[SortedByDist[0]] = true;
        coords = getElemCoords(coords[0], coords[1], SortedByDist[0]);

        for (int i = 1; i < sens.size() + cds.size(); ++i) {

            SortedByDist[i] = findClosestAvailable(coords[0], coords[1], visited);
            coords = getElemCoords(coords[0], coords[1], SortedByDist[i]);
            visited[SortedByDist[i]] = true;
        }
    }

    private int[] getElemCoords(int x, int y, int elem) {//pone en x e y las coordenadas del elmento, sea sens o cds.
        int[] coords = new int[2];
        if (isSensor(elem)) {
            x = sens.get(elem).getCoordX();
            y = sens.get(elem).getCoordY();
        } else {
            x = cds.get(elem - sens.size()).getCoordX();
            y = cds.get(elem - sens.size()).getCoordY();
        }
        coords[0] = x;
        coords[1] = y;
        return coords;
    }

    private int findClosestAvailable(int x, int y, Boolean visited[]) {//dadas unas coordenadas, retorna el elemento mas cercano i free
        // en caso de no haber elementos free, debuelve -1
        double dist = Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        int elem = -1;
        int[] coords = new int[2];
        for (int i = 0; i < sens.size() + cds.size(); ++i) {
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

    private Boolean isSensor(int elem) {//true => is Sesnor || false => is Center
        if (elem < sens.size()) return true;
        return false;
    }

    private int centerConnections(int j) {
        int n = 0;
        j = j + sens.size();
        for (int i = 0; i < connections.length; ++i) {
            if (connections[i] == j) ++n;
        }
        return n;
    }

    private int sensorConnections(int j) {
        int n = 0;
        for (int i = 0; i < connections.length; ++i) {
            if (connections[i] == j) ++n;
        }
        return n;
    }
    //First Successor funcion

    public boolean isSwappable(int i, int j) {
        return connections[j] != i && connections[i] != j && connections[j] != connections[i];
    }

    //SECOND Successor funcion, comprueba que el sensor o centro objectivo no este lleno
    public boolean isAvailable(int elem) {
        if (isSensor(elem)) {
            return sensorConnections(elem) < 3;
        } else {
            return centerConnections(elem) < 25;
        }
    }

    public boolean TwoSensGDA(int i, int j) {
        if (j < sens.size()) return connections[j] != i;
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

        double x = data_received();

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

    public double data_received() {
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

    public double data_default() {
        double count = 0;
        for (int i = 0; i < sens.size(); ++i) {
            count += sens.get(i).getCapacidad();
        }
        return count;
    }

    // GETTERS

    public Sensores getSens() {
        return sens;
    }

    public CentrosDatos getCds() {
        return cds;
    }

    public double getTotal_Data() {
        return total_Data;
    }

    public int[] getDataSent() {
        return dataSent;
    }

    public int[] getConnections() {
        return connections;
    }

    // OUTPUT

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        double x = data_received();

        s.append("  ");
        for (int i = 0; i < sens.size(); ++i)
            s.append("S" + i + " ");
        for (int j = 0; j < cds.size(); ++j)
            s.append("C" + j + " ");

        s.append("\n");

        for (int i = 0; i < sens.size(); ++i) {
            s.append("S" + i + " ");
            for (int j = 0; j < sens.size() + cds.size(); ++j) {
                if (connections[i] == j) s.append(dataSent[i] + "  ");
                else s.append(0 + "  ");
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
        s.append("Datos transferidos: " + x + "\n");
        return s.toString();
    }
}
