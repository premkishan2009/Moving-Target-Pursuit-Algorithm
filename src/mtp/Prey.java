package mtp;

import java.util.Hashtable;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Prey extends TimerTask {

	public static int uP = 200;
	public static int qCounter = 0;
	GUI window;

	// RTAA for Prey Escape
	RTAASearcher rtaa;
	private boolean[][] observed;
	final public static int INFINITE = Integer.MAX_VALUE;
	private double[][] heuristicFreeSpace;

	public char[][] map;

	// Prey Coordinates
	public static int goalX;
	public static int goalY;
	public static int goalEscX;
	public static int goalEscY;
	boolean goalset = false;
	public static ConcurrentLinkedQueue<AStarNode> currentTrajectory;

	public Prey(int goalX, int goalY, int geX, int geY, GUI window, char[][] map) {

		Prey.goalX = goalX;
		Prey.goalY = goalY;
		this.window = window;
		this.map = map;
		
		while(goalset!=true){
			Random rand = new Random();
			Prey.goalEscX = (int)rand.nextInt(193);
			Prey.goalEscY = (int)rand.nextInt(97);
			System.out.println(Prey.goalEscX+","+Prey.goalEscY);
			if(Available(Prey.goalEscX,Prey.goalEscY)){
				goalset=true;
			}
		}
		System.out.println("Escape Goal is at:"+"("+Prey.goalEscX+","+Prey.goalEscY+")");

		currentTrajectory = new ConcurrentLinkedQueue<AStarNode>();
		observed = new boolean[Controller.maxX][Controller.maxY];
		heuristicFreeSpace = new double[Controller.maxX][Controller.maxY];
		for (int i = 0; i < Controller.maxX; i++) {
			for (int j = 0; j < Controller.maxY; j++) {
				heuristicFreeSpace[i][j] = Heuristic(i, j);
				observed[i][j] = false;
			}
		}

		rtaa = new RTAASearcher();
		rtaa.Initialize(Controller.maxX, Controller.maxY, Observer());

		/*
		 * for (int i = 0; i < 10; i++) currentPath.add(Direction.left); for
		 * (int i = 0; i < 13; i++) currentPath.add(Direction.up); for (int i =
		 * 0; i < 40; i++) currentPath.add(Direction.right); for (int i = 0; i <
		 * 30; i++) currentPath.add(Direction.up);
		 */
	}

	private Observation Observer() {
		// Hash where observation data are saved
		Hashtable<Direction, Double> h = new Hashtable<Direction, Double>();
		Hashtable<Direction, Boolean> o = new Hashtable<Direction, Boolean>();

		observed[goalX][goalY] = true;
		// We calculate the observation for each of the possible successors
		for (Direction d : Direction.values()) {
			int newX = goalX + d.X();
			int newY = goalY + d.Y();
			observed[newX][newY] = true;
			if (Available(newX, newY)) {
				h.put(d, Heuristic(newX, newY));
			} else {
				h.put(d, (double) INFINITE);
			}
			o.put(d, Available(newX, newY));

		}

		// We return the corresponding observation
		Observation obs = new Datum(h, o, Heuristic(goalX, goalY), goalX,
				goalY, heuristicFreeSpace);
		return obs;
	}

	private Double Heuristic(int i, int j) {
		// System.out.println(i+" "+goalX+" "+j+" "+goalY+" "+(Math.abs(i-goalX)-Math.abs(j-goalY)));
		return (double) (Math.abs(i - Prey.goalEscX) + Math.abs(j - Prey.goalEscY));

	}

	public boolean Follow() {
		return !Hunter.terminate && Heuristic(goalX, goalY) != 0.0;
	}

	@Override
	public void run() {
		if (Follow()) 
		{
			Observation new_observation = Observer();
			Direction next = rtaa.Mover(new_observation);

			int newPreyX = goalX + next.X();
			int newPreyY = goalY + next.Y();

			if (Available(newPreyX, newPreyY)) {
				goalX = newPreyX;
				goalY = newPreyY;
				qCounter++;
				AStarNode node = new AStarNode(goalX, goalY, qCounter);
				currentTrajectory.add(node);
			}
			//Controller.total_moves++;
			window.UpdateMap(Hunter.x, Hunter.y, goalX, goalY,Controller.total_moves);

			if (!Follow()) {
				System.out.println("cost solution =" + Controller.total_moves);
				//System.exit(0);
			}
		}

	}

	private Boolean Available(int i, int j) {
		return (map[i][j] == 'g' || map[i][j] == 'G');

	}

}
