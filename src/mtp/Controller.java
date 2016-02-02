package mtp;

import java.util.Timer;


public class Controller {

	// Global Section
	public static int total_moves = 0;

	// Agent Coordinates
	public Hunter hunter;
	public VFBest vfbest;

	// coordinates of the goal
	public Prey prey;

	// Map Dimensions
	public static int maxX;
	public static int maxY;
	private boolean[][] observed;
	
	// GUI
	private GUI window;

	public Controller(char[][] map, int getx, int gety, int gx, int gy,int gex, int gey, int maxX, int maxY) {
		Controller.maxX = maxX;
		Controller.maxY = maxY;
		//this.total_moves = 0;

		observed = new boolean[maxX][maxY];
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				observed[i][j] = false;
			}
		}

		this.window = new GUI(map, getx, gety, gx, gy, observed);
		this.window.setFocusable(true);
		hunter = new Hunter(getx,gety,window,map);
		prey = new Prey(gx,gy,gex,gey,window,map);
		vfbest = new VFBest();
	}
	
	public void startExecution(){
		Timer preyT = new Timer();
		Timer vFBestT = new Timer();	
		Timer hunterT = new Timer();
		preyT.scheduleAtFixedRate(prey, 0, Prey.uP);
		vFBestT.scheduleAtFixedRate(vfbest, 0, 4900);
		hunterT.scheduleAtFixedRate(hunter, 0, Hunter.uH);
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

}
