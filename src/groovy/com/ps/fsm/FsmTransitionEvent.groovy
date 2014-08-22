package com.ps.fsm

/**
 * Represents transition of state of specified entity
 * @author pawel.gdula
 */
public final class FsmTransitionEvent implements Serializable {

    private static final long serialVersionUID = -8294332310956135944L;

    /**
     * Id of entity for which transition has occurred
     */
    final Long entityId

    /**
     * Class of entity for which transition has occurred
     */
    final Class entityClass

    /**
     * Changed field
     */
    final String filed

    /**
     * Initial state
     */
    final Integer from

    /**
     * Target state
     */
    final Integer to

    /**
     * Indicate presence of transaction.
     * If true, transition was made outside of transaction - not sure if commit was successful
     * If false transition was successfully committed.
     */
    final boolean dirty

    FsmTransitionEvent(Long entityId, Class entityClass, String filed, Integer from, Integer to, boolean dirty) {
        this.entityId = entityId
        this.entityClass = entityClass
        this.filed = filed
        this.from = from
        this.to = to
        this.dirty = dirty
    }
}