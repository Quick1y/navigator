package com.geo.navigator.Model;

import android.util.Log;

import com.geo.navigator.Model.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikita on 04.06.17.
 *
 * Подробнее об используемом алгоритме можно почитать здесь:
 *     http://old.exponenta.ru/soft/mathcad/stud25/index.asp
 */
public class DijkstrasAlgorithm {
    private static final String TAG = "DijkstrasAlgorithm";
    private Map<Integer, Integer> mPointsId;      // Map<Номер точки, Id точки>
    private Map<Integer, Integer> mPointsNumbers; // Map<Id точки, Номер точки>
                                                  // Содержат соответствие точки и ее номера в алгоритме

    private Edge[][] mEdges; //Матрица рёбер


    public DijkstrasAlgorithm(Edge[][] edges){
        mEdges = edges;
        mPointsNumbers = new HashMap<>();
        mPointsId = new HashMap<>();

        //Составляем соответствие номера и id точки
        for (int i = 0; i < edges.length; i++){
            mPointsNumbers.put(edges[i][0].getIdPointA(),i);
            mPointsId.put(i, edges[i][0].getIdPointA());

            Log.d(TAG, i + "   " + edges[i][0].getIdPointA());
        }

    }




    /**
     * Возвращает массив точек, являющийся кратчайшим путем
     * между точками pointA и pointB
     */
    public int[] getShortestRoute(int idA, int idB){

        int numA = mPointsNumbers.get(idA); // получаем номера точек на графе
        int numB = mPointsNumbers.get(idB); // в соответствии с их id

        int[][] costArr = findCost(numA); //рассчет стоймости пути
        int[] wayNumArr = findWay(costArr, numA, numB);

        int[] wayIdArr = new int[wayNumArr.length];
        for (int i = 0; i < wayNumArr.length; i++){
            wayIdArr[i] = mPointsId.get(wayNumArr[i]);
        }

        return wayIdArr;
    }

    /**
     * Находит стоймость переходов от стартовой точки до всех остальных
     */
    private int[][] findCost(int numA){
        int w = mEdges.length; // количество точек
        int numB;

        int[][] T = new int[w][2];
        int[] H = new int[w];
        boolean[] X = new boolean[w];

        for (int j = 0; j < w; j++){
            T[j][0] = 10000;
            X[j] = false;
        }

        H[numA] = 0;
        T[numA][0] = 0;
        X[numA] = true;

        int v = numA;

        while (true){
            for (int u = 0; u < w; u++){
                if (X[u] == false
                        && mEdges[v][u].getWeight() < 900
                        && T[u][0] > T[v][0] + mEdges[v][u].getWeight()){

                    T[u][0] = T[v][0] + mEdges[v][u].getWeight();
                    H[u] = v;
                }
            }

            numB = 9000;
            v = 0;

            for (int u = 1; u < w; u++){
                if(X[u] == false && T[u][0] < numB){
                    v = u;
                    numB = T[u][0];

                }
            }

            if (v == 0 || v == numB) {
                break;
            }


            X[v] = true;
        }

        for (int i = 0; i < T.length; i++){
            T[i][1] = H[i];
        }
        return T;
    }

    /**
     * На основе стоймости переходов ищет кратчайший путь из А в В
     */
    private static int[] findWay(int[][] K, int numA, int numB){
        int k = 0;

        int[] C = new int[K.length];
        for (int i = 0; i<K.length; i++){
            C[i] = K[i][1];
        }

        int q = 0;
        ArrayList<Integer> Z = new ArrayList<>();
        Z.add(numB);

        while (true){
            k++;
            Z.add(C[Z.get(k-1)]);
            if(Z.get(k) == numA) {
                break;
            }
        }

        int[] Rez = new int[Z.size()];
        for (int i = Z.size()-1; i >= 0; i--){
            Rez[q] = Z.get(i);
            q = q + 1;
        }
        return Rez;
    }
}
