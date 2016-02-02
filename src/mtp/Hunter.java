package mtp;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Hunter extends TimerTask {

	public static int uH = 100;
	public static int x;
	public static int y;
	public static boolean terminate;
	private boolean calculate = true;
	private boolean track = false;
	int sh = 150;
	GUI window;
	public char[][] map;
	static AStarNode hunter;

	int numOfMoves;
	int kSteps = 15;
	private boolean[][] observed;
	RTAASearcher rtaa;
	VFBest vfbest;
	public static double[][] heuristicFreeSpace;
	final public static int INFINITE = Integer.MAX_VALUE;

	int theta = 0;
	int thetaMin = 1;
	AStarNode interceptP;

	static AStarNode a;
	static AStarNode b;
	static int preyX;
	static int preyY;
	static ConcurrentLinkedQueue<AStarNode> follow;
	private static Queue<Direction> currentPath;

	// Heuristic Optimization
	int fromX;
	int fromY;
	int toX;
	int toY;
	int hWindow = 25;
	private Object[] tempTrajectory;
	boolean needHeuristic = true;
	boolean moveToBest = true;
	boolean best = true;
	int maxQSize = 25;
	int currQSize = 0;

	public Hunter(int x, int y, GUI window, char[][] map) {

		// AStarNode Hunter = new AStarNode(x,y);
		Hunter.x = x;
		Hunter.y = y;
		Hunter.hunter = new AStarNode(Hunter.x, Hunter.y);
		this.window = window;
		this.map = map;

		observed = new boolean[Controller.maxX][Controller.maxY];
		heuristicFreeSpace = new double[Controller.maxX][Controller.maxY];
		for (int i = 0; i < Controller.maxX; i++) {
			for (int j = 0; j < Controller.maxY; j++) {
				heuristicFreeSpace[i][j] = HeuristicInit(i, j);
				observed[i][j] = false;
			}
		}

		vfbest = new VFBest();
		rtaa = new RTAASearcher();
		rtaa.Initialize(Controller.maxX, Controller.maxY, Observer());
		currentPath = new LinkedList<Direction>();
	}

	@Override
	public void run() {
		if (Follow()) {
			if (track) {										 // track phase
				// System.out.println("Woo");
				AStarNode currP = new AStarNode(Prey.goalX, Prey.goalY);
				track(interceptP, currP);
			} else { 											// explore phase
				AO_OST();
				if (!checkQueue())
					track = false;
				else
					track = true;
			}
			Controller.total_moves++;
			window.UpdateMap(Hunter.x, Hunter.y, Prey.goalX, Prey.goalY,
					Controller.total_moves);

			if (!Follow()) {
				System.out.println("cost solution =" + Controller.total_moves);
				// System.exit(0);
			}
		}
	}

	private void AO_OST() {
		if (!VFBest.bestAvail) {
			best = false;
			if (needHeuristic) {
				tempTrajectory = Prey.currentTrajectory.toArray();
				AStarNode vF = (AStarNode) tempTrajectory[tempTrajectory.length / 2];
				reCalcHeuristics(vF);
				//VFBest.bestAvail = false;
				moveToBest = true;
				numOfMoves = 0;
			}
			Move();
		} else {
			//System.out.println(VFBest.vFBestNode + ",(" + Hunter.x+","+Hunter.y+")");
			best = true;
			if(moveToBest){
				reCalcHeuristics(VFBest.vFBestNode);
				numOfMoves = 0;
			}
			Move();
		}
	}

	private void Move() {
		if(best)
			System.out.println("Moving to vfBest");
		else
			System.out.println("Moving to vf");
		numOfMoves++;
		Direction next;
		int newHunterX = Hunter.x;
		int newHunterY = Hunter.y;
		if (numOfMoves <= kSteps) {
			//System.out.println(numOfMoves + "," +kSteps);
			needHeuristic = false;
			moveToBest = false;
			Observation new_observation = Observer();
			next = rtaa.Mover(new_observation);
			newHunterX = Hunter.x + next.X();
			newHunterY = Hunter.y + next.Y();

			if (Available(newHunterX, newHunterY)) {
				Hunter.x = newHunterX;
				Hunter.y = newHunterY;
			}
			if(VFBest.bestAvail && !best){
				moveToBest = true;
			}
		} else{
			if(best)
				VFBest.bestAvail = false;
			needHeuristic = true;
			moveToBest = true;
		}
	}

	// check if Hunter can Intercept Prey
	boolean checkQueue() {
		for (AStarNode node : Prey.currentTrajectory) {
			if (node.x == Hunter.x && node.y == Hunter.y) {
				interceptP = node;
				return true;
			}
		}
		return false;
	}

	// Create Heuristic Window for Hunter
	void heuristicBounds() {
		if (Hunter.x - hWindow > 0)
			fromX = Hunter.x - hWindow;
		else
			fromX = 0;

		if (Hunter.y - hWindow > 0)
			fromY = Hunter.y - hWindow;
		else
			fromY = 0;
		//
		if (Hunter.x + hWindow <= Controller.maxX)
			toX = Hunter.x + hWindow;
		else
			toX = 0;

		if (Hunter.y + hWindow <= Controller.maxY)
			toY = Hunter.y + hWindow;
		else
			toY = 0;
	}

	private Observation Observer() {
		// Hash where observation data are saved
		Hashtable<Direction, Double> h = new Hashtable<Direction, Double>();
		Hashtable<Direction, Boolean> o = new Hashtable<Direction, Boolean>();

		observed[Hunter.x][Hunter.y] = true;
		// We calculate the observation for each of the possible successors
		for (Direction d : Direction.values()) {
			int newX = Hunter.x + d.X();
			int newY = Hunter.y + d.Y();
			observed[newX][newY] = true;
			if (Available(newX, newY)) {
				// h.put(d, Heuristic(newX, newY));
				h.put(d, heuristicFreeSpace[newX][newY]);
			} else {
				h.put(d, (double) INFINITE);
			}
			o.put(d, Available(newX, newY));

		}

		// We return the corresponding observation
		Observation obs = new Datum(h, o,
				heuristicFreeSpace[Hunter.x][Hunter.y], Hunter.x, Hunter.y,
				heuristicFreeSpace);
		return obs;
	}

	public boolean Follow() {
		return !terminate && HeuristicInit(x, y) != 0.0;
	}

	private Boolean Available(int i, int j) {
		return (map[i][j] == 'g' || map[i][j] == 'G');

	}

	int currHPDist() {
		int currHPDist;
		double temp1 = Math.pow(Hunter.x - Prey.goalX, 2);
		double temp2 = Math.pow(Hunter.y - Prey.goalY, 2);
		currHPDist = (int) Math.pow(temp1 + temp2, 0.5);
		return currHPDist;
	}

	void test() {

		while (Hunter.x != VFBest.vFBestNode.x
				&& Hunter.y != VFBest.vFBestNode.y) {
			if (Hunter.x == Prey.goalX && Hunter.y == Prey.goalY) {
				System.out.println("cost solution =" + Controller.total_moves);
			}
			System.out.println("current Distance" + currHPDist());
			if (currHPDist() > sh) {
				System.out.println("Shit just got real");
			}

		}
	}

	void track(AStarNode start, AStarNode goal) {
		Hunter.x = start.x;
		Hunter.y = start.y;

		if(Hunter.x != goal.x && Hunter.y != goal.y) { 
			// sense scope check here 
			//currQSize = Prey.currentTrajectory.size();
			//System.out.println(Prey.currentTrajectory.size());
			//if (currQSize > maxQSize) {
				//vfbest.DTS(Prey.currentTrajectory.peek().qCounter,	start.qCounter - 1);
				//System.out.println(Prey.currentTrajectory.peek().qCounter+","+start.qCounter);
				//System.out.println("Queue Cut Hunter");
			//}

			//theta = (VFBest.alpha * (Math.abs(Hunter.x - Prey.goalX) + Math.abs(Hunter.y - Prey.goalY)) / (Prey.qCounter - start.qCounter));
			//System.out.println("current Theta is: " + theta);

			//if (theta < thetaMin) {
				//System.out.println("theta is less than thetaMin");
				//vfbest.DTS(Prey.currentTrajectory.peek().qCounter,	start.qCounter);
				//AStarNode prey = new AStarNode(Prey.goalX, Prey.goalY);
				//reCalcHeuristics(prey);
				//return;
			//}
			followPrey();
		}
	}

	void followPrey() {

		if (calculate) {
			currentPath.clear();
			preyX = Prey.goalX;
			preyY = Prey.goalY;
			follow = new ConcurrentLinkedQueue<AStarNode>(Prey.currentTrajectory);
			for (AStarNode node : follow) {
				if (node == interceptP) {
					break;
				}
				follow.poll();
			}
			a = follow.poll();
			b = follow.poll();
			calculate = false;
			System.out.println("calculated");
		}
		if(isChangedPreyPos()){
			preyX = Prey.goalX;
			preyY = Prey.goalY;
			AStarNode currP = new AStarNode(Prey.goalX,Prey.goalY);
			follow.add(currP);
		}
		//System.out.println(follow.isEmpty());
		if(!follow.isEmpty()) {
			AStarNode temp = follow.poll();
			Hunter.x = temp.x;
			Hunter.y = temp.y;
		}else{
			track = false;
			calculate = true;
			System.out.println("failed");
		}		
		if (Hunter.x == Prey.goalX && Hunter.y == Prey.goalY){
			//System.out.println("Got the MotherFucker");
			System.out.println("Success");
			terminate = true;
		}
		System.out.println("tracking");
		
	}
	
	boolean isChangedPreyPos(){
		if(preyX == Prey.goalX && preyY == Prey.goalY)
			return false;
		else
			return true;		
	}

	void reCalcHeuristics(AStarNode target) {
		heuristicBounds();
		// System.out.println("Inter");
		for (int i = fromX; i <= toX; i++)
			for (int j = fromY; j <= toY; j++) {
				if (rtaa.nodos.getNode(i, j) != null) {
					rtaa.nodos.getNode(i, j).heuristicDistanceToGoal = Heuristic(
							i, j, target);
				}
			}
	}

	private Double Heuristic(int i, int j, AStarNode target) {
		// System.out.println(i+" "+goalX+" "+j+" "+goalY+" "+(Math.abs(i-goalX)-Math.abs(j-goalY)));
		return (double) (Math.abs(i - target.x) + Math.abs(j - target.y));
		// return (double) (Math.abs(i - 120) + Math.abs(j - 120));
	}

	private Double HeuristicInit(int i, int j) {
		// System.out.println(i+" "+goalX+" "+j+" "+goalY+" "+(Math.abs(i-goalX)-Math.abs(j-goalY)));
		// return (double) (Math.abs(i - VFBest.vFBestNode.x) + Math.abs(j -
		// VFBest.vFBestNode.y));
		return (double) (Math.abs(i - 120) + Math.abs(j - 120));
	}

}
