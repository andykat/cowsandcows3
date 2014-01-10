package allOutAttack;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	static Random rand;
	static MapLocation enemyHQ;
	
	//taken from coarsenser
	static Direction allDirections[] = Direction.values();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	
	//start with examplefuncsplayer, attempt to modify
	public static void run(RobotController rc) {
		rand = new Random();
		Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
		
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
					if (rc.isActive()) {
						int action = (rc.getRobot().getID()*rand.nextInt(101) + 50)%101;
						//never construct PASTRs
						if (action < 30) {
							Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
							if (nearbyEnemies.length > 0) {;
								RobotInfo robotInfo = rc.senseRobotInfo(nearbyEnemies[0]);
								MapLocation enemy = robotInfo.location;
								rc.attackSquare(enemy);
								//add code to move towards enemy unit being attacked
								System.out.println("moving towards enemy");
								Path.simpleMove(rc, enemy);
							} else if (action<80){
								//move towards enemy HQ
								System.out.println("going for HQ");
								Direction dir = rc.getLocation().directionTo(enemyHQ);
								Path.tryToMove(dir, true, rc, directionalLooks, allDirections);
							}
						//Move in a random direction
						} else if (action < 80) {
							Direction moveDirection = directions[rand.nextInt(8)];
							if (rc.canMove(moveDirection)) {
								rc.move(moveDirection);
							}
						//Sneak towards the enemy
						} else {
							Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
							if (rc.canMove(toEnemy)) {
								rc.sneak(toEnemy);
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Soldier Exception");
				}
			}
			
			rc.yield();
		}
	}
}
