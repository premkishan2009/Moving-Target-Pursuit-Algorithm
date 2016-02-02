package mtp;

import java.util.TimerTask;

public class VFBest extends TimerTask {

	//Hunter Parameters
	
	public static int alpha = 2;
	private int INFINITY = Integer.MAX_VALUE;
	
	
	//DTS
	int maxQSize = 50;
	int currQSize = 0;
	public static AStarNode vFBestNode;
	public static boolean bestAvail = true;
	
	public VFBest(){
		VFBest.vFBestNode = new AStarNode(Prey.goalX,Prey.goalY);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		vfBest();
		if(currQSize > maxQSize){
			DTS(Prey.currentTrajectory.peek().qCounter,vFBestNode.qCounter);
			System.out.println("Queue Cut");
		}
		
	}
	
	public AStarNode DTS(int start, int end){
		int sizeToCut = end - start + 1;
		for(int i = 0;i<sizeToCut;i++){
			Prey.currentTrajectory.poll();
		}
		return Prey.currentTrajectory.peek();
	}

	private void vfBest() {
		int vFBest = INFINITY;
		int vF = 0;
		for (AStarNode node : Prey.currentTrajectory) {
			vF = alpha * (Math.abs(Hunter.x - node.x) + Math.abs(Hunter.y - node.y)) + (Prey.qCounter - node.qCounter) / (1 - (Hunter.uH / Prey.uP)); // 0.5
			//System.out.println("Current vF is: " + vF);
			if (vFBest >= vF){
				vFBest = vF;
				vFBestNode = node;
			}
		}
		currQSize = Prey.currentTrajectory.size();
		bestAvail = true;
		//System.out.println("Current QSize is: " + currQSize);
		//System.out.println("Current vFbest is: " + vFBest);
	}
}
