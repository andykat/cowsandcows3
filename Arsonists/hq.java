package Arsonists;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class hq {
	static int statusReport[]= new int [25];
	static int statusIteratorPosition= 0;
	static Direction allDirections[] = Direction.values();
	
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
		MapLocation[] enemyPSTRLoc= rc.sensePastrLocations(rc.getTeam().opponent());//Get pasture locations
		
		//Search through to find, in the first 0~24 channels, one that is not 0, if it contains a number, broad cast from 25~50 the location of its closest target
		if (enemyPSTRLoc.length> 0){
			for (int x= 0; x< rc.senseRobotCount(); x++)
			{
					rc.broadcast(x+25, locationServices.locToInt(enemyPSTRLoc[0]));
					//rc.broadcast(x+25, locationServices.locToInt(rc.senseEnemyHQLocation()));
			}
		}
		
		//generate map representation for download
		/*
		if(haveMap == false) {
			BreadthFirst.init(rc, bigBoxSize);
			//pathfinding does not happen here, I think.
			
			//MapLocation goal = getRandomLocation();
			//path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
			//VectorFunctions.printPath(path,bigBoxSize);
		}
		MapAssessment.assessMap(4, rc);
//		MapAssessment.printBigCoarseMap();
//		MapAssessment.printCoarseMap();
		mapData = MapAssessment.coarseMap;
		haveMap = true; */
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
	
	public static int getStatusIterator()
	{
		return statusIteratorPosition;
	}
	
	public static void iterate(RobotController rc) throws GameActionException 
	{
		int numRobots= rc.senseRobotCount();
		if (statusIteratorPosition+1< numRobots)
			statusIteratorPosition++;
		else
			statusIteratorPosition= 0;
	}
}
