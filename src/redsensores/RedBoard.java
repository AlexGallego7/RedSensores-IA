package redsensores;

import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;


public class RedBoard {

    private ArrayList<String> init;

    private Sensores sens;
    private CentrosDatos cds;

    public RedBoard(int nsens, int ncds) {

        // ESTADO INICIAL

        init = new ArrayList<>();
        sens = new Sensores(nsens, 1);
        cds = new CentrosDatos(ncds, 1);

        for(int j = 1; j <= sens.size(); ++j) {
            double min = 100;
            int n = -1;
            for(int i = 1; i <= cds.size(); ++i) {
                double dist = distance(sens.get(j-1).getCoordX(), sens.get(j-1).getCoordY(),
                        cds.get(i-1).getCoordX(), cds.get(i-1).getCoordY());
                if(dist < min) {
                    min = dist;
                    n = i;
                }
            }
            init.add("Sensor " + j +
                    " x:" + sens.get(j-1).getCoordX()+
                    " y:" + sens.get(j-1).getCoordY()+
                    " a Centro " + n +
                    " x:" + cds.get(n-1).getCoordX()+
                    " y:" + cds.get(n-1).getCoordY() +"\n");
        }
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1-x2), 2) + pow((y1-y2), 2));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(String i : init) {
            s.append(i);
        }
        return s.toString();
    }
}
