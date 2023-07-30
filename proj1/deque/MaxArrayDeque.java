package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> c;
    public MaxArrayDeque(Comparator<T> newc){
        //生成Comparator类型的arraydeque，然后方便进行compare运算
        c = newc;
    }
    public T max(){
        return max(c);
    }
    public T max(Comparator<T> comparator){
        //对于Comparator类型进行compare运算，算出最大值。
        if (isEmpty()){
            return null;
        }
        int cmp;
        T maxvalue = get(0);
        for (int i = 0; i < size(); i++) {
            cmp = comparator.compare(get(i), maxvalue);//默认compare：return object1 - object2;
            if (cmp > 0){
                maxvalue = get(i);
            }
        }
        return maxvalue;
    }

}