package moveTo1;


import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	public static RobotController rc;
	
	public static Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
		,Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	public static int[] int_dirs_X = {0,1,1,1,0,-1,-1,-1};
	public static int[] int_dirs_Y = {-1,-1,0,1,1,1,0,-1};
	static Random rand;
	
	public static int height;
	public static int width;
	
	public static int rType;
	public static int rX;
	public static int rY;
	public static boolean[][] rVisited;
	public static String rAction;
	public static int rDestinationX;
	public static int rDestinationY;
	
	public static void run(RobotController myRC) {
		rc = myRC;
		rand = new Random(myRC.getRobot().getID());
		
		
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		
		if(rc.getType()==RobotType.SOLDIER)
		{
			rType = rc.senseRobotCount()%10;
			rVisited = new boolean[width][height];
			for(int i=0;i<width;i++)
			{
				for(int j=0;j<height;j++)
				{
					rVisited[i][j] = false;
				}
			}
			rAction = "none";
		}

		while(true){
			try{
				if(rc.getType()==RobotType.HQ){
					tryToSpawn();
				}else if (rc.getType()==RobotType.SOLDIER){
					rX = rc.getLocation().x;
					rY = rc.getLocation().y;
					if(rType <100)
					{
						pastureBot();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}		
	}
	public static void tryToSpawn() throws GameActionException {
		//if(Clock.getRoundNum()>=lastSpawnRound+GameConstants.HQ_SPAWN_DELAY_CONSTANT_1){
		if(rc.isActive()&&rc.canMove(dirs[0])&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
			rc.spawn(dirs[0]);
			//lastSpawnRound=Clock.getRoundNum();
		}
		//}
	}
	
	public static void pastureBot() throws GameActionException {
		if(rc.isActive())
		{
			if(rAction.equals("none"))
			{
				rDestinationX = rand.nextInt(width);
				rDestinationY = rand.nextInt(height);
				/*if(rType == 2)
				{
					rDestinationX = 1;
					rDestinationY = 1;
				}
				else if(rType == 1)
				{
					rDestinationX = width - 1;
					rDestinationY = 1;
				}*/
				//System.out.println("type:" + rType);
				//System.out.println("rdx: " +  rDestinationX + " rdy: " + rDestinationY);
				rAction = "moving";
			}
			if(rAction.equals("moving"))
			{
				move(false);
			}
		}
	}
	
	public static void move(boolean mFlag) throws GameActionException 
	{
		if(rDestinationX == rX && rDestinationY == rY)
		{
			for(int i=0;i<width;i++)
			{
				for(int j=0;j<height;j++)
				{
					rVisited[i][j] = false;
				}
			}
			rAction = "makePasture";
			return;
		}
		
		
		Direction moveDirection = dirs[0];
		
		double dx = ((double)rDestinationX) - ((double)rX);
		double dy = ((double)rDestinationY) - ((double)rY);
		
		double angle = Math.atan(dy/dx);
		if(dx<0.0)
		{
			angle += Math.PI;
		}
		
		int cur_direction =(int) Math.round(angle * 4.0 / Math.PI) + 2;
		if(cur_direction<0)
		{
			cur_direction += 8;
		}
		else if(cur_direction>7)
		{
			cur_direction -= 8;
		}
		
		for(int i=1;i<9;i++)
		{
			int test_direction = i;
			if(test_direction%2 == 1)
			{
				test_direction = -test_direction;
			}
			test_direction = test_direction/2 + cur_direction;
			if(test_direction<0)
			{
				test_direction += 8;
			}
			else if(test_direction>7)
			{
				test_direction -= 8;
			}
			
			moveDirection = dirs[test_direction];
			
			if (rc.canMove(moveDirection)) {
				if(!rVisited[rX + int_dirs_X[test_direction]][rY + int_dirs_Y[test_direction]])
				{
					if(mFlag)
					{
						rc.move(moveDirection);
					}
					else
					{
						rc.sneak(moveDirection);
					}
					rVisited[rX + int_dirs_X[test_direction]][rY + int_dirs_Y[test_direction]] = true;
				//	System.out.println("rx: " + rX + " ry: " + rY);
				}
				return;
			}
		}
		
		
		
	}
	
}
