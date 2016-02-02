package mtp;

import java.util.*;

public class Datum implements Observation 
{
	Hashtable<Direction, Boolean> observer;
	Hashtable<Direction,Double> heuristic;
	private double heuristicPositionActual;
	private int posX;
	private int posY;
	private double[][] heuristicFreeSpace;
	
	public Datum( Hashtable<Direction,Double> heuristic, Hashtable<Direction,Boolean> observer, double heuristicPositionActual, int posX, int posY, double[][] heuristicFreeSpace)
	{
		this.observer = observer;
		this.heuristic = heuristic;
		this.heuristicPositionActual=heuristicPositionActual;
		this.posX = posX;
		this.posY = posY;
		this.heuristicFreeSpace=heuristicFreeSpace;
	}

	public double HeuristicFreeSpace(int i, int j) {
		return heuristicFreeSpace[i][j];
	}
	
	@Override
	public int getPosX() {
		return posX;
	}
	
	@Override
	public int getPosY() {
		return posY;
	}
	@Override
	public double Heuristic(Direction d) {
		
		return heuristic.get(d);
	}
	
	@Override
	public double Heuristic() {
		
		return heuristicPositionActual;
	}

	@Override
	public boolean Observer(Direction d) {

		return observer.get(d);
	}

}
