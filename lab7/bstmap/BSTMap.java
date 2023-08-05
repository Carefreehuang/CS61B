package bstmap;

import edu.princeton.cs.algs4.BST;

import java.security.Key;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V>{
    private BSTNode root;
    public BSTMap(){
    }
    private class BSTNode<K,V>{
        private K key;
        private V value;
        private int size;
        public BSTNode left, right;
        public BSTNode(K k,V v,int size){
            this.key = k;
            this.value = v;
            this.size = size;
        }
    }

    @Override
    public void clear() {
        root = null;
        root.size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }
    public boolean containsKey(BSTNode node, K key){
        if (node == null){
            return false;
        }
            int cmp = key.compareTo((K) node.key);
            if (cmp > 0){
                return containsKey(node.right, key);
            } else if (cmp < 0) {
                return containsKey(node.left, key);
            }else {
                return true;
            }
    }

    @Override
    public V get(K key) {
        return get(root,key);
    }
    private V get(BSTNode node,K key){
        if (node == null){
            return null;
        }
        int cmp = key.compareTo( (K) node.key);
        if (node.key == null){
            return null;
        }else {
            if (cmp > 0){
                return (V) get(node.right, key);
            }
            else if (cmp < 0){
                return (V)  get(node.left, key);
            }
            else {
                return (V) node.value;
            }
        }
    }

    @Override
    public int size() {
        return size(root);
    }
    private int size(BSTNode node){
        if (node == null){
            return 0;
        }else {
            return node.size;
        }
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }
    private BSTNode put(BSTNode node , K key, V value){
        if (node == null){
            return new BSTNode<K, V>(key, value, 1);
        }
        else {
            int cmp = key.compareTo((K) node.key);
            if (cmp > 0){
                node.right = put(node.right, key, value);
            } else if (cmp < 0) {
                node.left = put(node.left, key, value);
            }
            else{
                node.value = value;
            }
            node.size = size(node.left) + size(node.right) + 1;
        }
        return node;
    }


    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
