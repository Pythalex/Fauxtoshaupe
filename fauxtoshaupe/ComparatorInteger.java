package fauxtoshaupe;

import java.util.Comparator;

public class ComparatorInteger implements Comparator<Integer> {

    @Override
    public int compare(Integer left, Integer right){
        return left - right;
    }

}
