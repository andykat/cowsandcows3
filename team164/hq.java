package team164;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Robot;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class hq {
	static Direction allDirections[] = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	static boolean haveMap = false;
	public static int[][] mapData;
	//static int bigBoxSize = 5;
	//doing in soldiers for scrimmage purposes.
	
	public static Map<Robot, Direction[]> pathingData;
	
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
		
		//try to have HQ generate representation of map for BFS
		//ideally HQ makes map and others download it
		if(haveMap == false) {
			MapAssessment.assessMap(5, rc);
			mapData = MapAssessment.coarseMap;
			haveMap = true;
		}

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
	
	//make methods for calculuating and broadcasting path data
	public static void calcPath(RobotController rc, MapLocation target) {
		//use map pathingData to store path for each individual robot
		Robot key = rc.getRobot();
		if(pathingData.containsKey(key)==false){
			//find path, set in map
			Direction[] value = {};//placeholder
			pathingData.put(key, value);
		}
	}
	
	public static void castPath(RobotController rc) {
		int message = 0;
		//figure out how to store and broadcast chunks of path
		//figure out channels
		Direction[] path = pathingData.get(rc.getRobot());
		if(path.length > 25) {
			//take first 25 values and set as pathChunk
			//remove first 25 values from stored path
			Direction[] pathChunk = Arrays.copyOfRange(path, 0, 24);
			path = Arrays.copyOfRange(path, 25, path.length);
			pathingData.put(rc.getRobot(), path);
		} else if (path.length > 0) {
			Direction[] pathChunk = path;
		}
		
	}
}
