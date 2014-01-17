package team164;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;

public class RobotPlayer{
	
	public static RobotController rc;
	static Random randall = new Random();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2};
	static int enemeyPastureIterate;
	
	static Direction allDirections[] = Direction.values();
	public static ArrayList<MapLocation> path;
	static int bigBoxSize = 5;
	static MapLocation goal;
	
	public static void run(RobotController rcin){
		rc = rcin;
		randall.setSeed(rc.getRobot().getID());
		
		if(rc.getType() == RobotType.SOLDIER) {
		BreadthFirst.init(rc, bigBoxSize);
		try {
		goal = locationServices.intToLoc(rc.readBroadcast(hq.getStatusIterator()+25));
		} catch (GameActionException g) {
			System.out.println("LocationException");
		}
		path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
		//VectorFunctions.printPath(path,bigBoxSize);
		}
		
		while(true){
			try{
				if(rc.getType()==RobotType.HQ){//if I'm a headquarters
					hq.run(rc);
				}else if(rc.getType()==RobotType.SOLDIER){
					if(path.size() == 0){
						goal = getRandomLocation();
						path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(), bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
						//System.out.println("random dest");
						//this prints once in a while but somehow nobody is pathing after some point
					}
					pastureExterminator.run(rc);
				}
				rc.yield();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private static MapLocation getRandomLocation() {
		return new MapLocation(randall.nextInt(rc.getMapWidth()),randall.nextInt(rc.getMapHeight()));
	}
}