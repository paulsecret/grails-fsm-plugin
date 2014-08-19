package com.ps.fsm

import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author pawel.gdula
 */
@Slf4j
class FsmEventService {

    GrailsApplication grailsApplication

    @Transactional
    public void syncExecution(final FsmEventUniquenessAware uniquenessAware, final Closure executor) {
        try {
            new FsmEventLog(id: "${grailsApplication.metadata['app.name']}-${uniquenessAware.getUniqueness()}").save(flush: true)
            executor.call()
        } catch (final Throwable exp) {
            log.debug('Discarding event {}, already handled', uniquenessAware.getUniqueness())
        }
    }

}
