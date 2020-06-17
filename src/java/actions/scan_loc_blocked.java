// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class scan_loc_blocked extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	int blockedScanLocX = (int) ((NumberTerm) args[0]).solve();
    	int blockedScanLocY = (int) ((NumberTerm) args[1]).solve();
    	
    	int scanWidth = (int) ((NumberTerm) args[2]).solve() - 1;
    	
    	int prevScanLocX = (int) ((NumberTerm) args[3]).solve();
    	int prevScanLocY = (int) ((NumberTerm) args[4]).solve();
    	
    	int reverseNum = (int) ((NumberTerm) args[5]).solve();
    	
    	int newScanX;
		int newScanY;
    	
    	if(reverseNum == 1) {
    		int perfLocX;
    		int perfLocY;
    		
    		if(prevScanLocX == 0) {
    			perfLocX = MapState.getMapWidth() - scanWidth;
    			perfLocY = prevScanLocY;
    		} else {
    			perfLocX = prevScanLocX - scanWidth;
    			perfLocY = prevScanLocY;    			

    		}
    		
    		if(perfLocX <= 0) {
    			perfLocX = 0;
    			perfLocY -= scanWidth;    		
    		}
    		
    		if(perfLocY < 0) {
    			perfLocY = MapState.getMapHeight() + perfLocY;
    		}
    		
    		
    		int diffXFromPerfLoc = blockedScanLocX - perfLocX;
    		int diffYFromPerfLoc = blockedScanLocY - perfLocY;
    		
    		
    		int newDiffX;
    		int newDiffY;
    		
    		if(perfLocX == 0) {
    			newDiffX = 0;
    			newDiffY = diffYFromPerfLoc - 1;
    		} else {
    			if(diffXFromPerfLoc < scanWidth) {
    				newDiffX = diffXFromPerfLoc - 1;
    				newDiffY = diffYFromPerfLoc;
    			} else {
    				newDiffX = 0;
    				newDiffY = diffYFromPerfLoc - 1;
    			}    		
    		}
    		
    		newScanX = perfLocX - newDiffX;
    		newScanY = perfLocY - newDiffY;
    	} else {
    		int perfLocX = prevScanLocX + scanWidth;
    		int perfLocY = prevScanLocY;
    		
    		if(perfLocX >= MapState.getMapWidth()) {
    			perfLocX = 0;
    			perfLocY = (prevScanLocY + scanWidth) % MapState.getMapHeight();
    		}
    		
    		int diffXFromPerfLoc = perfLocX - blockedScanLocX < 0 ? MapState.getMapWidth() - blockedScanLocX : perfLocX - blockedScanLocX;
    		int diffYFromPerfLoc = perfLocY - blockedScanLocY < 0 ? MapState.getMapHeight() - blockedScanLocY : perfLocY - blockedScanLocY;
    		
    		int newDiffX;
    		int newDiffY;
    		
    		if(perfLocX == 0) {
    			newDiffX = 0;
    			newDiffY = diffYFromPerfLoc + 1;
    		} else {
    			if(diffXFromPerfLoc < scanWidth) {
    				newDiffX = diffXFromPerfLoc + 1;
    				newDiffY = diffYFromPerfLoc;
    			} else {
    				newDiffX = 0;
    				newDiffY = diffYFromPerfLoc + 1;
    			}    		
    		}
    		
    		newScanX = perfLocX - newDiffX;
    		newScanY = perfLocY - newDiffY;
    		
    	}

    	newScanX = convert(newScanX, MapState.getMapWidth());
    	newScanY = convert(newScanY, MapState.getMapHeight());

    	return un.unifies(new NumberTermImpl(newScanX), args[6]) && un.unifies(new NumberTermImpl(newScanY), args[7]); 
    }
    
    private int convert(int d, int limit) {
    	if(d >= limit) {
    		return d % limit;    		
    	}
    	
    	return d;
    }
} 
