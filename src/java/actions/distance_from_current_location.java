// Internal action code for project CM30174_50206-master

package actions;

import java.util.ArrayList;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class distance_from_current_location extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	ArrayList<Integer> currentLoc = MapState.getBase(ts.getUserAgArch().getAgName());
    	
    	int goalX = (int) ((NumberTerm) args[0]).solve();
    	int goalY = (int) ((NumberTerm) args[1]).solve();
    	
    	int mapWidth = MapState.getMapWidth();
    	int mapHeight = MapState.getMapHeight();
    	
    	int distanceX = getDistance(currentLoc.get(0), mapWidth, goalX);
    	int distanceY = getDistance(currentLoc.get(1), mapHeight, goalY);
  
    	NumberTerm movementX = new NumberTermImpl(distanceX);
    	NumberTerm movementY = new NumberTermImpl(distanceY);
    	
    	return un.unifies(movementX, args[2]) && un.unifies(movementY, args[3]);
    }
    
    private int getDistance(int curr, int limit, int goal) {
    	int d;
    	
    	if(curr <= 0) {
    		d = curr * -1;
    	} else {
    		d = limit - curr;
    	}
    	
    	d = goal - d;
    	
    	if(Math.abs(d) > limit / 2) {
    		if(d < 0) {
    			d = limit + d;
    		} else {
    			d = d - limit;
    		}
    	}
    	
    	return d;
    }
}
