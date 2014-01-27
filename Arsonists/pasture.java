package Arsonists;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class pasture
{
  public static void run(RobotController rc)
    throws GameActionException
  {
    int numSurroundingEnemies = senseNumSurroundingEnemyRobots(rc);
    if (rc.getHealth() < numSurroundingEnemies * 10) {
      rc.selfDestruct();
    }
    if (rc.getHealth() < 20.0D) {
      rc.selfDestruct();
    }
  }
  
  public static int senseNumSurroundingEnemyRobots(RobotController rc)
    throws GameActionException
  {
    Robot[] nearbyEnemies = (Robot[])rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
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
    return totalEnemies;
  }
}