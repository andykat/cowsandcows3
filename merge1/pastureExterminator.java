package merge1;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

import java.util.ArrayList;
import java.util.Random;


public class pastureExterminator
{
	static Random randall = new Random();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction[] allDirections = Direction.values();
	static ArrayList<MapLocation> path;
	static int bigBoxSize = 5;
	static MapLocation target;
	public static RobotController rc;
	
	public pastureExterminator(RobotController rcIn) {
		rc = rcIn;
		//initialize
		BreadthFirst.init(rc, bigBoxSize);
		target = getRandomLocation(rc);
		path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(target,bigBoxSize), 100000);
	}

	public static void run(RobotController rc)
			throws GameActionException
			{
		int goal = -1;
		if (rc.readBroadcast(0) != -1)
		{
			goal = rc.readBroadcast(rc.readBroadcast(0));
			rc.broadcast(0, rc.readBroadcast(0) + 1);
		}
		if (rc.isActive()) {
			if (thereAreNearbyEnemies(rc))
			{
				if (senseDangerStatus(rc) >= 0)
				{
					int channelZInfo = rc.readBroadcast(30);
					if (channelZInfo == -1)
					{
						attackRandomNearByEnemies(rc);
					}
					else if (rc.canAttackSquare(locationServices.intToLoc(channelZInfo)))
					{
						rc.attackSquare(locationServices.intToLoc(channelZInfo));
						if (!thereAreEnemyRobotsAtLocation(rc, locationServices.intToLoc(channelZInfo))) {
							rc.broadcast(30, -1);
						}
					}
					else if (thereAreNearbyEnemies(rc))
					{
						attackRandomNearByEnemies(rc);
					}
					else
					{
						//move towards nearby enemy?
						locationServices.simpleMove(rc, locationServices.intToLoc(channelZInfo));
					}
				}
				else
				{
					evade(rc);
				}
			}
			else if (goal != -1) {
				//these are pathing.  If goal:
				//locationServices.simpleMove(rc, locationServices.intToLoc(goal));
				MapLocation target = locationServices.intToLoc(goal);
				//if not at destination and destination != target, make new path
				if (path.size() != 0 && !path.get(path.size()-1).equals(VectorFunctions.mldivide(target,bigBoxSize))){
					path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(target,bigBoxSize), 100000);
				}
				//if at destination
				if(path.size()==0){
					//go somewhere random
					target = getRandomLocation(rc);
					path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(target,bigBoxSize), 100000);
				}
				//follow path
				Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
				locationServices.tryToMove(bdir, true, rc, directionalLooks, allDirections);
			} else {
				//move to random location
				locationServices.simpleMove(rc, locationServices.getRandomLocation(rc));
				if(path.size()==0){
					target = locationServices.getRandomLocation(rc);
					path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(target,bigBoxSize), 100000);
				}
				Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
				locationServices.tryToMove(bdir, true, rc, directionalLooks, allDirections);
			}
		}
			}

	public static boolean thereAreEnemiesAtLocation(RobotController rc, MapLocation target)
			throws GameActionException
			{
		if (rc.senseObjectAtLocation(target).getTeam() == rc.getTeam().opponent()) {
			return true;
		}
		return false;
			}

	public static boolean thereAreEnemyRobotsAtLocation(RobotController rc, MapLocation target)
			throws GameActionException
			{
		Robot[] nearbyEnemies = (Robot[])rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
		boolean answer = false;
		for (int x = 0; x < nearbyEnemies.length; x++) {
			if (rc.senseRobotInfo(nearbyEnemies[x]).location == target)
			{
				answer = true;
				break;
			}
		}
		return answer;
			}

	public static int senseDangerStatus(RobotController rc)
			throws GameActionException
			{
		int answer = 0;
		Robot[] nearbyEnemies = (Robot[])rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
		Robot[] nearbyAllies = (Robot[])rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam());
		MapLocation[] enemyPSTRLoc = rc.sensePastrLocations(rc.getTeam().opponent());
		int totalEnemies = nearbyEnemies.length;
		for (int x = 0; x < nearbyEnemies.length; x++)
		{
			if (rc.senseRobotInfo(nearbyEnemies[x]).location == rc.senseEnemyHQLocation()) {
				totalEnemies--;
			}
			for (int y = 0; y < enemyPSTRLoc.length; y++) {
				if (rc.senseRobotInfo(nearbyEnemies[x]).location == enemyPSTRLoc[y]) {
					totalEnemies--;
				}
			}
		}
		answer = nearbyAllies.length - totalEnemies;
		return answer;
			}

	public static boolean thereAreNearbyEnemies(RobotController rc)
			throws GameActionException
			{
		Robot[] nearbyEnemies = (Robot[])rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
		int totalEnemies = nearbyEnemies.length;
		for (int x = 0; x < totalEnemies; x++) {
			if (rc.senseRobotInfo(nearbyEnemies[x]).location == rc.senseEnemyHQLocation())
			{
				totalEnemies--;
				break;
			}
		}
		if (totalEnemies > 0) {
			return true;
		}
		return false;
			}

	public static void attackRandomNearByEnemies(RobotController rc)
			throws GameActionException
			{
		Robot[] nearbyEnemies = (Robot[])rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
		if (nearbyEnemies.length > 0)
		{
			ArrayList<MapLocation> bombSite = new ArrayList();
			for (int x = 0; x < nearbyEnemies.length; x++) {
				if (rc.senseRobotInfo(nearbyEnemies[x]).location != rc.senseEnemyHQLocation()) {
					bombSite.add(rc.senseRobotInfo(nearbyEnemies[x]).location);
				}
			}
			if (bombSite.size() > 0)
			{
				MapLocation target = (MapLocation)bombSite.get(randall.nextInt(bombSite.size()));
				rc.attackSquare(target);
				if (rc.senseObjectAtLocation(target).getTeam() == rc.getTeam().opponent()) {
					rc.broadcast(30, locationServices.locToInt(target));
				} else {
					rc.broadcast(30, -1);
				}
			}
		}
			}

	public static void hover(RobotController rc, int location, int radius)
			throws GameActionException
			{}

	public static void evade(RobotController rc)
			throws GameActionException
			{
		Direction[] allDirections = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };
		int[] directionCount = new int[8];
		Robot[] nearbyEnemies = (Robot[])rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
		if (nearbyEnemies.length > 0)
		{
			for (int x = 0; x < nearbyEnemies.length; x++) {
				for (int y = 0; y < allDirections.length; y++) {
					if (rc.senseRobotInfo(nearbyEnemies[x]).location.directionTo(rc.getLocation()) == allDirections[y]) {
						directionCount[y] += 1;
					}
				}
			}
			int largestCount = 0;
			for (int x = 0; x < directionCount.length; x++) {
				if (directionCount[x] > largestCount) {
					largestCount = directionCount[x];
				}
			}
			ArrayList<Direction> possDir = new ArrayList();
			for (int x = 0; x < directionCount.length; x++) {
				if (directionCount[x] == largestCount) {
					possDir.add(allDirections[x]);
				}
			}
			Direction chosen = (Direction)possDir.get(randall.nextInt(possDir.size()));
			//TODO changed this to have directionalLooks and allDirections
			locationServices.tryToMove(chosen, false, rc, directionalLooks, allDirections);
		}
			}

	public static boolean isEnemyPASTR(RobotController rc, int loc)
			throws GameActionException
			{
		boolean answer = false;
		MapLocation[] enemyPastureLoc = rc.sensePastrLocations(rc.getTeam().opponent());
		for (int x = 0; x < enemyPastureLoc.length; x++) {
			if (locationServices.intToLoc(loc) == enemyPastureLoc[x])
			{
				answer = true;
				break;
			}
		}
		return answer;
			}

	public static boolean isOwnPASTR(RobotController rc, int loc)
			throws GameActionException
			{
		boolean answer = false;
		MapLocation[] ownPastureLoc = rc.sensePastrLocations(rc.getTeam());
		for (int x = 0; x < ownPastureLoc.length; x++) {
			if (locationServices.intToLoc(loc) == ownPastureLoc[x])
			{
				answer = true;
				break;
			}
		}
		return answer;
			}

	public static void attackNearestEnemy(RobotController rc) {}

	public static MapLocation findClosestPasture(RobotController rc, MapLocation myLoc)
	{
		MapLocation[] enemyPastures = rc.sensePastrLocations(rc.getTeam().opponent());
		int chosenIndex = 0;
		if (enemyPastures.length > 0)
		{
			double closest = locationServices.distanceBetween(myLoc, enemyPastures[0]);
			for (int x = 1; x < enemyPastures.length; x++) {
				if (locationServices.distanceBetween(rc.getLocation(), myLoc) < closest)
				{
					closest = rc.getLocation().distanceSquaredTo(myLoc);
					chosenIndex = x;
				}
			}
		}
		return enemyPastures[chosenIndex];
	}
	
	private static MapLocation getRandomLocation(RobotController rc) {
		return new MapLocation(randall.nextInt(rc.getMapWidth()),randall.nextInt(rc.getMapHeight()));
	}
}