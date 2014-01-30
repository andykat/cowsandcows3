package merge1;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import java.util.ArrayList;

public class Proposal
{
  public MapLocation loc;
  public Direction dir;
  public int dist;
  
  public Proposal(MapLocation toMapLoc, Direction fromDirection, int fromDistance)
  {
    this.loc = toMapLoc;
    this.dir = fromDirection;
    this.dist = fromDistance;
  }
  
  public static void generateProposals(MapLocation locus, int distToLocus, int incrementalDist, ArrayList<Proposal> proposalList, Direction[] consideredDirs)
  {
    for (Direction d : consideredDirs)
    {
      Proposal p;
      //Proposal p;
      if (d.isDiagonal()) {
        p = new Proposal(locus.add(d), d, distToLocus + incrementalDist * 14);
      } else {
        p = new Proposal(locus.add(d), d, distToLocus + incrementalDist * 10);
      }
      int val = BreadthFirst.getMapData(p.loc);
      if (val > 0)
      {
        p.dist += (val - 10000) * 10;
        proposalList.add(p);
      }
    }
  }
}