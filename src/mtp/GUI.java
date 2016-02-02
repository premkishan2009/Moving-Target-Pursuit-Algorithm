package mtp;


import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class GUI extends JFrame {
	// Map data
	JLabel[][] labels;
	int rows;
	int columns;
	int pixel;

	//Agent Details
	int x;
	int y;
	Color previous = null;

	// labels.
	JLabel agent;
	JLabel goal; 
	JLabel vfbest;
	JLabel moved;
	private boolean [][] observed;
	private char [][] map;
	//private int goalX;
	//private int goalY;
	
	private boolean hidden=false;  //controls if you hide cells never observed

	public GUI(char[][] map, int agentX, int agentY, int goalX, int goalY,boolean[][] observed) {
		// Data from the GUI starts.
		this.rows = map.length;
		this.map = map;
		this.columns = map[0].length;
		this.labels = new JLabel[rows][columns];
		this.pixel = 4;
		//this.goalX=goalX;
		//this.goalY=goalY;
		this.observed=observed;
		this.setTitle("Simulator");

		// We create the agent
		agent = new JLabel();
		agent.setOpaque(true);
		agent.setBounds(30 * pixel, 30 * pixel, pixel, pixel);
		agent.setBackground(Color.yellow);
		this.add(agent);
		
		//We create Goal
		goal = new JLabel();
		goal.setOpaque(true);
		goal.setBounds(30 * pixel, 30 * pixel, pixel, pixel);
		goal.setBackground(Color.red);
		this.add(goal);
		
		//We create VFBest
		vfbest = new JLabel();
		vfbest.setOpaque(true);
		vfbest.setBounds(30 * pixel, 30 * pixel, pixel, pixel);
		vfbest.setBackground(Color.black);
		this.add(vfbest);

		// We create the window
		moved = new JLabel();
		moved.setBounds(10, 5, 200, 20);
		moved.setForeground(Color.yellow);
		this.add(moved);

		//We travel the map and paint the window.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				JLabel label = new JLabel();
				label.setOpaque(true);
				label.setBounds(i * pixel, j * pixel, pixel, pixel);
				if (!hidden || observed[i][j]) 
					label.setBackground(getColor(map[i][j]));
				else 
					label.setBackground(Color.black);
				this.add(label);
				labels[i][j] = label;
			}
		}
		//labels[goalX][goalY].setBackground(Color.red);

		this.setLayout(null);
		this.setSize(rows * pixel, columns * pixel);
		this.setVisible(true);

	}

	private Color getColor(char a) {
		switch (a) {
		case 'g':
		case 'G':
			return Color.gray;
		case 'S':
			return Color.blue;
		case 'W':
			return Color.darkGray;
		case 'T':
			return Color.green;
		default:
			return Color.black;
		}

	}

	public void UpdateMap(int target_x, int target_y,int goal_x, int goal_y, int current_moves) {

		if (!this.isFocusOwner())
			this.requestFocus();
		agent.setBounds(target_x * pixel, target_y * pixel, pixel, pixel);
		goal.setBounds(goal_x * pixel, goal_y * pixel, pixel, pixel);
		vfbest.setBounds(VFBest.vFBestNode.x * pixel, VFBest.vFBestNode.y * pixel, pixel, pixel);
		moved.setText("total moved: " + current_moves);

		if (hidden) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					JLabel label = labels[i][j];
					label.setBounds(i * pixel, j * pixel, pixel, pixel);
					if (observed[i][j])
						label.setBackground(getColor(map[i][j]));
					else
						label.setBackground(Color.black);
				}
			}
			//labels[goalX][goalY].setBackground(Color.red);
		}
	}
}
