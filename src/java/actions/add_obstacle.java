// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class add_obstacle extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        int x = (int) ((NumberTerm) args[0]).solve();
        int y = (int) ((NumberTerm) args[1]).solve();
        
        MapState.addObstacle(x, y, ts.getUserAgArch().getAgName());
       
        return true;
    }
}
