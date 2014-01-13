package Arsonists;

import battlecode.common.*;

import java.util.Random;

public class RobotPlayer{
	
	public static RobotController rc;
	static Random randall = new Random();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2};
	static int enemeyPastureIterate;
	
	public static void run(RobotController rcin){
		rc = rcin;
		randall.setSeed(rc.getRobot().getID());
		while(true){
			try{
				if(rc.getType()==RobotType.HQ){//if I'm a headquarters
					hq.run(rc);
				}else if(rc.getType()==RobotType.SOLDIER){
					pastureExterminator.run(rc);
				}
				rc.yield();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}