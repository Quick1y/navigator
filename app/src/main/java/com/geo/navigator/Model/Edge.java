package com.geo.navigator.Model;

/**
 * Created by nikita on 04.06.17.
 *
 * Класс описывает ребра графа
 */

public class Edge {
    private int mIdPointA;
    private int mIdPointB;
    private int mWeight;
    private int mId_map;
    private String mDescription; // возможно, понадобится описание

    public Edge(int idA, int idB, int weight, int id_map, String desc){
        mIdPointA = idA;
        mIdPointB = idB;
        this.mWeight = weight;
        mId_map = id_map;
        mDescription = desc;
    }

    public int getIdPointA() {
        return mIdPointA;
    }

    public int getIdPointB() {
        return mIdPointB;
    }

    public int getWeight() {
        return mWeight;
    }

    public String getDescription(){return mDescription;}

    public int getId_map() {
        return mId_map;
    }

    /**
     * Преобразует матрицу весов в виде двумерного массива int
     * в матрицу ребер типа Edge
     */
    public static Edge[][] fromWeightMatrix(int[][] weightMatrix, int idmap){
        int wLength = weightMatrix.length;
        int hLength = weightMatrix[0].length; //Пока предполагаем, что матрица квадратная. В идеале надо искать самый длинный массив

        Edge[][] edgesMatrix = new Edge[hLength][wLength];

        for(int h = 0; h < hLength; h++){
            for (int w = 0; w < wLength; w++){
                edgesMatrix[h][w] = new Edge(h,w,weightMatrix[h][w], idmap,"from weight matrix");
            }
        }

        return edgesMatrix;
    }


}
