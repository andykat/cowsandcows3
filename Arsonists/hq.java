package Arsonists;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class hq
{
  static Direction[] allDirections = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };
  
  public static void run(RobotController rc)
    throws GameActionException
  {
    Direction spawnDir = allDirections[0];
    for (int x = 1; x < allDirections.length; x++) {
      if (rc.canMove(allDirections[x]))
      {
        spawnDir = allDirections[x];
        break;
      }
    }
    if ((rc.isActive()) && (rc.canMove(spawnDir)) && (rc.senseRobotCount() < 25)) {
      rc.spawn(spawnDir);
    }
    broadcastAssignments(rc);
  }
  
  public static void broadcastAssignments(RobotController rc)
    throws GameActionException
  {
    int numAlly = rc.senseRobotCount() - rc.sensePastrLocations(rc.getTeam()).length * 2;
    MapLocation[] enemyPastures = rc.sensePastrLocations(rc.getTeam().opponent());
    int numEnemyPastures = enemyPastures.length;
    if (numEnemyPastures == 0) {
      rc.broadcast(0, -1);
    } else {
      rc.broadcast(0, 1);
    }
    if (numEnemyPastures == 1) {
      for (int x = 0; x < numAlly; x++) {
        if (x < 18) {
          rc.broadcast(x + 1, locationServices.locToInt(enemyPastures[0]));
        }
      }
    } else if (numEnemyPastures == 2) {
      for (int x = 0; x < numAlly; x++) {
        if (x < 18) {
          if (x < numAlly / 2) {
            rc.broadcast(x + 1, locationServices.locToInt(enemyPastures[0]));
          } else {
            rc.broadcast(x + 1, locationServices.locToInt(enemyPastures[1]));
          }
        }
      }
    } else if (numEnemyPastures >= 3) {
      for (int x = 0; x < numAlly; x++) {
        if (x < 18) {
          if (x < numAlly / 3) {
            rc.broadcast(x + 1, locationServices.locToInt(enemyPastures[0]));
          } else if (x < 2 * (numAlly / 3)) {
            rc.broadcast(x + 1, locationServices.locToInt(enemyPastures[1]));
          } else {
            rc.broadcast(x + 1, locationServices.locToInt(enemyPastures[2]));
          }
        }
      }
    }
  }
}