package com.ps.fsm

/**
 * @author pawel.gdula
 */
public final class FsmEvent {

    final Long id

    final int from

    final int to

    FsmEvent(Long id, int from, int to) {
        this.id = id
        this.from = from
        this.to = to
    }
}