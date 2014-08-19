package com.ps.fsm

/**
 * @author pawel.gdula
 */
class FsmEventLog {

    String id

    Date dateCreated

    static mapping = {
        id generator: 'assigned'
        version false
    }
}
