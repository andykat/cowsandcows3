package Arsonists;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import java.util.ArrayList;

public class BreadthFirst
{
  public static RobotController rc;
  public static MapLocation enemy;
  public static MapLocation myLoc;
  public static int height;
  public static int width;
  public static Direction[][] pathingData;
  public static int[][] distanceData;
  public static int[][] mapData;
  public static ArrayList<MapLocation> path = new ArrayList();
  public static Direction[] dirs = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };
  public static boolean shortestPathLocated;
  public static RobotController rci;
  
  public static void init(RobotController rci, int bigBoxSize)
  {
    rc = rci;
    width = rc.getMapWidth() / bigBoxSize;
    height = rc.getMapHeight() / bigBoxSize;
    MapAssessment.assessMap(bigBoxSize, rci);
    

    updateInternalMap(rc);
  }
  
  private static int getMapData(int x, int y)
  {
    return mapData[(x + 1)][(y + 1)];
  }
  
  public static int getMapData(MapLocation m)
  {
    return getMapData(m.x, m.y);
  }
  
  private static void setMapData(int x, int y, int val)
  {
    mapData[(x + 1)][(y + 1)] = val;
  }
  
  private static void updateInternalMap(RobotController rc)
  {
    mapData = new int[width + 2][height + 2];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++)
      {
        int val = MapAssessment.coarseMap[x][y];
        if (val == MapAssessment.bigBoxSize * MapAssessment.bigBoxSize) {
          val = 0;
        } else {
          val += 10000;
        }
        setMapData(x, y, val);
      }
    }
  }
  
  public static ArrayList<MapLocation> pathTo(MapLocation start, MapLocation goal, int maxSearchDist)
  {
    shortestPathLocated = false;
    path = new ArrayList();
    pathingData = new Direction[width][height];
    distanceData = new int[width][height];
    ArrayList<MapLocation> outermost = new ArrayList();
    outermost.add(start);
    distanceData[start.x][start.y] = (-maxSearchDist * 10);
    while ((!shortestPathLocated) && (outermost.size() > 0)) {
      outermost = getNewOutermost(outermost, start, goal);
    }
    listDirections(start, goal);
    return path;
  }
  
  private static ArrayList<MapLocation> getNewOutermost(ArrayList<MapLocation> outermost, MapLocation start, MapLocation goal)
  {
    ArrayList<MapLocation> newOutermost = new ArrayList();
    ArrayList<Proposal> props = new ArrayList();
    for (MapLocation m : outermost) {
      Proposal.generateProposals(m, distanceData[m.x][m.y], 1, props, dirs);
    }
    for (Proposal p : props) {
      if (p.dist < distanceData[p.loc.x][p.loc.y])
      {
        if (distanceData[p.loc.x][p.loc.y] != 0) {
          newOutermost.remove(p.loc);
        }
        distanceData[p.loc.x][p.loc.y] = p.dist;
        pathingData[p.loc.x][p.loc.y] = p.dir;
        newOutermost.add(p.loc);
      }
    }
    return newOutermost;
  }
  
  private static void listDirections(MapLocation start, MapLocation end)
  {
    MapLocation currentLoc = end;
    while (!currentLoc.equals(start))
    {
      Direction d = pathingData[currentLoc.x][currentLoc.y];
      path.add(0, currentLoc);
      currentLoc = currentLoc.add(d.opposite());
    }
  }
  
  public static Direction getNextDirection(ArrayList<MapLocation> path, int bigBoxSize)
  {
    if (VectorFunctions.mldivide(rc.getLocation(), bigBoxSize).equals(path.get(0))) {
      path.remove(0);
    }
    return rc.getLocation().directionTo(VectorFunctions.bigBoxCenter((MapLocation)path.get(0), bigBoxSize));
  }
}