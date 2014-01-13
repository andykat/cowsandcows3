package Arsonists;

import java.util.Random;
import java.util.ArrayList;

import battlecode.common.*;

public class locationServices {
	/*
	 * CURRENT PROBLEM: does not implement BFS.  Is like allOutAttack in that only
	 * short range pathing works - issues with long range happen when you
	 * try to use short range algorithm for long range movement.
	 */
	static int directionalLooks[] = new int[]{0,1,-1,2,-2};
	static Direction allDirections[] = Direction.values();
	
	static Random randall = new Random();
	public static MapLocation mladd(MapLocation m1, MapLocation m2){
		return new MapLocation(m1.x+m2.x,m1.y+m2.y);
	}
	
	public static MapLocation mldivide(MapLocation bigM, int divisor){
		return new MapLocation(bigM.x/divisor, bigM.y/divisor);
	}
	
	public static int locToInt(MapLocation m){
		return (m.x*100 + m.y);
	}
	
	public static MapLocation intToLoc(int i){
		return new MapLocation(i/100,i%100);
	}
	
	public static double distanceBetween (MapLocation pointA, MapLocation pointB){
		return Math.sqrt(Math.pow(pointA.x- pointB.x, 2) + Math.pow(pointA.y- pointB.y, 2));
	}
	
	public static MapLocation findClosestRobot(Robot[] nearbyEnemies, MapLocation myLoc, RobotController rc) throws GameActionException
	{
		int closestDist= 1000000;//Apparently so  you know when to give up
		int challengerDist= closestDist; 
		MapLocation closestLoc= null;
		for (Robot r: nearbyEnemies){
			challengerDist= myLoc.distanceSquaredTo(rc.senseLocationOf(r));
			if (challengerDist< closestDist){
				closestDist= challengerDist;
			}
		}
		return closestLoc;
	}
	
	public static MapLocation findClosest(MapLocation[] nearbyEnemies, MapLocation myLoc)
	{
		int closestDist= 1000000;//Apparently so  you know when to give up
		int challengerDist= closestDist; 
		MapLocation closestLoc= null;
		for (MapLocation m: nearbyEnemies){
			challengerDist= myLoc.distanceSquaredTo(m);
			if (challengerDist< closestDist){
				closestDist= challengerDist;
			}
		}
		return closestLoc;
	}

	public static void simpleMove(RobotController rc, MapLocation target) throws GameActionException{
		int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
		Direction chosenDirection = rc.getLocation().directionTo(target);
		for(int directionalOffset:directionalLooks){
			int forwardInt = chosenDirection.ordinal();
			Direction trialDir = Direction.values()[(forwardInt+directionalOffset+8)%8];
			if(rc.canMove(trialDir)){
				rc.move(trialDir);
				break;
			}
		}
	}
	
	public static MapLocation getRandomLocation(RobotController rc){
		return new MapLocation(randall.nextInt(rc.getMapWidth()), randall.nextInt(rc.getMapHeight()));
	}
	
	static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();
	
	public static boolean canMove(Direction dir, boolean selfAvoiding,RobotController rc){
		//include both rc.canMove and the snail Trail requirements
		if(selfAvoiding){
			MapLocation resultingLocation = rc.getLocation().add(dir);
			for(int i=0;i<snailTrail.size();i++){
				MapLocation m = snailTrail.get(i);
				if(!m.equals(rc.getLocation())){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
						return false;
					}
				}
			}
		}
		//if you get through the loop, then dir is not adjacent to the icky snail trail
		return rc.canMove(dir);
	}
	
	public static void tryToMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc) throws GameActionException{
		while(snailTrail.size()<2)
			snailTrail.add(new MapLocation(-1,-1));
		if(rc.isActive()){
			//snailTrail.remove(0);
			//snailTrail.add(rc.getLocation());
			for(int directionalOffset:directionalLooks){
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(canMove(trialDir,selfAvoiding,rc)){
					rc.move(trialDir);
					snailTrail.remove(0);
					snailTrail.add(rc.getLocation());
					break;
				}
			}
			//System.out.println("I am at "+rc.getLocation()+", trail "+snailTrail.get(0)+snailTrail.get(1)+snailTrail.get(2));
		}
	}
	
}
