package lab11.graphs;

import java.util.ArrayDeque;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int source;
    private int target;
    private Maze maze;
    private boolean targetFound = false;
    private ArrayDeque arrayDeque;
    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        arrayDeque = new ArrayDeque<Integer>();
        maze = m;
        // Add more variables here!
        source = maze.xyTo1D(sourceX, sourceY);
        target = maze.xyTo1D(targetX, targetY);
        distTo[source] = 0;
        edgeTo[source] = source;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        // TODO: Your code here. Don't forget to update distTo, edgeTo, and marked, as well as call announce()
        arrayDeque.add(source);
        marked[source] = true;
        announce();
        while ( !arrayDeque.isEmpty() ){
            int current = (int)arrayDeque.remove();
            for (int w: maze.adj(current)) {
                if (!marked[w]){
                    marked[w] = true;
                    announce();
                    arrayDeque.add(w);
                    edgeTo[w] = current;
                    announce();
                    distTo[w] = distTo[current] + 1;
                    announce();
                    if (w == target){
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

