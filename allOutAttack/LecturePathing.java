package allOutAttack;
import battlecode.common.*;

import java.util.*;

public class LecturePathing{
	/*
	 * don't go back to "stuck" square.
	 * record list of gooey trail
	 * 
	 */
	
	/*
	 * snailtrail = arraylist of map locations
	 * trytomove - while array is small, add something off the map
	 * make move
	 * remove item at beginning of list, add current location to end
	 * propose move
	 * check if on/adjacent to trail, make sure current location is not
	 * on trail
	 * check if can move
	 * 
	 * good for local pathing.  Use something else for long range?
	 */
}