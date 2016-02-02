package mtp;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {

	public static void main(String[] args) throws InterruptedException {
		int [] maxDimensions = new int[2];
		char[][] map = ParseMap(args[0],maxDimensions);
		int x_initial = Integer.parseInt(args[1]);
		int y_initial = Integer.parseInt(args[2]);
		int x_meta = Integer.parseInt(args[3]);
		int y_meta = Integer.parseInt(args[4]);
		int goal_escX = Integer.parseInt(args[5]);
		int goal_escY = Integer.parseInt(args[6]);

		System.out.println("Done Parsing");
		Controller control = new Controller(map, x_initial,	y_initial, x_meta, y_meta,goal_escX,goal_escY,maxDimensions[0],maxDimensions[1]);

		// Search Cycle
		control.startExecution();
		//Timer t = new Timer();
		//t.scheduleAtFixedRate(control, 0, 30);
		
	}

	@SuppressWarnings("resource")
	private static char[][] ParseMap(String path,int[] maxDimensions) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			System.out.println("Incorrect File - Map not Found");
			e.printStackTrace();
			System.exit(0);
		}

		sc.next(); // type
		sc.next(); // value
		sc.next(); // height
		int height = sc.nextInt();
		sc.next(); // width
		int width = sc.nextInt();
		sc.next(); // map

		// Initialize map
		System.out.println("Initilizing Map");
		char[][] map = new char[height][width];
		maxDimensions[0] = height;
		maxDimensions[1] = width;
		// Skip Numbers
		for (int i = 0; i < height; i++)
			sc.next();

		// Read Map
		for (int i = 0; i < height; i++) {
			String fila = sc.next();
			for (int j = 0; j < width; j++) {
				map[i][j] = fila.charAt(j);
			}

		}
		return map;
	}

}
