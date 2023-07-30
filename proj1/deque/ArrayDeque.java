package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>{
        private T[] items;
        private int capacity = 8;
        private int size;
        private int nextFirst;//队首下标
        private int nextLast;//队尾下标
        public ArrayDeque(){
                //初始大小以及初始点是可以随便更改的，以下参照ppt
                items = (T[]) new Object[capacity];
                nextFirst = 4;
                nextLast  = 5;
                size = 0;
        }
        @Override
        public void addFirst(T t){
                if (size == items.length){
                        resize(2 *size);
                }
                size += 1;
                items[nextFirst] = t;
                nextFirst = (nextFirst - 1 + capacity) % capacity;// 加上 capacity ！
        }
        @Override
        public void addLast(T t){
                if (size == items.length){
                        resize(2 * size);
                }
                size += 1;
                items[nextLast] = t;
                nextLast = (nextLast + 1) % capacity;
        }
        @Override
        public int size(){
                return size;
        }

        @Override
        public void printDeque(){//首尾指针相同， 队列满， 打印顺序的确定
                int first = (nextFirst + 1) % capacity;
                int last = (nextLast - 1 + capacity) % capacity;
                while (first != last){
                        System.out.print(items[first] + " ");
                        first = (first + 1) % capacity;
                }
                System.out.print(items[first] + " ");  //最后一步，last == first，所以需要额外打印。
                System.out.println(" ");
        }

        @Override
        public T removeFirst(){//remove是真的挺难的！！！
                //不是常数时间
                //实现的很蠢，应该可以直接修改指针位置
                int first = (nextFirst + 1) % capacity;
                int last = (nextLast - 1 + capacity) % capacity;
                if (items[first] == null){
                        return null;
                }
                else {
                        T result = items[first];
                        while (first != last) {
                                items[first] = items[first + 1];
                                first = (first + 1) % capacity;
                        }
                        items[first] = null;
                        size -= 1;
                        nextLast -= 1;
                        return result;
                }
        }
        @Override
        public T removeLast(){//remove是真的挺难的！！！
                int last = (nextLast - 1 + capacity) % capacity;
                if (items[last] == null){
                        return null;
                }else{
                T result = items [last];
                items[last] = null;
                size -= 1;
                nextLast = last;
                return result;}
        }
        @Override
        public T get(int index){
                if (index <= size - 1){
                        int first = (nextFirst + 1) % capacity;
                        return items[(first + index) % capacity];
                }
                else {
                        return null;
                }
        }
        private void resize(int newcapacity){
                T[] a = (T[]) new Object[newcapacity];
                //如何复制不打乱原来顺序是关键
                //选择从头到尾复制到新的扩张数组(从0 - size ),操作类似于printdeque
                int i = 0;
                int first = (nextFirst + 1) % capacity;
                int last = (nextLast - 1 + capacity) % capacity;
                while (first != last){
                        a[i] = items[first];
                        first = (first + 1) % capacity;
                        i++;
                }
                a[i] = items[first];
                items = a;
                capacity = newcapacity;
                nextFirst = newcapacity-1;
                nextLast = size;
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
        public Iterator<T> iterator() {
                return new ArrayDequeIterator();
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
