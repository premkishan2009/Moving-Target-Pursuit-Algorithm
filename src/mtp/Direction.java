package mtp;



public enum Direction 
{
	left (-1, 0),
    right (1, 0),
	up (0,-1),
	down (0,1);

	private int x;
	private int y;
	
	Direction( int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	int X()
	{
		return x;
	}
	int Y()
	{
		return y;
	}
}
