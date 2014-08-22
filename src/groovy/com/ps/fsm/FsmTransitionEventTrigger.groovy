package com.ps.fsm

import grails.util.Holders
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionSynchronizationManager

/**
 * Hook into existing FSM infrastructure.
 * Delegates event to registered listeners
 * @author pawel.gdula
 */
@Slf4j
class FsmTransitionEventTrigger {

    @Autowired
    List<FsmTransitionEventListener> listeners

    /**
     * Singleton accessor
     * @return Singleton instance
     */
    public static FsmTransitionEventTrigger getInstance() {
        Holders.grailsApplication.mainContext.getBean(FsmTransitionEventTrigger)
    }

    /**
     * Triggers an event
     * @param entityClass Class of entity for which transition has occurred
     * @param entityId Id of entity for which transition has occurred
     * @param filed Changed field
     * @param from Initial state
     * @param to Target state
     */
    public void fire(final Class entityClass, final Long entityId, final String filed, final int from, final int to) {
        boolean dirty = !TransactionSynchronizationManager.isSynchronizationActive()
        FsmTransitionEvent event = new FsmTransitionEvent(entityId, entityClass, filed, from, to, dirty)
        listeners.each { it.handleEvent(event) }
    }

}
