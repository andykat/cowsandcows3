package merge1;

import battlecode.common.*;

import java.util.*;


public class PastureMaker {
	public static RobotController rc;
	
	public static Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
									  Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
									 };
	public static int[] int_dirs_X = {0,1,1,1,0,-1,-1,-1};
	public static int[] int_dirs_Y = {-1,-1,0,1,1,1,0,-1};
	static Random rand;
	
	public static int height;
	public static int width;
	
	public static int rType;
	public static int rType_s;
	public static int rX;
	public static int rY;
	public static int[][] rVisited;
	public static String rAction;
	public static int rDestinationX;
	public static int rDestinationY;
	
	//pasture Variables
	public static int corner_Index;
	public static int pastureX;
	public static int pastureY;
	public static int cow_push_type;
	public static int[] push_init_loc_X = { 0,-1, 1,-1};
	public static int[] push_init_loc_Y = {-1, 0,-1, 1};
	public static int pastureDir;
	public static int push_Radius = 4;
	
	public static int move_Outside_Count;
	public static double[] Map_Corners_X = {1.0,1.0,1.0,1.0};
	public static double[] Map_Corners_Y = {1.0,1.0,1.0,1.0};
	
	public static int cornerFails;
	
	public PastureMaker(RobotController tRc, int tRType)
	{
		rc = tRc;
		rType = tRType;
		cornerFails = 0;
		PM_init();
	}
	
	public void run() throws GameActionException 
	{
		rX = rc.getLocation().x;
		rY = rc.getLocation().y;
		
		if(rc.isActive())
		{
			if(rAction.equals("init"))
			{
				
				Map_Corners_X[2] = ((double)width)-2.0;
				Map_Corners_X[3] = ((double)width)-2.0;
				Map_Corners_Y[1] = ((double)height)-2.0;
				Map_Corners_Y[2] = ((double)height)-2.0;
				double min_dist=0;
				int index=-1;
				double hqX = (double)rc.senseHQLocation().x;
				double hqY = (double)rc.senseHQLocation().y;
				int rx = 0;
				int ry = 0;
				int corners = rType/2+1;
				System.out.println("cornerblah:" + corners);
				for(int j=0;j<corners+cornerFails;j++)
				{
					index = -1;
					min_dist=1000000.0;
					for(int i=0;i<4;i++)
					{
						double cornerdist = (hqX-Map_Corners_X[i])*(hqX-Map_Corners_X[i]) + (hqY-Map_Corners_Y[i])*(hqY-Map_Corners_Y[i]);
						if(cornerdist<min_dist)
						{
							min_dist = cornerdist;
							index = i;
						}
					}
					rx = (int) Map_Corners_X[index];
					ry = (int) Map_Corners_Y[index];
					Map_Corners_X[index] = 305.0;
					Map_Corners_Y[index] = 305.0;
				}
				
				if(index==-1)
				{
					rAction = "fail";
					return;
				}
				else
				{
					rDestinationX = rx;
					rDestinationY = ry;
					System.out.println("mx:" + rDestinationX);
					System.out.println("my:" + rDestinationY);
				}
				
				rAction = "moving";
			}
			else if(rAction.equals("moving"))
			{
				move(true);
			}
			else if(rAction.equals("makePasture"))
			{
				rc.construct(RobotType.PASTR);
				
				rAction = "done";
			}
			
		}
	}
	
	private void PM_init()
	{
		rand = new Random(rc.getRobot().getID());
		
		
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		
		
		rVisited = new int[width][height]; //saving visited locations
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				rVisited[i][j] = -1;
			}
		}
		rAction = "init";
		rType_s = 1;
	}
	
	public static void move(boolean mFlag) throws GameActionException 
	{
		//found destination
		if(rDestinationX == rX && rDestinationY == rY)
		{
			for(int i=0;i<width;i++)
			{
				for(int j=0;j<height;j++)
				{
					rVisited[i][j] = -1;
				}
			}
			
			if(rType_s==1)
			{
					//found corner
				
					//check if pasture is empty
					MapLocation currentLoc = new MapLocation(rX,rY);
					if(rc.senseCowsAtLocation(currentLoc)<10.0)
					{
						rAction = "init";
						cornerFails++;
						return;
					}
				
					int Ncorners = rc.readBroadcast(3) + 1;
					rc.broadcast(3, Ncorners);
					rc.broadcast(10 + Ncorners, rX*100 + rY);
					
					int tdirection = 0;
					if(rX == 1 && rY ==1)
					{
						tdirection = 7;
					}
					else if(rX == width - 2 && rY == 1)
					{
						tdirection = 1;
					}
					else if(rX == width - 2 && rY == height - 2)
					{
						tdirection = 3;
					}
					else if(rX == 1 && rY == height - 2)
					{
						tdirection = 5;
					}
					/*if(rType==1)
					{
						tdirection = 7;
					}
					if(rType==3)
					{
						tdirection = 1;
					}*/
					System.out.println("direction:" + tdirection);
					rc.broadcast(20+Ncorners, tdirection);
					
					rAction = "makePasture";
			}
			else if(rType_s==2)
			{
				if(rAction.equals("moveToPushLoc"))
				{
					rAction = "wait";
					int current_CowPushers = rc.readBroadcast(30+corner_Index);
					//rc.broadcast(30+corner_Index, current_CowPushers+10);
				}
				else if(rAction.equals("push") || rAction.equals("return"))
				{
					rAction = "wait";
					int current_CowPushers = rc.readBroadcast(30+corner_Index);
					//rc.broadcast(30+corner_Index, current_CowPushers+10);
					//if(current_CowPushers+10>20)
					//{
						int temp_dir = rc.readBroadcast(40+corner_Index);
						rc.broadcast(40+corner_Index, temp_dir + 1);
					//}
				}
			}
			return;
		}
		
		//find direction to destination
		Direction moveDirection = dirs[0];
		
		double dx = ((double)rDestinationX) - ((double)rX);
		double dy = ((double)rDestinationY) - ((double)rY);
		
		double angle = Math.atan(dy/dx);
		if(dx<0.0)
		{
			angle += Math.PI;
		}
		
		int cur_direction =(int) Math.round(angle * 4.0 / Math.PI) + 2;
		if(cur_direction<0)
		{
			cur_direction += 8;
		}
		else if(cur_direction>7)
		{
			cur_direction -= 8;
		}
		boolean movedFlag = false;
		int cornerFlag = 0;
		double cornerXSum = 0;
		double cornerYSum = 0;
		//find closest direction
		if(rType==1)
		{
			if(rX == 2 && rY == 2)	System.out.println("rx: " + rX + " ry: " + rY);
		}
		for(int i=1;i<9;i++)
		{
			if(rType==1)
			{
				if(rX == 2 && rY == 2)	System.out.println("conerf:" + cornerFlag);
			}
			int test_direction = i;
			if(test_direction%2 == 1)
			{
				test_direction = -test_direction;
			}
			test_direction = test_direction/2 + cur_direction;
			if(test_direction<0)
			{
				test_direction += 8;
			}
			else if(test_direction>7)
			{
				test_direction -= 8;
			}
			
			moveDirection = dirs[test_direction];
			
			if (rc.canMove(moveDirection)) {
				if(rVisited[rX + int_dirs_X[test_direction]][rY + int_dirs_Y[test_direction]]==-1/* || rVisited[rX + int_dirs_X[test_direction]][rY + int_dirs_Y[test_direction]]!= test_direction*/)
				{
					if(rType_s==2)
					{
						int cur_dist = (rDestinationX - rX)*(rDestinationX - rX) + (rDestinationY - rY)*(rDestinationY - rY);
						int new_dist = (rDestinationX - (rX + int_dirs_X[test_direction]))*(rDestinationX - (rX + int_dirs_X[test_direction])) + (rDestinationY - (rY + int_dirs_Y[test_direction]))*(rDestinationY - (rY + int_dirs_Y[test_direction]));
						if(new_dist>cur_dist)
						{
							if(rAction.equals("moveToPushLoc"))
							{
								rAction = "wait";
								int current_CowPushers = rc.readBroadcast(30+corner_Index);
								//rc.broadcast(30+corner_Index, current_CowPushers+10);
							}
							else if(rAction.equals("push") || rAction.equals("return"))
							{
								rAction = "wait";
								int current_CowPushers = rc.readBroadcast(30+corner_Index);
								//rc.broadcast(30+corner_Index, current_CowPushers+10);
						//		if(current_CowPushers+10>20)
						//		{
									int temp_dir = rc.readBroadcast(40+corner_Index);
									rc.broadcast(40+corner_Index, temp_dir + 1);
						//		}
							}
							return;
						}
					}
					//move!
					if(!movedFlag)
					{
						if(mFlag)
						{
							rc.move(moveDirection);
						}
						else
						{
							rc.sneak(moveDirection);
						}
					}
					if(rVisited[rX + int_dirs_X[test_direction]][rY + int_dirs_Y[test_direction]]==-1)
					{
						rVisited[rX + int_dirs_X[test_direction]][rY + int_dirs_Y[test_direction]] = test_direction;
					}
					if(rType_s==1)
					{
						
						
						movedFlag = true;
						if(cornerFlag == 0)
						{
							return;
						}
					}
					else
					{
						return;
					}
				}
				
			}
			else
			{
				if(rType_s==1)
				{
					if(rType==1)
					{
						if(rX == 2 && rY == 2)	System.out.println("dir:" + test_direction);
					}
					cornerFlag++;
					cornerXSum += (double) int_dirs_X[test_direction];
					cornerYSum += (double) int_dirs_Y[test_direction];
				}
			}
		}
		if(rType_s==1)
		{
			//rc.setIndicatorString(0, Integer.toString(cornerFlag));
			//System.out.println("what");
			if(cornerFlag>4)
			{
				//found corner
				
				//check if corner is empty
				MapLocation currentLoc = new MapLocation(rX,rY);
				if(rc.senseCowsAtLocation(currentLoc)<10.0)
				{
					rAction = "init";
					cornerFails++;
					return;
				}
				
				int Ncorners = rc.readBroadcast(3) + 1;
				rc.broadcast(3, Ncorners);
				rc.broadcast(10 + Ncorners, rX*100 + rY);
				System.out.println("wtf corners:" + Ncorners);
				//find corner direction
				double tdx = (double)Math.round((cornerXSum/2.0));
				double tdy = (double)(Math.round(cornerYSum/2.0));
				double tangle = Math.atan(tdy/tdx);
				if(dx<0.0)
				{
					tangle += Math.PI;
				}
				
				int tdirection =(int) Math.round(tangle * 4.0 / Math.PI) + 2;
				if(tdirection<0)
				{
					tdirection += 8;
				}
				else if(tdirection>7)
				{
					tdirection -= 8;
				}
				System.out.println("direction:" + tdirection);
				rc.broadcast(20+Ncorners, tdirection);
				
				rAction = "makePasture";
			}
		}
		else
		{
		}
		if(!movedFlag)
		{
			//stuck
			for(int i=0;i<width;i++)
			{
				for(int j=0;j<height;j++)
				{
					rVisited[i][j] = -1;
				}
			}
			return;
		}
		
		
		
	}
	
}
