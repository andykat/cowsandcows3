package allOutAttack;

import java.util.ArrayList;

import coarsenserPlayer.RobotPlayer;
import battlecode.common.*;

/*
 * Started with BasicPathing from coarsenserPlayer
 * Modifying for testing allOutAttack - probably only 
 * good for local pathing.
 */

public class Path{
	
	static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();
	
	public static boolean canMove(Direction dir, boolean selfAvoiding,RobotController rc){
		//include both rc.canMove and the snail Trail requirements
		if(selfAvoiding){
			MapLocation resultingLocation = rc.getLocation().add(dir);
			for(int i=0;i<snailTrail.size();i++){
				MapLocation m = snailTrail.get(i);
				if(!m.equals(rc.getLocation())){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
						return false;
					}
				}
			}
		}
		//if you get through the loop, then dir is not adjacent to the icky snail trail
		return rc.canMove(dir);
	}
	
	public static void tryToMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc, int[] directionalLooks, Direction[] allDirections) throws GameActionException{
		while(snailTrail.size()<2)
			snailTrail.add(new MapLocation(-1,-1));
		if(rc.isActive()){
			//snail trail stays constant at length 2
			snailTrail.remove(0);
			snailTrail.add(rc.getLocation());
			//propose action, check if on trail
			for(int directionalOffset:directionalLooks){
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(canMove(trialDir,selfAvoiding,rc)){
					rc.move(trialDir);
					//snailTrail.remove(0);
					//snailTrail.add(rc.getLocation());
					break;
				}
			}
			//System.out.println("I am at "+rc.getLocation()+", trail "+snailTrail.get(0)+snailTrail.get(1)+snailTrail.get(2));
		}
	}
	
	public static void simpleMove(RobotController rc, MapLocation target) throws GameActionException{
		int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
		Direction chosenDirection = rc.getLocation().directionTo(target);
		for(int directionalOffset:directionalLooks){
			int forwardInt = chosenDirection.ordinal();
			Direction trialDir = Direction.values()[(forwardInt+directionalOffset+8)%8];
			if(rc.canMove(trialDir)){
				rc.move(trialDir);
				break;
			}
		}
	}
	
}