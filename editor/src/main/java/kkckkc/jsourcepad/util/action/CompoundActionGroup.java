package kkckkc.jsourcepad.util.action;

public class CompoundActionGroup extends ActionGroup {
    final Runnable LISTENER = new Runnable() {
        @Override
        public void run() {
            items.clear();
            items.addAll(actionGroup1.items);
            items.addAll(actionGroup2.items);

            updateDerivedComponents();
        }
    };
    
    private ActionGroup actionGroup1;
    private ActionGroup actionGroup2;

    public CompoundActionGroup(ActionGroup actionGroup1, ActionGroup actionGroup2) {
        this.actionGroup1 = actionGroup1;
        this.actionGroup2 = actionGroup2;
        
        items.addAll(actionGroup1.items);
        items.addAll(actionGroup2.items);

        actionGroup1.registerListener(LISTENER);
        actionGroup2.registerListener(LISTENER);
    }

}
