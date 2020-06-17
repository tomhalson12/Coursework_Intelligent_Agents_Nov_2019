package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class get_next_scan_location extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	int prevScanLocX = (int) ((NumberTerm) args[0]).solve();
    	int prevScanLocY = (int) ((NumberTerm) args[1]).solve();
    	
    	int scanWidth = (int) ((NumberTerm) args[2]).solve() - 1;
    	
    	int reverseNum = (int) ((NumberTerm) args[3]).solve();
    	
    	boolean reverse = reverseNum == 1;
    	
    	int nextScanLocX;
		int nextScanLocY;
    	
    	if(reverse) {
    		if(prevScanLocX == 0) {
    			nextScanLocX = MapState.getMapWidth() - scanWidth;
    			nextScanLocY = prevScanLocY;
    		} else {
    			nextScanLocX = prevScanLocX - scanWidth;
    			nextScanLocY = prevScanLocY;    			
    		}
    		
    		if(nextScanLocX <= 0) {
    			nextScanLocX = 0;
    			nextScanLocY -= scanWidth ;    		
    		}
    		
    		if(nextScanLocY < 0) {
    			nextScanLocY = MapState.getMapHeight() + nextScanLocY;
    		}
    		
    	} else {
    		nextScanLocX = prevScanLocX + scanWidth;
    		nextScanLocY = prevScanLocY;
    		
    		if(nextScanLocX >= MapState.getMapWidth()) {
    			nextScanLocX = 0;
    			nextScanLocY += scanWidth ;    		
    		}
    		
    		if(nextScanLocY >= MapState.getMapHeight()) {
    			nextScanLocY = nextScanLocY % MapState.getMapHeight();
    		}
    	}
    	
    	NumberTerm x = new NumberTermImpl(nextScanLocX);
    	NumberTerm y = new NumberTermImpl(nextScanLocY);
    	
    	return un.unifies(x, args[4]) && un.unifies(y, args[5]); 
    }
}
