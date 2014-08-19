package com.ps.fsm

import groovy.transform.EqualsAndHashCode
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import java.lang.reflect.Method

/**
 * @author pawel.gdula
 */
class FsmEventListenersRepository {

    public Map<Key, List<Executor>> listeners = new HashMap<>()

    @Autowired
    GrailsApplication grailsApplication

    @Autowired
    FsmEventService fsmEventService

    @PostConstruct
    public void init() {
        grailsApplication.mainContext.getBeansWithAnnotation(FsmEventListenerContainer).values().each { final Object annotatedObject ->
            annotatedObject.class.methods.findAll { it.isAnnotationPresent(FsmEventListener) }.each { Method method ->
                FsmEventListener annotation = method.getAnnotation(FsmEventListener)
                Key key = new Key(annotation.entity(), annotation.field(), annotation.from(), annotation.to())

                if (!listeners[key]) {
                    listeners[key] = []
                }

                Executor executor = new DefaultExecutor(annotatedObject, method)

                if (annotation.unique()) {
                    executor = new UniqueExecutor(executor, fsmEventService)
                }

                listeners[key] << executor
            }
        }
    }

    public void delegateEvent(final Class entity, final Long id, final String filed, final int from, final int to) {
        Key key = new Key(entity, filed, from, to)

        if (!listeners[key]) {
            return
        }

        FsmEvent event = new FsmEvent(id, from, to)
        listeners[key].each { it.execute(event, key) }
    }

    @EqualsAndHashCode(includes = ['entity', 'from', 'field', 'to'])
    private static final class Key implements FsmEventUniquenessAware {

        private final Class entity

        private final String field

        private final int from

        private final int to

        Key(Class entity, String field, int from, int to) {
            this.entity = entity
            this.field = field
            this.from = from
            this.to = to
        }

        @Override
        String getUniqueness() {
            return "${entity}-${field}-${from}-${to}"
        }
    }

    private static interface Executor {

        public void execute(FsmEvent event, Key key)

    }

    private static class DefaultExecutor implements Executor {

        private final Object target

        private final Method method

        DefaultExecutor(Object target, Method method) {
            this.target = target
            this.method = method
        }

        public void execute(FsmEvent event, Key key) {
            method.invoke(target, event)
        }
    }

    private static class UniqueExecutor implements Executor {

        private final DefaultExecutor executor

        private final FsmEventService service

        UniqueExecutor(final DefaultExecutor executor, final FsmEventService service) {
            this.executor = executor
            this.service = service
        }

        public void execute(FsmEvent event, Key key) {
            service.syncExecution(key) {
                executor.execute(event, key)
            }
        }
    }

}
