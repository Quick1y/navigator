package com.geo.navigator.route.model;

/**
 * Created by nikita on 04.06.17.
 *
 * Класс описывает ребра графа
 */

public class Edge {
    private int idPointA;
    private int idPointB;
    private int weight;
    private String description; // возможно, понадобится описание

    public Edge(int idA, int idB, int weight){
        idPointA = idA;
        idPointB = idB;
        this.weight = weight;
    }

    public int getIdPointA() {
        return idPointA;
    }

    public int getIdPointB() {
        return idPointB;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Преобразует матрицу весов в виде двумерного массива int
     * в матрицу ребер типа Edge
     */
    public static Edge[][] fromWeightMatrix(int[][] weightMatrix){
        int wLength = weightMatrix.length;
        int hLength = weightMatrix[0].length; //Пока предполагаем, что матрица квадратная. В идеале надо искать самый длинный массив

        Edge[][] edgesMatrix = new Edge[hLength][wLength];

        for(int h = 0; h < hLength; h++){
            for (int w = 0; w < wLength; w++){
                edgesMatrix[h][w] = new Edge(h,w,weightMatrix[h][w]);
            }
        }

        return edgesMatrix;
    }


}
