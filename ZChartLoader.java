package  main.java;

import java.io.*;
import java.util.*;

/**
 * The ZChartLoader class is responsible for loading ZChart data.
 */
public class ZChartLoader {
    private final TreeMap<Double, Double[]> zChartMap;

    public ZChartLoader() {
        zChartMap = new TreeMap<>();
        loadZChart();
    }

    private void loadZChart() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ZChart.tsv")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] values = line.split("\\t");
                if (values.length < 3) continue;
                Double z = Double.valueOf(values[0]);
                Double fZ = Double.valueOf(values[1]);
                Double lZ = Double.valueOf(values[2]);
                zChartMap.put(fZ, new Double[]{z, lZ});
            }
        } catch (Exception e) {
            System.err.println("ZChart verileri yüklenemedi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public double getZValueForFR(double Fr) {
        Map.Entry<Double, Double[]> entry = zChartMap.ceilingEntry(Fr);
        if (entry == null) {
            throw new IllegalArgumentException("FR değeri için Z bulunamadı: " + Fr);
        }
        return entry.getValue()[0];
    }

    public double getLZValueForFR(double Fr) {
        Map.Entry<Double, Double[]> entry = zChartMap.ceilingEntry(Fr);
        if (entry == null) {
            throw new IllegalArgumentException("FR değeri için L(Z) bulunamadı: " + Fr);
        }
        return entry.getValue()[1];
    }
}