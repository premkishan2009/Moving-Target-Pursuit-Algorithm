package mtp;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Stack;

public class AStarRTAA {

	private AStarNode goal;
	private AStarNode nextToExpand;

	private PriorityQueue<AStarNode> open; // not reviewed
	private Hashtable<AStarNode, AStarNode> closed; // reviewed
	private Stack<AStarNode> path;

	private int lookahead;

	public AStarRTAA(AStarNode start, AStarNode goal, int lookahead) {
		this.goal = goal;
		this.lookahead = lookahead;

		this.open = new PriorityQueue<AStarNode>();
		this.closed = new Hashtable<AStarNode, AStarNode>();
		this.path = new Stack<AStarNode>();

		// Initializations
		start.distanceToStart = 0;
		start.previous = null;

		open.add(start);
	}

	public void runAlgorithm() {
		int lookaheadCounter = 0;

		while (!open.isEmpty()) {
			lookaheadCounter++;

			AStarNode popped = open.poll(); // The score less open

			if (popped.equals(goal)) {
				reconstructPath(goal);
				return;
			}

			closed.put(popped, popped);

			for (AStarNode neighbour : popped.getNeighbours()) {
				if (closed.containsKey(neighbour))
					continue; // Next neighbour

				double tentativeDistanceToStart = popped.distanceToStart + 1;
				boolean tentativeIsBetter = false;
				boolean addNeighbourToOpen = false;

				// Neighbour never tried (it has a definite g)
				if (!open.contains(neighbour)) {
					addNeighbourToOpen = true;
					tentativeIsBetter = true;
				}
				// Yes have a defined g and is better than the tentative
				else if (tentativeDistanceToStart < neighbour.distanceToStart) {
					tentativeIsBetter = true;
				}
				// Does have a defined and worse g
				else {
					tentativeIsBetter = false;
				}

				if (tentativeIsBetter) {
					neighbour.previous = popped; // A neighbor is reached via
													// popped

					// System.out.println ("A" + neighbor + "is reached by" +
					// popped);
					neighbour.distanceToStart = tentativeDistanceToStart;
				}

				if (addNeighbourToOpen)
					open.add(neighbour);

				if (lookaheadCounter > lookahead) {
					nextToExpand = open.poll();
					reconstructPath(popped);
					return;
				}
			}
		}
	}

	private void reconstructPath(AStarNode currentNode) {
		path.push(currentNode);

		if (currentNode.previous != null)
			reconstructPath(currentNode.previous);
	}

	public Stack<AStarNode> getPath() {
		return path;
	}

	public Hashtable<AStarNode, AStarNode> getClosed() {
		return closed;
	}

	public AStarNode getNextToExpand() {
		return nextToExpand;
	}
}
