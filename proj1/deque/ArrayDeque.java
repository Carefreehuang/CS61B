package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>,Iterable<T>{
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

        @Override
        public void printDeque() {
                for (int i = 0; i < size; i++) {
                        System.out.print(get(i) + " ");

                }
                System.out.println(" ");
        }

        private void resize(int capacity) {//依旧不太明白
                T[] a = (T[]) new Object[capacity];
                int first = getFirst();
                int last = getLast();
                if (first < last) {
                        System.arraycopy(items, first, a, 0, size);
                } else {
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
        private void sizeCheck(){
                if (size < items.length / 4 + 1 && items.length > 16){
                        resize(items.length / 2);
                }
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
                return items[(getFirst() + index) % items.length];
        }

        public Iterator<T> iterator() {
                return new ArrayDequeIterator();
        }

        private class ArrayDequeIterator<T> implements Iterator<T> {
                private int pos;

                ArrayDequeIterator() {
                        pos = 0;
                }

                public boolean hasNext() {
                        return pos < size;
                }

                public T next() {
                        T returnItem = (T) get(pos);
                        pos += 1;
                        return returnItem;
                }
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) {
                        return true;
                }
                if (o == null) {
                        return false;
                }

                if (!(o instanceof Deque)) {
                        return false;
                }
                Deque<T> other = (Deque<T>) o;
                if (size() != other.size()) {
                        return false;
                }
                for (int i = 0; i < size(); i++) {
                        T item1 = get(i);
                        T item2 = other.get(i);
                        if (!item1.equals(item2)) {
                                return false;
                        }
                }
                return true;
        }
}
