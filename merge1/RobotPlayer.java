package merge1;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer
{
  public static RobotController rc;

  public static int rType;
  public static String rClass;
  public static String rClassOrder[] = {"p","s","s","f","s","s","s","s"}; // p - pasture maker;  s - soldier;  f - farmer
  public static void run(RobotController rcin)
  {
    rc = rcin;

	
    try
    {
    	
    	if (rc.getType() == RobotType.HQ) {
    		for (;;)
        	{
    			hq.run(rc);
    			rc.yield();
        	}
		}
		else if (rc.getType() == RobotType.SOLDIER) {
			int robotCount=0;
    		robotCount = rc.readBroadcast(1)+1;
			rc.broadcast(1, robotCount);
			rType = robotCount;
			rClass = rClassOrder[(rType-1)%8];
			
			if(rClass.equals("p"))
			{
				PastureMaker PM = new PastureMaker(rc, rType);
				for (;;)
		    	{
					PM.run();
					rc.yield();
		    	}
			}
			else if(rClass.equals("s"))
			{
				pastureExterminator PE = new pastureExterminator(rc);
				for (;;)
		    	{
					PE.run(rc);
					rc.yield();
		    	}
			}
			else if(rClass.equals("f"))
			{
				Farmer FM = new Farmer(rc, rType);
				for (;;)
		    	{
					FM.run();
					rc.yield();
		    	}
			}
			
		}
		else if (rc.getType() == RobotType.PASTR) {
			for (;;)
	    	{
				pasture.run(rc);
				rc.yield();
	    	}
		}
    }
    catch (Exception e)
    {
    	e.printStackTrace();
    }
  }
}