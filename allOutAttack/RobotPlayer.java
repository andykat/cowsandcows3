package allOutAttack;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	static Random rand;
	static MapLocation enemyHQ;

	//taken from coarsenser
	static Direction allDirections[] = Direction.values();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

	//start with examplefuncsplayer, attempt to modify
	public static void run(RobotController rc) {
		rand = new Random();

		while(true) {
			if (rc.getType() == RobotType.HQ) {
				try {	
					//first round only - find enemy HQ
					//This works as intended.
					if (Clock.getRoundNum() == 0 && rc.isActive()) {
						enemyHQ = rc.senseEnemyHQLocation();
					}
					//Check if a robot is spawnable and spawn one if it is
					if (rc.isActive() && rc.senseRobotCount() < GameConstants.MAX_ROBOTS) {
						Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null) {
							rc.spawn(toEnemy);
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
				}
			}

			if (rc.getType() == RobotType.SOLDIER) {
				try {
				runSoldier(rc);
				} catch (Exception e) {
					System.out.println("SoldierException");
				}
			}

			rc.yield();
		}
	}


	//code for running soldiers.
	private static void runSoldier(RobotController rc) throws GameActionException {
			//never construct PASTRs
			Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
			if (nearbyEnemies.length > 0) {
				MapLocation[] robotLocations = new MapLocation[nearbyEnemies.length];
				for(int i=0;i<nearbyEnemies.length;i++){
					Robot anEnemy = nearbyEnemies[i];
					RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
					if (anEnemyInfo.type != RobotType.HQ){
						//only consider non-HQ enemy units
						robotLocations[i] = anEnemyInfo.location;
					} 
				}
				MapLocation closestEnemyLoc = VectorFunc.findClosest(robotLocations, rc.getLocation());
				if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
					System.out.println("trying to shoot");
					if(rc.isActive()){
						rc.attackSquare(closestEnemyLoc);
						//Path.simpleMove(rc, closestEnemyLoc); //chase
					}
				} else {//enemies sensed but not in range
					System.out.println("trying to get in range");
					Path.simpleMove(rc, closestEnemyLoc);
					}
				
		} else { //no nearby enemies
			//System.out.println("going for HQ"); //code for seeking HQ is broken
			//Direction dir = rc.getLocation().directionTo(enemyHQ);
			//Path.tryToMove(dir, true, rc, directionalLooks, allDirections);
			Direction moveDirection = directions[rand.nextInt(8)];
			if (rc.canMove(moveDirection)) {
				rc.move(moveDirection); //random direction
			}
			
		}
	} 
}
