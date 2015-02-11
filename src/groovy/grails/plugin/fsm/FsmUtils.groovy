package grails.plugin.fsm

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.springframework.beans.BeanUtils

class FsmUtils {

    public static final String FSMDEF = "fsm_def"

    static def mockFsm(domainClass) {
        def log = new Expando(
                trace: { println it },
                debug: { println it },
                info: { println it },
                warn: { println it },
                error: { println it }
        )

        MetaClassRegistry registry = GroovySystem.metaClassRegistry
        def fsm = GrailsClassUtils.getStaticPropertyValue(domainClass, FSMDEF)
        if (fsm) {
            // Will create the proper FsmSupport instance!
            fsm.each { String p, definition ->
                definition.each { start, defclosure ->
                    def mp = domainClass.metaClass.getMetaProperty(p)
                    if (!mp)
                        throw new FsmSupportException("Error in FSM definition: '${domainClass}' does not have '${p}' property to hold defined workflow status!")
                    def tmp = domainClass.newInstance()
                    if (tmp[p] != null)
                        log.error("Default value of '${domainClass}.${p}' will be overriden by FSM definition for that property. ")

                    // Modify the metaclass so new instances will have new behaviour!!
                    domainClass.metaClass.setProperty(p, start)
                    domainClass.metaClass.setProperty("_fsm${p}", null)  // internal, will hold FsmSupport instance
                    domainClass.metaClass.fire = fireClosure
                    domainClass.metaClass."fire_${p}" = fireClosure.curry(p)
                    domainClass.metaClass.fireable = fireableClosure
                    domainClass.metaClass."fireable_${p}" = fireableClosure.curry(p)

                }
            }
            // This code is a COPY of DomainClassGrailsPlugin.enhanceDomainClasses
            // because I cannot seem to be able to decorate it.
            // We just added the "${p}" initializing!
            domainClass.metaClass.constructor = { ->
                def bean = BeanUtils.instantiateClass(domainClass)
                fsm.each { pp, defdef ->
                    defdef.each { startstart, clos ->
                        bean."${pp}" = startstart
                    }
                }
                bean
            }
            domainClass.metaClass.static.create = { ->
                def bean = BeanUtils.instantiateClass(domainClass)
                fsm.each { pp, defdef ->
                    defdef.each { startstart, clos ->
                        bean."${pp}" = startstart
                    }
                }
            }
        }
    }

    static Closure fireClosure = { String flow, String event ->
        return getFsmExecutor(flow, event, delegate).fire(event)
    }

    static Closure fireableClosure = { String flow, String event ->
        return getFsmExecutor(flow, event, delegate).isFireable(event)
    }

    static def getFsmExecutor(String flow, String event, Object targetObject) {
        Map<String, Map<Integer, Closure>> flowdef = GrailsClassUtils.getStaticPropertyValue(targetObject.class, FsmUtils.FSMDEF)

        if (flowdef[flow] == null) {
            throw new FsmSupportException("Can't fire on flow '${flow}' which is not defined in '${targetObject.class}'")
        }

        // there is only one entry (this is kind of DSL way of providing initial state
        Map.Entry<Integer, Closure> definition = flowdef[flow].entrySet().first()
        Closure flowClosure = definition.value
        Integer initialState = definition.key
        Integer currentState = targetObject.getProperty(flow)
        // prepare fsm support object
        FsmSupport fsmSupport = new FsmSupport(targetObject, flow, initialState, currentState)
        flowClosure.call(fsmSupport.record())
        return fsmSupport
    }
}

