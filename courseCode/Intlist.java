public class Intlist {
    public int first;
    public Intlist rest;

    public Intlist(int f, Intlist r) {
        first = f;
        rest = r;
    }

    public int size() {
        if (this.rest == null) {
            return 1;
        } else {
            return 1 + this.rest.size();
        }
    }

    public static void main(String[] args) {
        Intlist L = new Intlist(0, null);// 你是个傻逼吧卧槽，创建对象要new啊！
        L = new Intlist(2, L);
        L = new Intlist(5, L);
        System.out.println("L的长度为" + L.size());
        System.out.print(L.get(1) + "," + L.get(2) + "," + L.get(3));
    }

    public int get(int n) {
        if (n == 0) {
            return this.first;
        } else {
            return this.rest.get(n - 1);
        }
    }
}
