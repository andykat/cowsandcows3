package team164;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;


public class RobotPlayer{
	
	public static RobotController rc;
	static Random randall = new Random();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction allDirections[] = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	//static int enemyPastureIterate;
	static int bigBoxSize = 5;
	static ArrayList<MapLocation> path;
	//static MapLocation goal;
	
	public static void run(RobotController rcin){
		rc = rcin;
		randall.setSeed(rc.getRobot().getID());
		
		if(rc.getType()==RobotType.HQ){
			try {
				hq.run(rc);
			} catch (GameActionException g) {
				System.out.println("initial hq derp");
			}
		}else{
			//BreadthFirst.init(rc, bigBoxSize);
			//MapLocation goal = getRandomLocation();
			//path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
			//VectorFunctions.printPath(path,bigBoxSize);
		}
		
		
		while(true){
			try{
				if(rc.getType()==RobotType.HQ){//if I'm a headquarters
					hq.run(rc);
					//runHQ();
				}else if(rc.getType()==RobotType.SOLDIER){
					pastureExterminator PE = new pastureExterminator(rc);
					for (;;) {
						PE.run(rc);
						rc.yield();
					}
					
					//runPastureExterminator(rc);
					//runSoldier();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}
	}
	
	
	public static void runPastureExterminator(RobotController rc) throws GameActionException {
		
		/*
		 *Moving pastureExterminator.java contents here - wasn't working otherwise.
		 *I have yet to find a workaround with the run method in a separate file that
		 *avoids absolute bytecode screwage by recomputing the map each time 
		 */
		
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
			else { //if not active
				//if there are enemy pastures
				if (rc.sensePastrLocations(rc.getTeam().opponent()).length> 0) {
					//short range pathfinding simpleMove
					//locationServices.simpleMove(rc, rc.sensePastrLocations(rc.getTeam().opponent())[0]);
					MapLocation goal = rc.sensePastrLocations(rc.getTeam().opponent())[0];
					//if you have a path and the path is not to the targeted pasture
					if (path.size() != 0 && !path.get(path.size()-1).equals(VectorFunctions.mldivide(goal,bigBoxSize))){
						path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
						//System.out.println("recalculate");
						//go after that pasture
					}
					//if at destination
					if(path.size()==0){
						//go somewhere random
						goal = getRandomLocation();
						path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(rc.senseEnemyHQLocation(),bigBoxSize), 100000);
					}
					Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
					BasicPathing.tryToMove(bdir, true, rc, directionalLooks, allDirections);
				} else { //if there are no enemy pastures
					if(path.size()==0){
						MapLocation goal = getRandomLocation();
						path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
					}
					Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
					BasicPathing.tryToMove(bdir, true, rc, directionalLooks, allDirections);
				}
				//follow breadthFirst path
				//getting array index out of bounds here
				//Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
				//locationServices.tryToMove(bdir, true, rc, directionalLooks, allDirections);
				}
			
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
	
	private static MapLocation getRandomLocation() {
		return new MapLocation(randall.nextInt(rc.getMapWidth()),randall.nextInt(rc.getMapHeight()));
	}
	private static void runSoldier() throws GameActionException {
		//follow orders from HQ
		//Direction towardEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		//BasicPathing.tryToMove(towardEnemy, true, rc, directionalLooks, allDirections);//was Direction.SOUTH_EAST
		
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,10000,rc.getTeam().opponent());
		if(enemyRobots.length>0){//if there are enemies
			rc.setIndicatorString(0, "There are enemies");
			MapLocation[] robotLocations = new MapLocation[enemyRobots.length];
			for(int i=0;i<enemyRobots.length;i++){
				Robot anEnemy = enemyRobots[i];
				RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
				robotLocations[i] = anEnemyInfo.location;
			}
			MapLocation closestEnemyLoc = VectorFunctions.findClosest(robotLocations, rc.getLocation());
			if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
				rc.setIndicatorString(1, "trying to shoot");
				if(rc.isActive()){
					rc.attackSquare(closestEnemyLoc);
				}
			}else{
				rc.setIndicatorString(1, "trying to go closer");
				Direction towardClosest = rc.getLocation().directionTo(closestEnemyLoc);
				simpleMove(towardClosest);
			}
		}else{

			if(path.size()==0){
				MapLocation goal = getRandomLocation();
				path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(rc.senseEnemyHQLocation(),bigBoxSize), 100000);
			}
			//follow breadthFirst path
			Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
			BasicPathing.tryToMove(bdir, true, rc, directionalLooks, allDirections);
		}
		//Direction towardEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		//simpleMove(towardEnemy);
		
	}
	
	private static void simpleMove(Direction chosenDirection) throws GameActionException{
		for(int directionalOffset:directionalLooks){
			int forwardInt = chosenDirection.ordinal();
			Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
			if(rc.canMove(trialDir)){
				rc.move(trialDir);
				break;
			}
		}
	}
	private static void runHQ() throws GameActionException {
		//tell robots where to go
		tryToSpawn();
	}

	public static void tryToSpawn() throws GameActionException {
		if(rc.isActive()&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
			for(int i=0;i<8;i++){
				Direction trialDir = allDirections[i];
				if(rc.canMove(trialDir)){
					rc.spawn(trialDir);
					break;
				}
			}
		}
	}
	
}

//}