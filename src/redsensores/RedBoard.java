package redsensores;

import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;

import java.util.HashMap;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;


public class RedBoard {

    private HashMap<Sensor, Double> init;

    private Sensores sens;
    private CentrosDatos cds;

    public RedBoard(int nsens, int ncds) {

        sens = new Sensores(nsens, 1);
        cds = new CentrosDatos(ncds, 1);

        for(Sensor s : sens) {
            double min = 100;
            for(Centro c : cds) {
                double dist = distance(s.getCoordX(), s.getCoordY(), c.getCoordX(), c.getCoordY());
                if(dist < min)
                    min = dist;
            }
            init.put(s, min);
        }
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1-x2), 2) + pow((y1-y2), 2));
    }

}
