package moveTo1;


import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	public static RobotController rc;
	
	public static Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
		,Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	static Random rand;
	
	public static int height;
	public static int width;
	
	public static int rType;
	public static int rX;
	public static int rY;
	public static int[][] rVisited;
	public static String rAction;
	public static int rDestinationX;
	public static int rDestinationY;
	
	public static void run(RobotController myRC) {
		rc = myRC;
		rand = new Random();
		
		
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		
		if(rc.getType()==RobotType.SOLDIER)
		{
			rType = rc.senseRobotCount()%10;
			rVisited = new int[width][height];
			rAction = "none";
		}

		while(true){
			try{
				if(rc.getType()==RobotType.HQ){
					tryToSpawn();
				}else if (rc.getType()==RobotType.SOLDIER){
					rX = rc.getLocation().x;
					rY = rc.getLocation().y;
					if(rType <2)
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
				if(rType == 0)
				{
					rDestinationX = width - 1;
					rDestinationY = height - 1;
				}
				else if(rType == 1)
				{
					rDestinationX = 1;
					rDestinationY = height - 1;
				}
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
		Direction moveDirection = dirs[0];
		
		//double dx = 
		
		
		if (rc.canMove(moveDirection)) {
			if(mFlag)
			{
				rc.move(moveDirection);
			}
			else
			{
				rc.sneak(moveDirection);
			}
		}
	}
	
}
