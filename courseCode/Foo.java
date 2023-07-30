public class Foo {
    public int x, y;

    public Foo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static void switcheroo(Foo a, Foo b) {
        Foo temp = a;
        a = b;
        b = temp;
    }

    public static void fliperoo(Foo a, Foo b) {
        Foo temp = new Foo(a.x, a.y);
        a.x = b.x;
        a.y = b.y;
        b.x = temp.x;
        b.y = temp.y;
    }

    public static void swaperoo(Foo a, Foo b) {
        Foo temp = a;
        a.x = b.x;
        a.y = b.y;
        b.x = temp.x;
        b.y = temp.y;
    }

    public static void main(String[] args) {
        Foo foobar = new Foo(10, 20);
        Foo baz = new Foo(30, 40);
        System.out.println(" ");
        System.out.printf("%d,%d", foobar.x, foobar.y);
        System.out.printf("%d,%d", baz.x, baz.y);
        System.out.println(" ");
        switcheroo(foobar, baz); // foobar.x: _30__ foobar.y: __40_ baz.x: _10__ baz.y: _20__
        // 10,20,30,40
        System.out.printf("%d,%d", foobar.x, foobar.y);
        System.out.printf("%d,%d", baz.x, baz.y);
        System.out.println(" ");
        fliperoo(foobar, baz); // foobar.x: __30_ foobar.y: _40__ baz.x: __10_ baz.y: __20_
        // 30,40,10,20
        System.out.printf("%d,%d", foobar.x, foobar.y);
        System.out.printf("%d,%d", baz.x, baz.y);
        System.out.println(" ");
        swaperoo(foobar, baz); // foobar.x: __30_ foobar.y: __40_ baz.x: _30__ baz.y: __40_
        //
        System.out.printf("%d,%d", foobar.x, foobar.y);
        System.out.printf("%d,%d", baz.x, baz.y);
    }
}