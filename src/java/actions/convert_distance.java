// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class convert_distance extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        int distanceX = (int) ((NumberTerm) args[0]).solve();
        int distanceY = (int) ((NumberTerm) args[1]).solve();
        
        int newX = convertDistance(distanceX, MapState.getMapWidth());
        int newY = convertDistance(distanceY, MapState.getMapHeight());
        
        return un.unifies(new NumberTermImpl(newX), args[2]) & un.unifies(new NumberTermImpl(newY), args[3]);
    }
    
    private int convertDistance(int d, int limit) {
    	int newD = Math.abs(d) % limit;
    	
    	if(d < 0) {
    		newD = newD * -1;
    	}
    	
    	if(Math.abs(newD) > limit / 2) {
    		if(newD < 0) {
    			return limit + newD;
    		} else {
    			return newD - limit;
    		}
    	} else {
    		return newD;
    	}
    }
    
    
}
