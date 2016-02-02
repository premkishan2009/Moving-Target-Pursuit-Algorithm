package mtp;

import java.util.*;

public class RTAASearcher implements Agent {

    private final int lookahead = 250;

    public AStarNodesList nodos;
    private AStarNode goal;

    public Queue<Direction> currentPlan;

    @Override
    public void Initialize(int maxX, int maxY, Observation obs)
    {

        this.nodos = new AStarNodesList();
        this.currentPlan = new LinkedList<Direction>();

        //We create nodes first
        for (int i=0; i < maxX; i++)
        {
            for (int j=0; j < maxY; j++)
            {
                AStarNode nodo = new AStarNode(i, j, obs.HeuristicFreeSpace(i, j), nodos);
                nodos.addNode(nodo);

                // Save what is the goal
                if (nodo.heuristicDistanceToGoal == 0) goal = nodo;
            }
        }

        System.out.println("Map size "+ maxX +", "+ maxY);
        System.out.println("Goal at "+ goal);
        System.out.println("Im at "+ nodos.getNode(obs.getPosX(), obs.getPosY()) );

        // Primer plan
        replan(nodos.getNode(obs.getPosX(), obs.getPosY())    );

        //System.exit(0);

    }

    @Override
    public Direction Mover(Observation obs)
    {
        updateKnowledge(obs);

        // The stage over current plan can not be done .. Reschedule from here
        AStarNode actual = nodos.getNode(obs.getPosX(), obs.getPosY());
        //System.out.println("Curr pos : "+ actual);

        // The plan is terminated
        if (currentPlan.isEmpty())
        {
            //System.out.println("plan Completed. replan()");
            replan(actual);
        }

        // Ran into obstacle
        if ( !obs.Observer(currentPlan.peek()))
        {
            //System.out.println("Ran into Obstacle. replan()");
            replan(actual);
        }

        return currentPlan.poll();
    }

    private void replan(AStarNode start)
    {
        
        // Lying above plan
        currentPlan.clear();

        //We run A * and reschedule the path
        AStarRTAA algorithm = new AStarRTAA(start, goal, lookahead);
        algorithm.runAlgorithm();
        Stack<AStarNode> path = algorithm.getPath();
        
        // Q rescued stage would expand
        AStarNode nextToExpand = algorithm.getNextToExpand();

        // for all S is Closed
        if (nextToExpand != null)
            for (AStarNode s : algorithm.getClosed().values())
            {
                s.changeH(nextToExpand.distanceToStart + nextToExpand.heuristicDistanceToGoal - s.distanceToStart);
            }

        //System.out.println("path size:"+path.size());
        AStarNode a = path.pop();
        AStarNode b = path.pop();

        while (true)
        {
            // As we go from A to B?
            currentPlan.add(a.directionToNeighbour(b));

            if (path.isEmpty()) break;
            
            // a and b swap a position
            a = b;
            b = path.pop();

        }
    }

    //Remove nodes of the graph that are not really nodes (are obstacles)
    private void updateKnowledge(Observation obs)
    {
        for (Direction dir : Direction.values())
        {
            if (!obs.Observer(dir))
                killNode(obs.getPosX()+dir.X(), obs.getPosY()+dir.Y());
        }

        //System.out.println("Knowledge updated.");
    }

    private void killNode(int row, int column)
    {
        AStarNode nodeToKill = nodos.getNode(row, column);

        //System.out.println ("Removing the node" + nodeToKill + "from graph");

        nodos.removeNode(nodeToKill);
    }



}
