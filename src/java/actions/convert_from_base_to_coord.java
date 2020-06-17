// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class convert_from_base_to_coord extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	int toBaseX = (int) ((NumberTerm) args[0]).solve();
    	int toBaseY = (int) ((NumberTerm) args[1]).solve();

        int newX = convert(toBaseX, MapState.getMapWidth());
        int newY = convert(toBaseY, MapState.getMapHeight());
        
        return un.unifies(new NumberTermImpl(newX), args[2]) & un.unifies(new NumberTermImpl(newY), args[3]);
    }
    
    private int convert(int d, int limit) {
    	if(d <= 0) {
    		return -d;
    	}
    	
    	return limit - d;	
    }
}
