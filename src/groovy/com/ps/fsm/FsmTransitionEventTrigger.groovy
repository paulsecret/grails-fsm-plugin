package com.ps.fsm

import grails.util.Holders
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionSynchronizationManager

/**
 * @author pawel.gdula
 */
@Slf4j
class FsmTransitionEventTrigger {

    @Autowired
    List<FsmTransitionEventListener> listeners

    public static FsmTransitionEventTrigger getInstance() {
        Holders.grailsApplication.mainContext.getBean(FsmTransitionEventTrigger)
    }

    public void fire(final Class entity, final Long id, final String filed, final int from, final int to) {
        boolean dirty = !TransactionSynchronizationManager.isSynchronizationActive()
        FsmTransitionEvent event = new FsmTransitionEvent(entity, id, filed, from, to, dirty)
        listeners.each { it.handleEvent(event) }
    }
}
