package Arsonists;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class killList {
	private static Map<Integer, Integer> crushKillDestroy;
	//addAssignment(int ID, int killLoc);
	//getAssignment(int ID); Use hashtables to search
	
	public void addAssignment(int ID, int killLoc)
	{
		//killAssignments a= new killAssignments(ID, killLoc);
		//klist.at(ID%)
		
		//set up hashmap to associate IDs and locations
		//Could also use an array of killAssignment objects, which is what
		//I think you were originally trying to do?
		crushKillDestroy.put(ID, killLoc);
	}
	
	public int getAssignment(int ID) {
		if(crushKillDestroy.containsKey(ID)) {
			return crushKillDestroy.get(ID);
		}
		//if it doesn't exist
		return -1;
	}
}
