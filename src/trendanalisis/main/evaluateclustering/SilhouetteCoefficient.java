/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.evaluateclustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

public class SilhouetteCoefficient {

    private double Calculate(double[] silhouette) throws IOException {



        double s = 0.0f;
        for (int i = 0; i < silhouette.length; i++) {
            s += silhouette[i];
        }

        return s / silhouette.length;
    }

    public double evaluateClustering(Map<Integer, ArrayList<Integer>> mapClusterIndex, Instances instances) throws IOException {

        int[] datawithClusetrId = new int[instances.size()];

        // Set tabel ID for mapping ID CLuster
        for (int cluster : mapClusterIndex.keySet()) {
            for (int i = 0; i < mapClusterIndex.get(cluster).size(); i++) {
                datawithClusetrId[mapClusterIndex.get(cluster).get(i)] = cluster;
              //  System.out.println( mapClusterIndex.get(cluster).get(i) +" \t:" + cluster);//min
            }

        }

        double[] s = new double[instances.size()];

        for (int i = 0; i < s.length; i++) {
            int cluster = datawithClusetrId[i];
            //testing if the cluster is a singleton
            if (mapClusterIndex.get(cluster).size() > 1) {
                double a = Average(instances.get(i), instances, mapClusterIndex.get(cluster));

                double b = Double.POSITIVE_INFINITY;
                for (int j = 0; j < mapClusterIndex.keySet().size(); j++) {
                    if (j == cluster) {
                        continue;
                    }
                    //System.out.println(mapClusterIndex.get(cluster).size()+"\t"+  j +" \t:" + mapClusterIndex.get(j));//min
                    b = Math.min(b, Average(instances.get(i), instances, mapClusterIndex.get(j)));
                }

                s[i] = (b - a) / (Math.max(a, b));
            } else {
                //if it is a singleton, s <- 0
                s[i] = 0.0f;
            }
        }
        return Calculate(s);
    }

    private double Average(Instance instance, Instances instances, ArrayList<Integer> idInCluster) {
        //System.out.println("WB-Index (Min)     \t:" + idInCluster.size());//min
        EuclideanDistance ecludiean = new EuclideanDistance(instances);
        double a = 0.0f;

        for (int i = 0; i < idInCluster.size(); i++) {
          //  System.out.println(idInCluster.get(i));//min
            a += ecludiean.distance(instance, instances.get(idInCluster.get(i)));
        }

        return a / idInCluster.size();
    }
}
