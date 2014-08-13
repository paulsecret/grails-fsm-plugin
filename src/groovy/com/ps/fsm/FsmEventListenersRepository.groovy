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

    @PostConstruct
    public void init() {
        grailsApplication.mainContext.getBeansWithAnnotation(FsmEventListenerContainer).values().each { final Object annotatedObject ->
            annotatedObject.class.methods.findAll { it.isAnnotationPresent(FsmEventListener) }.each { Method method ->
                FsmEventListener annotation = method.getAnnotation(FsmEventListener)
                Key key = new Key(annotation.entity(), annotation.field(), annotation.from(), annotation.to())

                if (!listeners[key]) {
                    listeners[key] = []
                }

                listeners[key] << new Executor(key, annotatedObject, method)
            }
        }
    }

    public void delegateEvent(final Class entity, final Long id, final String filed, final int from, final int to) {
        Key key = new Key(entity, filed, from, to)

        if (!listeners[key]) {
            return
        }

        FsmEvent event = new FsmEvent(id, from, to)
        listeners[key].each { it.execute(event) }
    }

    @EqualsAndHashCode(includes = ['entity', 'from', 'field', 'to'])
    private static final class Key {

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
    }

    private static class Executor {

        private final Key key

        private final Object target

        private final Method method

        Executor(Key key, Object target, Method method) {
            this.key = key
            this.target = target
            this.method = method
        }

        public void execute(FsmEvent event) {
            method.invoke(target, event)
        }
    }

}
