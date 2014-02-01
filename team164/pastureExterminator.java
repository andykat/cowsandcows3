package team164;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;

public class pastureExterminator {
	
	static Random randall = new Random();
	static ArrayList<MapLocation> path;
	static int bigBoxSize = 5;
	//static Direction allDirections[] = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	static Direction allDirections[] = Direction.values();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	
	public static void run(RobotController rc) throws GameActionException {
		/*
		 * Channel:
		 * 
		 * 	0: Team 1 goal
		 * 	1: Team 1 attack location
		 * 	3: Team 1 member 1 location
		 * 	4: Team 1 member 2 location
		 * 	5: Team 1 member 3 location
		 * 	6: Team 1 member 4 location
		 * 	7: Team 1 member 5 location
		 *  8: Team 2 goal... etc
		 *  
		 * New Strategy
			1) Check if you are in range of your goal (location)
				if you are, sense what is in your location
					a. if it is an enemy pasture
						i. if there are no other enemy units around
							Attack the pasture
						ii. if there are enemy units around
							if safety status> 0
								if team attack channel contains enemy within range
									Attack enemy
								else 
									find closest enemy, attack it, and broadcast to respective team attack channel
							else evade 
					b. if it is a friendly pasture
						i. if there are no enemy units around
							Hover 
						ii. if there are enemy units around
							if safety status> 0
								if team attack channel contains enemy within range
									Attack enemy
								else 
									find closest enemy, attack it, and broadcast to respective team attack channel
							else evade 
					c. if it is none of the above
						i. if there are no enemy units around
							Hold position and don't move 
						ii. if there are enemy units around
							if safety status> 0
								if team attack channel contains enemy within range
									Attack enemy
								else 
									find closest enemy, attack it, and broadcast to respective team attack channel
							else evade 
				else (if you are not in range of your goal)
					a. if there are no enemy units around
							Move towards the goal
					b. if there are enemy units around
						if safety status> 0
							if team attack channel contains enemy within range
								Attack enemy
							else 
								find closest enemy, attack it, and broadcast to respective team attack channel
						else evade 
		 */
		//BreadthFirst.init(rc, bigBoxSize);
		MapLocation goal = getRandomLocation(rc);
		path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
		
		if (rc.isActive())
		{
			if (thereAreNearbyEnemies(rc)){
				if (senseDangerStatus(rc)>= 0){
					int channelZInfo= rc.readBroadcast(1);
					if (channelZInfo== -1)
						attackRandomNearByEnemies(rc);
					else{
						if (rc.canAttackSquare(VectorFunctions.intToLoc(channelZInfo))){
							rc.attackSquare(VectorFunctions.intToLoc(channelZInfo));
						}
						else{
							attackRandomNearByEnemies(rc);
						}
					}
				}
				else
					evade(rc);
			}
			else
				if (rc.sensePastrLocations(rc.getTeam().opponent()).length> 0) {
					//short range pathfinding simpleMove
					//locationServices.simpleMove(rc, rc.sensePastrLocations(rc.getTeam().opponent())[0]);
					goal = rc.sensePastrLocations(rc.getTeam().opponent())[0];
					
					if (path.size() != 0 && !path.get(path.size()-1).equals(VectorFunctions.mldivide(goal,bigBoxSize))){
						path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
						//System.out.println("recalculate");
					}
					if(path.size()==0){
						goal = getRandomLocation(rc);
						path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(rc.senseEnemyHQLocation(),bigBoxSize), 100000);
					}
				} else {
					//if(path.size()==0){
						//goal = getRandomLocation(rc);
						//path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(rc.senseEnemyHQLocation(),bigBoxSize), 100000);
					//}

				}
			//follow breadthFirst path
			//Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
			//locationServices.tryToMove(bdir, true, rc);//, directionalLooks, allDirections);
		}
	}
	
	public static int senseDangerStatus(RobotController rc) throws GameActionException
	{
		int answer= 0;
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent()); //has to remove pastures and hqs from this
		Robot[] nearbyAllies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam());
		MapLocation[] enemyPSTRLoc= rc.sensePastrLocations(rc.getTeam().opponent());
		int totalEnemies= nearbyEnemies.length;
		for (int x= 0; x< nearbyEnemies.length; x++){
			if (rc.senseRobotInfo(nearbyEnemies[x]).location== rc.senseEnemyHQLocation()){
				totalEnemies--;
			}
			for (int y= 0; y< enemyPSTRLoc.length; y++){
				if (rc.senseRobotInfo(nearbyEnemies[x]).location== enemyPSTRLoc[y]){
					totalEnemies--;
				}
			}
		}
		
		answer= nearbyAllies.length- totalEnemies;
		return answer;
	}
	
	public static boolean thereAreNearbyEnemies(RobotController rc) throws GameActionException
	{
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
		int totalEnemies= nearbyEnemies.length;
		for (int x= 0; x< totalEnemies; x++){
			if (rc.senseRobotInfo(nearbyEnemies[x]).location== rc.senseEnemyHQLocation()){
				totalEnemies--;
				break;
			}	
		}
			
		if (totalEnemies> 0){
			return true;
		}
		else
			return false;
	}
	
	public static void attackRandomNearByEnemies(RobotController rc) throws GameActionException
	{
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
		if (nearbyEnemies.length > 0) {
		ArrayList <MapLocation> bombSite= new ArrayList<MapLocation>();
			for (int x= 0; x< nearbyEnemies.length; x++){
				if (rc.senseRobotInfo(nearbyEnemies[x]).location!= rc.senseEnemyHQLocation())
					bombSite.add(rc.senseRobotInfo(nearbyEnemies[x]).location);
			}
			if (bombSite.size()> 0){
				MapLocation target= bombSite.get(randall.nextInt(bombSite.size()));
				rc.attackSquare(target);
				if (rc.senseObjectAtLocation(target).getTeam()== rc.getTeam().opponent())
					rc.broadcast(0, VectorFunctions.locToInt(target));
				else
					rc.broadcast(0, -1);
			}
		}
	}
	
	public static void hover(RobotController rc, int location, int radius) throws GameActionException
	{
	}
	
	public static void evade (RobotController rc) throws GameActionException
	{
		/*
		 * Add up how many directions are towards you, and chose randomly out of the most frequent ones to move in that direction
		 */
		Direction allDirections[] = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
		int directionCount[]= new int [8];
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
		
		if (nearbyEnemies.length> 0){
			for (int x= 0; x< nearbyEnemies.length; x++ ){
				for (int y= 0; y< allDirections.length; y++ ){
					if (rc.senseRobotInfo(nearbyEnemies[x]).location.directionTo(rc.getLocation())== allDirections[y]){
						 directionCount[y]++;
					}
				}
			}
			//Now find the largest numbers
			int largestCount= 0;
			for (int x= 0; x< directionCount.length; x++){
				if (directionCount[x]> largestCount){
					largestCount= directionCount[x];
				}
			}
			
			ArrayList<Direction> possDir= new ArrayList<Direction>();
			//Now find all the same numbers
			for (int x= 0; x< directionCount.length; x++){
				if (directionCount[x]== largestCount){
					possDir.add(allDirections[x]);
				}
			}
			
			//Now chose one to move randomly in
			Direction chosen= possDir.get(randall.nextInt(possDir.size()));
			BasicPathing.tryToMove(chosen, false, rc, directionalLooks, allDirections); //Or adjust to better move algorithm, so the guy doesn't get sandwiched
		}
	}
	
	public static boolean isEnemyPASTR(RobotController rc, int loc) throws GameActionException //Probably a better way to do this, but currently can't get RobotType of object at given location
	{
		boolean answer= false;
		MapLocation[] enemyPastureLoc= rc.sensePastrLocations(rc.getTeam().opponent());
		for (int x= 0; x< enemyPastureLoc.length; x++){
			if (VectorFunctions.intToLoc(loc)== enemyPastureLoc[x]){
				answer= true;
				break;
			}
		}
		return answer;
	}
	
	public static boolean isOwnPASTR(RobotController rc, int loc)  throws GameActionException //Probably a better way to do this, but currently can't get RobotType of object at given location
	{
		boolean answer= false;
		MapLocation[] ownPastureLoc= rc.sensePastrLocations(rc.getTeam());
		for (int x= 0; x< ownPastureLoc.length; x++){
			if (VectorFunctions.intToLoc(loc)== ownPastureLoc[x]){
				answer= true;
				break;
			}
		}
		return answer;
	}
	
	public static void attackNearestEnemy(RobotController rc){
		
	}
	
	private static MapLocation getRandomLocation(RobotController rc) {
		return new MapLocation(randall.nextInt(rc.getMapWidth()),randall.nextInt(rc.getMapHeight()));
	}
}
