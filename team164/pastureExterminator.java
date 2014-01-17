package team164;

import java.util.ArrayList;
import java.util.Random;


//import coarsenserPlayer.BreadthFirst;
//import coarsenserPlayer.VectorFunctions;
import battlecode.common.*;

public class pastureExterminator {
	static Random randall = new Random();
	static Direction allDirections[] = Direction.values();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	static ArrayList<MapLocation> path;
	static int bigBoxSize = 5;
	static MapLocation goal;
	
	public static void run(RobotController rc) throws GameActionException {
		/*1. Download pasture assignment (i.e. an integer from HQ, signifying the closest pasture available)
		 *2. Find path to said location, use a smarter algorithm, maybe Dijkstra
		 *3. Do team move. Attack in patterns of set number of team mates (let's say 5 for now, and they get replenished)
		 *	a. If team members die the HQ replenishes the team's members, members retreat from fights if too low on people and they sense more people, or stay put and don't move
		 *4. Attack and report destruction (if alive), when HQ gets notification HQ reassigns attack targets
		 */
		//System.out.println();
		if (rc.isActive()){
				rc.broadcast(hq.getStatusIterator(), locationServices.locToInt(rc.getLocation()));//Tell hq your position
		//Attack enemies first, then attack pastures
			if (thereAreNearbyEnemies(rc)){
				attackNearByEnemies(rc);
			}
			
			path = RobotPlayer.path;
			
			if (rc.readBroadcast(rc.getRobot().getID())== 0){
				if (rc.readBroadcast(hq.getStatusIterator()+25)!= -1){
					MapLocation target= locationServices.intToLoc(hq.getStatusIterator()+25);
				if (rc.canAttackSquare(target)) {
					rc.attackSquare(target);
					if (rc.senseObjectAtLocation(target)== null){
						rc.broadcast(hq.getStatusIterator()+25, -1);
					}
				}
				else{
					//locationServices.tryToMove(rc.getLocation().directionTo(locationServices.intToLoc(rc.readBroadcast(hq.getStatusIterator()+25))), false, rc);
					//Write a better move to location function later

					//follow breadthFirst path
					if (path.size() != 0) {
						Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
						locationServices.tryToMove(bdir, true, rc);
					}
				}		
				hq.iterate(rc);
				}
			}
			else
			{
				locationServices.simpleMove(rc, locationServices.getRandomLocation(rc));//Move randomly
				rc.broadcast(rc.getRobot().getID(), rc.readBroadcast(rc.getRobot().getID())-1);
			}
		}
	}
	
	public static int senseDangerStatus(RobotController rc)
	{
		int answer= 0;
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
		Robot[] nearbyAllies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam());
		answer= nearbyAllies.length- nearbyEnemies.length;
		return answer;
	}
	
	public static boolean thereAreNearbyEnemies(RobotController rc)
	{
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
		if (nearbyEnemies.length> 0)
		{
			return true;
		}
		else
			return false;
	}
	
	public static void attackNearByEnemies(RobotController rc) throws GameActionException
	{
		/*
		 * I put in some of my fixes to prevent them getting stuck attacking the HQ,
		 * and to have them check that an enemy is in range before shooting.
		 * Might eventually want to add in some of the others to go after closest enemy?
		 */
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
		if (nearbyEnemies.length > 0) {
			RobotInfo robotInfo = rc.senseRobotInfo(nearbyEnemies[randall.nextInt(nearbyEnemies.length)]);
			if (robotInfo.type != RobotType.HQ) {
				if(robotInfo.location.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared) {
					rc.attackSquare(robotInfo.location); //attack if in range
				} else { //if not, try to get closer
					locationServices.simpleMove(rc, robotInfo.location);
				}
			} 
			}
	}
	
}
