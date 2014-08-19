package com.ps.fsm

import com.ps.messaging.MessageSenderService
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager

/**
 * @author pawel.gdula
 */
@Slf4j
class FsmEventTrigger {

    @Autowired
    MessageSenderService messageSenderService

    public static FsmEventTrigger getInstance() {
        Holders.grailsApplication.mainContext.getBean(FsmEventTrigger)
    }

    public void fire(final Class entity, final Long id, final String filed, final int from, final int to) {
        Map<String, ?> message = [
                class: entity,
                id   : id,
                field: filed,
                from : from,
                to   : to,
                dirty: !TransactionSynchronizationManager.isSynchronizationActive()
        ]

        if (!message.dirty) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    messageSenderService.send(com.ps.messaging.Queue.ENTITY_STATE_TRANSITION, message)
                }
            })
        } else {
            messageSenderService.send(com.ps.messaging.Queue.ENTITY_STATE_TRANSITION, message)
        }
    }
}
