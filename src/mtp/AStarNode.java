package mtp;

import java.util.ArrayList;
import java.util.List;


public class AStarNode implements Comparable<AStarNode> {

    public int x;
    public int y;
    public int qCounter;

    public AStarNode previous;              // Previous Node
    public double distanceToStart;          // (g) Distance from start
    public double heuristicDistanceToGoal;  // (h) Estimated distance to goal

    private AStarNodesList nodes;

    public AStarNode(int x, int y, double heuristicDistanceToGoal, AStarNodesList nodes)
    {
        this.x = x;
        this.y = y;

        this.previous = null;
        this.heuristicDistanceToGoal = heuristicDistanceToGoal;

        this.nodes = nodes;
    }
    
    public AStarNode(int x,int y, int qCounter)
    {
    	this.x = x;
    	this.y = y;
    	this.qCounter = qCounter;
    	
    	this.previous = null;
    }
    
    public AStarNode(int x,int y)
    {
    	this.x = x;
    	this.y = y;
    	
    	this.previous = null;
    }

    // distance (start, current) + distancia_estimated (current, goal)
    public double getScore()
    {
        return distanceToStart + heuristicDistanceToGoal;
    }
    
    public int getQPosition(AStarNode node)
    {
    	return node.qCounter;
    }

    public void changeH(double newHeuristicDistanceToGoal)
    {
        this.heuristicDistanceToGoal = newHeuristicDistanceToGoal;
    }

    public List<AStarNode> getNeighbours()
    {
        List<AStarNode> neighbours = new ArrayList<AStarNode>();

        for (Direction dir : Direction.values())
        {
            AStarNode nodo = nodes.getNode(x + dir.X(), y + dir.Y());

            if (nodo != null)
            {
            	
                neighbours.add(nodo);
            }
        }
        
        return neighbours;
    }

    //To be used in priority queue
    //Is good (> 0) the score is lower
    @Override
    public int compareTo(AStarNode arg0)
    {
        if (this.getScore() == arg0.getScore()) return 0;

        if (this.getScore() < arg0.getScore()) return -1;

        return 1;
    }

    // Cartesian plane rotated by 90-clockwise
    public Direction directionToNeighbour(AStarNode neighbour)
    {
        if (neighbour.x == this.x)
        {
            if (this.y < neighbour.y)
                return Direction.down;
            else
                return Direction.up;
        }
        else // if (neighbour.y == this.y)
        {
            if (this.x < neighbour.x)
                return Direction.right;
            else
                return Direction.left;
        }
    }



    @Override
    public String toString()
    {
        return "("+ x +", "+ y +")";
    }


    // delete
    public boolean known = false;

    public boolean allNeighboursAreUnknown()
    {
        for (AStarNode neighbour : this.getNeighbours())
        {
            // Find one's name
            if (neighbour.known) return false;
        }

        return true;
    }




}
