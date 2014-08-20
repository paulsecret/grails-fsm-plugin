package com.ps.fsm

/**
 * @author pawel.gdula
 */
public interface FsmTransitionEventListener {

    public void handleEvent(FsmTransitionEvent event);
}