package mtp;

import java.util.Collection;
import java.util.Hashtable;


public class AStarNodesList {

    private Hashtable<String, AStarNode> nodos = new Hashtable<String, AStarNode>();

    public AStarNodesList()
    {

    }

    public void addNode(AStarNode node)
    {
        nodos.put(getKey(node.x, node.y), node);
    }

    public void removeNode(AStarNode node)
    {
        try
        {
            removeNode(node.x, node.y);
        }
        catch(Exception e){}
    }

    public Collection<AStarNode> getNodes()
    {
        return nodos.values();
    }

    public void removeNode(int x, int y)
    {
        nodos.remove(getKey(x, y));
    }

    public AStarNode getNode(int x, int y)
    {
        if (!nodos.containsKey(getKey(x, y))) return null;

        return nodos.get(getKey(x, y));
    }

    private String getKey(int x, int y)
    {
        return "("+ x +", "+ y +")";
    }

    public int size()
    {
        return nodos.size();
    }

    public boolean contains(int x, int y)
    {
        return (nodos.containsKey(getKey(x, y)));
    }
}
