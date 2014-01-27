package Arsonists;

import battlecode.common.MapLocation;
import java.io.PrintStream;
import java.util.ArrayList;

public class VectorFunctions
{
  public static MapLocation findClosest(MapLocation[] manyLocs, MapLocation point)
  {
    int closestDist = 10000000;
    int challengerDist = closestDist;
    MapLocation closestLoc = null;
    MapLocation[] arrayOfMapLocation = manyLocs;int j = manyLocs.length;
    for (int i = 0; i < j; i++)
    {
      MapLocation m = arrayOfMapLocation[i];
      challengerDist = point.distanceSquaredTo(m);
      if (challengerDist < closestDist)
      {
        closestDist = challengerDist;
        closestLoc = m;
      }
    }
    return closestLoc;
  }
  
  public static MapLocation mladd(MapLocation m1, MapLocation m2)
  {
    return new MapLocation(m1.x + m2.x, m1.y + m2.y);
  }
  
  public static MapLocation mldivide(MapLocation bigM, int divisor)
  {
    return new MapLocation(bigM.x / divisor, bigM.y / divisor);
  }
  
  public static MapLocation mlmultiply(MapLocation bigM, int factor)
  {
    return new MapLocation(bigM.x * factor, bigM.y * factor);
  }
  
  public static int locToInt(MapLocation m)
  {
    return m.x * 100 + m.y;
  }
  
  public static MapLocation intToLoc(int i)
  {
    return new MapLocation(i / 100, i % 100);
  }
  
  public static void printPath(ArrayList<MapLocation> path, int bigBoxSize)
  {
    for (MapLocation m : path)
    {
      MapLocation actualLoc = bigBoxCenter(m, bigBoxSize);
      System.out.println("(" + actualLoc.x + "," + actualLoc.y + ")");
    }
  }
  
  public static MapLocation bigBoxCenter(MapLocation bigBoxLoc, int bigBoxSize)
  {
    return mladd(mlmultiply(bigBoxLoc, bigBoxSize), new MapLocation(bigBoxSize / 2, bigBoxSize / 2));
  }
}