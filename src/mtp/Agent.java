package mtp;



public interface Agent 
{
	public void Initialize(int maxX, int maxY, Observation obs);
	public Direction Mover(Observation obs);
}
