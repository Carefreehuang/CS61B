package deque;

import java.util.Iterator;

public class LinkedListDeque <T> implements Deque<T> {
    /*-----------------TNode----------------*/
    private class TNode{
        public T item;
        public TNode prev;
        public TNode next;
        public TNode(T i,TNode p, TNode n){//初始化Tn
            item = i;
            prev = p;
            next = n;
        }
        }
    /*----------------LinkedListDeque-----------------*/
    private TNode sentinel;//采用更好的Circular sentinel
    private int size;
    public LinkedListDeque(){
        //初始化LinkedListDeque
        size = 0;
        sentinel = new TNode(null,sentinel,sentinel);
        //因为语句还没结束，此时的sentinel还是null，
        // 所以赋值任然是null，因此需要下面两次赋值
        sentinel.prev=sentinel;
        sentinel.next=sentinel;
    }
    /*----------------Methods-----------------*/
    @Override
    public void addFirst(T item){
        TNode newNode = new TNode(item,sentinel,sentinel.next);
        TNode newNodeNext = sentinel.next;
        newNodeNext.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }
    @Override
    public void addLast(T item){
        //卡了半天了，第一个prev总是指不到sentinel
        TNode newNode = new TNode(item,sentinel.prev,sentinel);
        TNode newNodePrev = sentinel.prev;
        newNodePrev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }
    @Override
    public int size(){
        return size;
    }
    @Override
    public void printDeque(){
        TNode p = sentinel.next;
        while (p != sentinel){
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println(" ");
    }
    @Override
    public T removeFirst(){
        if (size == 0){
            return null;
        }
        else {
            T removeNode = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size -=1;
            return  removeNode;
        }
    }
    @Override
    public T removeLast(){
        if (size == 0){
            return null;
        }
        else {
            T removeNode = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size -=1;
            return  removeNode;
        }
    }
    @Override
    public T get(int index){
        if (index > size - 1 || index < 0) {
            return null;
        }
        else{
            TNode p = sentinel.next;
            for (int i = 0; i < index ; i++) {
                p = p.next;
            }
            return p.item;
        }
    }
    private T getRecursiveHelper(int index, TNode p){
        if (index > size - 1){
            return null;
        }
        if (index == 0){
            return p.item;
        }
        else {
            return getRecursiveHelper(index - 1, p.next);
        }
    }
    public T getRecursive(int index){
        return getRecursiveHelper(index, sentinel.next);
    }
    public class LDIterator implements Iterator<T>{
        private int pos;//指向当前元素
        public LDIterator(){//初始化
            pos = 0;
        }
        public boolean hasNext(){
            return pos < size();
        }
        public T next(){
            T result = get(pos);
            pos += 1;
            return result;
        }

    }
    public Iterator<T> iterator(){
        return new LDIterator();
    }
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (o == null){
            return false;
        }
        if (o.getClass() != this.getClass()){
            return false;
        }
        LinkedListDeque newO = (LinkedListDeque) o;
        if (newO.size() != size()){
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if ( !this.get(i).equals(newO.get(i)) ){
                return false;
            }
        }
        return true;
    }
}
