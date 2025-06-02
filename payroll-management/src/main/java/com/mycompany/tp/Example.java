package com.mycompany.tp;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Example {

    public static void main(String[] args) {
        String s = "aabbccdddeff";
        int[][] mat = new int[26][2];
        for(int i=0; i<26; i++)
            mat[i][0] = i;

        for(char c:s.toCharArray())
            mat[c-'a'][1]++;

        Arrays.sort(mat, (a,b)->a[1]-b[1]);

        int max = mat[mat.length-1][1];
        int min = Arrays.stream(mat).filter(i-> i[1]>0).findFirst().get()[1];


    }
}
