package com.ps.fsm

import com.ps.messaging.Consumer
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author pawel.gdula
 */
class HandlingEventService {

    static transactional = false

    @Autowired
    FsmEventListenersRepository repository

    @Consumer(com.ps.messaging.Queue.ENTITY_STATE_TRANSITION)
    public void handleMessage(final Map<String, ?> message) {
        repository.delegateEvent(message.class, message.id, message.field, message.from, message.to)
    }

}
