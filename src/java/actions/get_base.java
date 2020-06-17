// Internal action code for project CM30174_50206-master

package actions;

import java.util.ArrayList;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class get_base extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	ArrayList<Integer> base = MapState.getBase(ts.getUserAgArch().getAgName());
    	
    	NumberTerm x = new NumberTermImpl(base.get(0));
    	NumberTerm y = new NumberTermImpl(base.get(1));
    	
        return un.unifies(x, args[0]) && un.unifies(y, args[1]);
    }
}
