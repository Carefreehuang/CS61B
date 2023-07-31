package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>{
        private T[]  items;
        private int nextFirst;
        private int nextLast;
        private int size;
        private void setIndex(int first,int last){//设置索引位置
                nextFirst = first;
                nextLast  = last;
        }
        private int getFirst(){
                return (nextFirst + 1) % items.length;
        }
        private int getLast(){
                return (nextLast - 1 + items.length) % items.length;
        }
        private void incFirst(){
                nextFirst = (nextFirst + 1) % items.length;
        }
        private  void decFirst(){
                nextFirst = (nextFirst - 1 + items.length) % items.length;
        }
        private void incLast(){
                nextLast = (nextLast + 1) % items.length;
        }
        private  void decLast(){
                nextLast = (nextLast - 1 + items.length) % items.length;
        }
        public ArrayDeque(){
                items = (T[]) new Object[8];
                size = 0;
                setIndex(4,5);
        }

        @Override
        public int size() {
                return size;
        }
        public void resize(int capcacity){
                T[] a = (T[]) new Object[capacity];
                int first = getFirst();
                int last = getLast();
                if (first < last) {     //没有环绕，正常复制
                        System.arraycopy(items, first, a, 0, size);
                } else {                //有环绕，考虑情况
                        if (first <= items.length - 1) {
                                System.arraycopy(items, first, a, 0, items.length - first);
                        }
                        if (last >= 0) {
                                System.arraycopy(items, 0, a, items.length - first, last + 1);
                        }
                }
                items = a;
                setIndex(items.length - 1, size);
        }

        @Override
        public void addFirst(T item) {
                if (size == items.length){
                        resize(2 * size);
                }
                items[nextFirst] = item;
                decFirst();
                size += 1;
        }

        @Override
        public void addLast(T item) {
                if (size == items.length){
                        resize(2 * size);
                }
                items[nextLast] = item;
                incLast();
                size += 1;
        }

        @Override
        public T removeFirst() {
                if (size == 0){
                        return null;
                }
                sizeCheck();
                T result = items[getFirst()];
                items[getFirst()] = null;
                incFirst();
                size -= 1;
                return result;
        }

        @Override
        public T removeLast() {
                if (size == 0){
                        return null;
                }
                sizeCheck();
                T result = items[getLast()];
                items[getLast()] = null;
                decLast();
                size -= 1;
                return result;
        }

        @Override
        public T get(int index) {
                return items[];
        }
}
