package mtp;



public interface Observation 
{
	public int getPosX();
	public int getPosY();
	public double HeuristicFreeSpace(int x, int y);
	public double Heuristic(Direction d);
	public double Heuristic();
	public boolean Observer(Direction d);
}
