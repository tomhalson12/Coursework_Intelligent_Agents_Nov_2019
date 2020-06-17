// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class clear_moves extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        MapState.clearMoves(ts.getUserAgArch().getAgName());
        return true;
    }
}
