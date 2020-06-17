// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class add_move extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        int movementX = (int) ((NumberTerm) args[0]).solve();
        int movementY = (int) ((NumberTerm) args[1]).solve();
       
       
        MapState.addMove(movementX, movementY, ts.getUserAgArch().getAgName());
        return true;
    }
}
