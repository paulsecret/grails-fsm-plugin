package com.ps.fsm

/**
 * @author pawel.gdula
 */
public final class FsmTransitionEvent implements Serializable {

    private static final long serialVersionUID = -8294332310956135944L;

    final Class entity

    final Long id

    final String filed

    final int from

    final int to

    final boolean dirty

    FsmTransitionEvent(Class entity, Long id, String filed, int from, int to, boolean dirty) {
        this.entity = entity
        this.id = id
        this.filed = filed
        this.from = from
        this.to = to
        this.dirty = dirty
    }
}