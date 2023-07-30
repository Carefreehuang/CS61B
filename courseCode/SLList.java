public class SLList {
    public class IntNode {
        public int item;
        public IntNode next;

        public IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }
    }

    public IntNode first;

    public SLList(int x) {
        first = new IntNode(x, null);
    }

    public void addFirst(int x) {
        first = new IntNode(x, first);
    }

    public int getFirst() {
        return first.item;
    }

    private int sizeRecussion(IntNode p) {
        if (p.next == null) {
            return 1;
        } else {
            return 1 + sizeRecussion(p.next);
        }
    }

    public int size() {
        // if (first.next == null){
        // return 1;
        // }
        // else {
        // return 1 + first.next.next.size();
        // }
        int size = 1;
        IntNode currentFirst = first;
        while (currentFirst.next != null) {
            currentFirst = currentFirst.next;
            size += 1;
        }
        return size;
    }

    public void addLast(int x) {
        IntNode last = new IntNode(x, null);
        IntNode currentIntNode = first;
        for (int i = 0; i < this.size() - 1; i++) {
            currentIntNode = currentIntNode.next;
        }
        currentIntNode.next = last;
    }

    public void printSLList() {
        IntNode currentIntNode = first;
        for (int i = 0; i < this.size(); i++) {
            System.out.println(currentIntNode.item);
            currentIntNode = currentIntNode.next;
        }

    }

    public static void main(String[] args) {
        SLList L = new SLList(10);
        L.addFirst(20);
        L.addFirst(30);
        L.addFirst(40);
        System.out.println(L.size());
        L.printSLList();
        L.addLast(0);
        System.out.println(L.size());
        L.printSLList();
        System.out.println(L.sizeRecussion(L.first));
    }
}