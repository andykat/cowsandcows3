package Arsonists;

import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import java.util.Random;

public class RobotPlayer
{
  public static RobotController rc;
  static Random randall = new Random();
  static int[] directionalLooks = { 0, 1, -1, 2, -2 };
  static int enemeyPastureIterate;
  
  public static void run(RobotController rcin)
  {
    rc = rcin;
    randall.setSeed(rc.getRobot().getID());
    try
    {
      for (;;)
      {
        if (rc.getType() == RobotType.HQ) {
          hq.run(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
          pastureExterminator.run(rc);
        } else if (rc.getType() == RobotType.PASTR) {
          pasture.run(rc);
        }
        rc.yield();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}