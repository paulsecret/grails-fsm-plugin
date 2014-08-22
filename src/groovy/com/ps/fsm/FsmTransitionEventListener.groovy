package com.ps.fsm

/**
 * Interface for any object interested on FSM events
 * @author pawel.gdula
 */
public interface FsmTransitionEventListener {

    public void handleEvent(FsmTransitionEvent event);

}