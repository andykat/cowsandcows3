package moveTo1;


import battlecode.common.*;

import java.util.*;
/*channels
 * 1 # of robots spawned 
 * 2 # of cow pushers
 * 3 # of corners
 * 4
 * 5
 * 6
 * 7
 * 8
 * 9
 * 10
 * 11 corner 1 location
 * 12 corner 2 location
 * 21 corner 1 cowpush direction
 * 22 corner 2 cowpush direction
 * 31 corner 1 # of cowpushers
 * 32 corner 2 # of cowpushers
 * 41 corner 1 pushing data
 * 42
 */

public class RobotPlayer {
	public static RobotController rc;
	
	public static Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
		,Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
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
	public static int push_Radius = 8;
	
	public static int move_Outside_Count;
	public static void run(RobotController myRC) {
		rc = myRC;
		
		//set rand seed
		rand = new Random(myRC.getRobot().getID());
		
		
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		
		//set robot variables
		if(rc.getType()==RobotType.SOLDIER)
		{
			int robotCount=0;
			try {
				robotCount = rc.readBroadcast(1)+1;
				rc.broadcast(1, robotCount);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			
			rType = robotCount; //type
			rVisited = new int[width][height]; //saving visited locations
			for(int i=0;i<width;i++)
			{
				for(int j=0;j<height;j++)
				{
					rVisited[i][j] = -1;
				}
			}
			rAction = "init";
		}

		while(true){
			try{
				if(rc.getType()==RobotType.HQ){
					//spawn soldiers
					tryToSpawn();
				}else if (rc.getType()==RobotType.SOLDIER){
					rX = rc.getLocation().x;
					rY = rc.getLocation().y;
					if(rType == 1 || rType == 4)
					{
						rType_s = 1;
						pastureBot(); //find corner and build pasture
					}
					else if(rType == 2 || rType == 3)
					{
						rType_s = 2;
						cowPushBot(); //move cows into pastures
					}
					else if(rType == 5 || rType == 6)
					{
						rType_s = 2;
						cowPushBot(); //move cows into pastures
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}		
	}
	public static void tryToSpawn() throws GameActionException {
		//if(Clock.getRoundNum()>=lastSpawnRound+GameConstants.HQ_SPAWN_DELAY_CONSTANT_1){
		if(rc.isActive()&&rc.canMove(dirs[0])&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
			rc.spawn(dirs[0]);
			//lastSpawnRound=Clock.getRoundNum();
		}
		//}
	}
	public static void cowPushBot() throws GameActionException {
		if(rc.isActive())
		{

			if(rAction.equals("init"))
			{
				int current_CowPushers = rc.readBroadcast(2)+1;
				rc.broadcast(2, current_CowPushers);
				corner_Index = (current_CowPushers-1)/2 + 1;
				
				int Ncowpushers = rc.readBroadcast(30 + corner_Index);
				cow_push_type = Ncowpushers%10;
				rc.broadcast(30+corner_Index, Ncowpushers + 1);
				
				//move to random location
				Direction moveDirection = dirs[rand.nextInt(8)];
				if (rc.canMove(moveDirection)) {
					rc.move(moveDirection);
				}
				
				move_Outside_Count = 0;

				rAction = "wFC";
			}
			else if(rAction.equals("wFC")) // Waiting For Corner
			{
				int Ncorners = rc.readBroadcast(3);

				if(Ncorners > corner_Index-1)
				{
					rAction = "cF";
				}
				else
				{
					move_Outside_Count++;
					if(move_Outside_Count<11)
					{
						Direction moveDirection = dirs[rand.nextInt(8)];
						if (rc.canMove(moveDirection)) {
							rc.move(moveDirection);
						}
						else
						{
							Direction t = dirs[rand.nextInt(8)];
							if (rc.canMove(t)) {
								rc.move(t);
							}
						}
					}
				}
			}
			else if(rAction.equals("cF")) // Corner Found
			{
				 pastureDir = rc.readBroadcast(20 + corner_Index);
				
				int pasture_Loc = rc.readBroadcast(10 + corner_Index);
				pastureX = pasture_Loc / 100;
				pastureY = pasture_Loc % 100;
				System.out.println("px: "+pastureX + " py: " + pastureY);
				
				
				
				
				rAction = "moveToPushLoc";
				rDestinationX = pastureX - push_Radius * int_dirs_X[pastureDir] + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir] - 4*int_dirs_Y[pastureDir];
				rDestinationY = pastureY - push_Radius * int_dirs_Y[pastureDir] + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir] + 4*int_dirs_X[pastureDir];
				
				System.out.println("dx: "+ rDestinationX + " dy: " + rDestinationY + " ct: " + cow_push_type);
				System.out.println("x1: " + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir] + " x2: " + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir]);
				move(true);
			}
			else if(rAction.equals("moveToPushLoc"))
			{
				move(true);
			}
			else if(rAction.equals("wait"))
			{
				int  tempC = rc.readBroadcast(30+corner_Index);
				int current_CowPushers = tempC/10;
				if(current_CowPushers>1)
				{
					//ready to start push
					//System.out.println("started");
					if(current_CowPushers<10)
					{
						rc.broadcast(30+corner_Index, tempC - current_CowPushers*10 + 100);
					}
					else
					{
						rc.broadcast(30+corner_Index, tempC - 100);
					}
					
					
					
					
					int temp_Dir = rc.readBroadcast(40+corner_Index);
					if(temp_Dir%2 == 1)
					{
						if((temp_Dir+1)%10==0)
						{
							rDestinationX = pastureX - push_Radius * int_dirs_X[pastureDir] + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir] - 4*int_dirs_Y[pastureDir];
							rDestinationY = pastureY - push_Radius * int_dirs_Y[pastureDir] + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir] + 4*int_dirs_X[pastureDir];
						}
						else if((temp_Dir+1)%10==2)
						{
							rDestinationX = pastureX - push_Radius * int_dirs_X[pastureDir] + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir] + 4*int_dirs_Y[pastureDir];
							rDestinationY = pastureY - push_Radius * int_dirs_Y[pastureDir] + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir] - 4*int_dirs_X[pastureDir];
						}
						else if((temp_Dir+1)%10==4)
						{
							rDestinationX = pastureX - push_Radius * int_dirs_X[pastureDir] + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir] - 8*int_dirs_Y[pastureDir];
							rDestinationY = pastureY - push_Radius * int_dirs_Y[pastureDir] + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir] + 8*int_dirs_X[pastureDir];
						}
						else if((temp_Dir+1)%10==6)
						{
							rDestinationX = pastureX - push_Radius * int_dirs_X[pastureDir] + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir] + 8*int_dirs_Y[pastureDir];
							rDestinationY = pastureY - push_Radius * int_dirs_Y[pastureDir] + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir] - 8*int_dirs_X[pastureDir];
						}
						else if((temp_Dir+1)%10==8)
						{
							rDestinationX = pastureX - push_Radius * int_dirs_X[pastureDir] + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir];
							rDestinationY = pastureY - push_Radius * int_dirs_Y[pastureDir] + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir];
						}
						rAction = "return";
					}
					else
					{
						rDestinationX = pastureX - 1 * int_dirs_X[pastureDir] + push_init_loc_X[cow_push_type]*int_dirs_X[pastureDir];
						rDestinationY = pastureY - 1 * int_dirs_Y[pastureDir] + push_init_loc_Y[cow_push_type]*int_dirs_Y[pastureDir];
						rAction = "push";
					}
					//move(true);
				}
			}
			else if(rAction.equals("push"))
			{
				move(true);
			}
			else if(rAction.equals("return"))
			{
				move(false);
			}
		}
	}
	
	
	public static void pastureBot() throws GameActionException {
		if(rc.isActive())
		{
			if(rAction.equals("init"))
			{
				//rDestinationX = rand.nextInt(width);
				//rDestinationY = rand.nextInt(height);
				
				//set destination corner
				if(rType == 1)
				{
					rDestinationX = 1;
					rDestinationY = 1;
				}
				else if(rType == -1)
				{
					rDestinationX = width - 1;
					rDestinationY = height - 1;
				}
				else if(rType == -1)
				{
					rDestinationX = 1;
					rDestinationY = height - 1;
				}
				else if(rType == 4)
				{
					rDestinationX = width - 2;
					rDestinationY = 1;
				}
				//System.out.println("type:" + rType);
				//System.out.println("rdx: " +  rDestinationX + " rdy: " + rDestinationY);
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
					int Ncorners = rc.readBroadcast(3) + 1;
					rc.broadcast(3, Ncorners);
					rc.broadcast(10 + Ncorners, rX*100 + rY);
					
					int tdirection = 0;
					
					if(rType==1)
					{
						tdirection = 7;
					}
					if(rType==4)
					{
						tdirection = 1;
					}
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
					rc.broadcast(30+corner_Index, current_CowPushers+10);
				}
				else if(rAction.equals("push") || rAction.equals("return"))
				{
					rAction = "wait";
					int current_CowPushers = rc.readBroadcast(30+corner_Index);
					rc.broadcast(30+corner_Index, current_CowPushers+10);
					if(current_CowPushers+10>20)
					{
						int temp_dir = rc.readBroadcast(40+corner_Index);
						rc.broadcast(40+corner_Index, temp_dir + 1);
					}
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
		for(int i=1;i<9;i++)
		{
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
					//move!
					if(mFlag)
					{
						rc.move(moveDirection);
					}
					else
					{
						rc.sneak(moveDirection);
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
					cornerFlag++;
					cornerXSum += (double) int_dirs_X[test_direction];
					cornerYSum += (double) int_dirs_Y[test_direction];
				}
			}
		}
		if(rType_s==1)
		{
			if(cornerFlag>2 && !movedFlag)
			{
				//found corner
				int Ncorners = rc.readBroadcast(3) + 1;
				rc.broadcast(1, Ncorners);
				rc.broadcast(10 + Ncorners, rX*100 + rY);
				
				//find corner direction
				double tdx = (double)Math.round((cornerXSum/((double)cornerFlag)));
				double tdy = (double)(Math.round(cornerYSum/((double)cornerFlag)));
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
