package byow.lab12;
import com.sun.source.tree.SwitchTree;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final long SEED = 1379;

    /**
     * 初始化board
     * @param board
     */
    public static void fillBlankBoard(TETile[][] board){
        int width = board.length;
        int height = board[0].length;
        for (int x = 0; x < width; x++ ){
            for (int y = 0; y < height; y++){
                board[x][y] = Tileset.NOTHING;
            }
        }
    }
    public static void fillRow(TETile[][] board, Position P,TETile tile, int length){//从给定位置开始填充行
        int dx = P.x;
        int dy = P.y;
        for (int x = dx; x < length + dx; x ++){
            board[x][dy] = tile;
        }
    }
    public static void fillHexgonHelper(TETile[][] board,Position P,TETile tile, int length, int b){
        Position startPosition = P.shift(b,0);
        fillRow(board,startPosition,tile,length);
        if (b > 0){
            Position nextP = P.shift(0,-1);
            fillHexgonHelper(board,nextP,tile,length + 2,b-1);
        }
        Position reflectPosition = startPosition.shift(0,-(2*b + 1));
        fillRow(board,reflectPosition,tile,length);
    }
    public static void fillHexgon(TETile[][] board,Position P,TETile tile, int size){
        int b = size - 1;
        fillHexgonHelper(board,P,tile,size,b);
    }
    public static void fillColHexgon(TETile[][] board,Position P,TETile tile,int size,int num){
        Position startPostion = P.down(size);
        for (int i = 0;i < num; i++){
            fillHexgon(board,startPostion,tile,size);
            startPostion = startPostion.down(size);
        }
    }
    public static void fillBigHexgonHelper(TETile[][] board,Position P,TETile tile, int size, int bigSize,int b){
        fillColHexgon(board,P,Tileset.FLOWER,size,bigSize);
        if (b > 0){

        }
    }
    private static class Position {
        int x;
        int y;
        Position(int x, int y){
            this.x = x;
            this.y = y;
        }
        public Position shift(int dx, int dy){
            return new Position(x + dx, y+dy);
        }
        public Position down(int size){
            return this.shift(0,-2*size);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] board = new TETile[WIDTH][HEIGHT];
        Position P = new Position(10,40);
        fillBlankBoard(board);
        fillColHexgon(board,P,Tileset.FLOWER,3,3);
        ter.renderFrame(board);
    }
}
