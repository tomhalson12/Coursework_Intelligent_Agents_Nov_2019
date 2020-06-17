package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class get_next_location extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	double xVal = 0;
    	double yVal = 0;
    	
    	NumberTerm scanNumber = (NumberTerm) args[0];
    	NumberTerm scanWidth = (NumberTerm) args[1];
    	NumberTerm mapWidth = (NumberTerm) args[2];
    	
    	if(scanNumber.solve() != 1) {
    		xVal = scanWidth.solve();
    	
    		double scansPerRow = Math.ceil(mapWidth.solve() / scanWidth.solve());
    		double scanRow = Math.ceil(scanNumber.solve() / scansPerRow);
    		double scanNumInRow = scanNumber.solve() - (scanRow - 1)* scansPerRow;
    	
    		if(scanNumInRow == 1) {
    			yVal = scanWidth.solve();
    		}
    	}
    	
    	NumberTerm xRes = new NumberTermImpl(xVal);
    	NumberTerm yRes = new NumberTermImpl(yVal);
    	
        un.unifies(xRes, args[3]);
        un.unifies(yRes, args[4]);
        return true;
    }
}
