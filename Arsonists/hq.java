package Arsonists;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class hq {
	static Direction allDirections[] = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	
	//try to have HQ generate representation of map for BFS
	//ideally HQ makes map and others download it
	/*
	static boolean haveMap = false;
	public static int[][] mapData;
	static int bigBoxSize = 5;
	*/
	//doing in soldiers for scrimmage purposes.
	
	public static void run(RobotController rc) throws GameActionException {
		//Look at surrounding for can move location
		Direction spawnDir= allDirections[0];
		rc.broadcast(1, -1);//clear
		for (int x= 1; x< allDirections.length; x++){
			if (rc.canMove(allDirections[x])){
				spawnDir= allDirections[x];
				break;
			}
		}
		if(rc.isActive()&&rc.canMove(spawnDir)&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
			rc.spawn(spawnDir);
		}
		//Assigning pastures to pasture killers administration
		//MapLocation[] enemyPSTRLoc= rc.sensePastrLocations(rc.getTeam().opponent());//Get pasture locations
		

	}
	
	
	public static boolean thereAreEnemyPSTR(RobotController rc) throws GameActionException 
	{
		if (rc.sensePastrLocations(rc.getTeam().opponent()).length> 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
