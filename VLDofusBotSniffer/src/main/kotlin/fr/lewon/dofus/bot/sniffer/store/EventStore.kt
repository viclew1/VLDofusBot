package fr.lewon.dofus.bot.sniffer.store

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.store.waiters.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.locks.ReentrantLock

class EventStore {

    private val eventQueue = ArrayBlockingQueue<NetworkMessage>(QUEUE_SIZE)
    private val lock = ReentrantLock()
    private val waitLock = ReentrantLock()

    private var messageWaiter: AbstractMessageWaiter? = null


    fun addSocketEvent(dofusEvent: NetworkMessage, connection: DofusConnection) {
        lock.executeSyncOperation {
            getHandlers(dofusEvent.javaClass).forEach {
                it.onEventReceived(dofusEvent, connection)
            }
            if (!eventQueue.offer(dofusEvent)) {
                eventQueue.poll()
                eventQueue.offer(dofusEvent)
            }
            messageWaiter?.takeIf { !it.consumed }?.onMessageReceived(dofusEvent)
        }
    }

    fun <T : NetworkMessage> getAllEvents(eventClass: Class<T>): List<T> {
        return lock.executeSyncOperation {
            eventQueue.filter { it::class.java == eventClass }.map { eventClass.cast(it) }
        }
    }

    fun isAllEventsPresent(vararg messageClasses: Class<out NetworkMessage>): Boolean {
        return isAllEventsPresent(messageClasses.groupingBy { it }.eachCount())
    }

    fun isAllEventsPresent(messageClassByCount: Map<Class<out NetworkMessage>, Int>): Boolean {
        for (entry in messageClassByCount.entries) {
            if (getAllEvents(entry.key).size < entry.value) {
                return false
            }
        }
        return true
    }

    fun waitUntilMessagesArrives(messageClass: Class<out NetworkMessage>, timeout: Int): Boolean {
        return waitUntil(timeout) { MessageWaiter(waitLock, messageClass) }
    }

    fun waitUntilAnyMessageArrives(messageClasses: Array<out Class<out NetworkMessage>>, timeout: Int): Boolean {
        return waitUntil(timeout) { AnyMessageWaiter(waitLock, messageClasses) }
    }

    fun waitUntilMultipleMessagesArrive(messageClasses: Array<out Class<out NetworkMessage>>, timeout: Int): Boolean {
        return waitUntil(timeout) { MultipleMessagesWaiter(waitLock, messageClasses) }
    }

    fun waitUntilOrderedMessagesArrive(messageClasses: Array<out Class<out NetworkMessage>>, timeout: Int): Boolean {
        return waitUntil(timeout) { OrderedMessagesWaiter(waitLock, messageClasses) }
    }

    private fun waitUntil(timeout: Int, buildWaiter: () -> AbstractMessageWaiter): Boolean {
        val newMessageWaiter = buildWaiter()
        return newMessageWaiter.lock.executeSyncOperation {
            messageWaiter = newMessageWaiter
            newMessageWaiter.waitUntilNotify(timeout.toLong())
        }
    }

    fun <T : NetworkMessage> getLastEvent(eventClass: Class<T>, filterFunction: (T) -> Boolean = { true }): T? {
        return lock.executeSyncOperation {
            getAllEvents(eventClass).lastOrNull(filterFunction)
        }
    }

    fun <T : NetworkMessage> getFirstEvent(eventClass: Class<T>, filterFunction: (T) -> Boolean = { true }): T? {
        return lock.executeSyncOperation {
            getAllEvents(eventClass).firstOrNull(filterFunction)
        }
    }

    fun clear() {
        lock.executeSyncOperation {
            eventQueue.clear()
        }
    }

    fun <T : NetworkMessage> clear(eventClass: Class<T>) {
        lock.executeSyncOperation {
            eventQueue.removeIf { it::class.java == eventClass }
        }
    }

    fun clearUntilFirst(eventClass: Class<out NetworkMessage>) {
        lock.executeSyncOperation {
            clearUntil(getFirstEvent(eventClass))
        }
    }

    fun clearUntilLast(eventClass: Class<out NetworkMessage>) {
        lock.executeSyncOperation {
            clearUntil(getLastEvent(eventClass))
        }
    }

    private fun clearUntil(event: NetworkMessage?) {
        lock.executeSyncOperation {
            while (eventQueue.firstOrNull() != event) {
                eventQueue.poll()
            }
        }
    }

    companion object {
        private const val QUEUE_SIZE = 500
        private val HANDLER_MAPPER = HashMap<Class<out NetworkMessage>, ArrayList<IEventHandler<NetworkMessage>>>()
        private val STATIC_LOCK = ReentrantLock()

        fun <T : NetworkMessage> getHandlers(eventClass: Class<T>): ArrayList<IEventHandler<T>> {
            return STATIC_LOCK.executeSyncOperation {
                (HANDLER_MAPPER[eventClass] ?: ArrayList()) as ArrayList<IEventHandler<T>>
            }
        }

        @Synchronized
        fun <T : NetworkMessage> addEventHandler(eventClass: Class<T>, eventHandler: IEventHandler<T>) {
            val eventHandlers = HANDLER_MAPPER.computeIfAbsent(eventClass) { ArrayList() }
            eventHandlers.add(eventHandler as IEventHandler<NetworkMessage>)
        }

        fun <T : NetworkMessage> addEventHandler(eventHandler: IEventHandler<T>) {
            val eventHandlerInterface = getAllGenericInterfaces(eventHandler::class.java)
                .filterIsInstance<ParameterizedType>()
                .firstOrNull { IEventHandler::class.java.isAssignableFrom(it.rawType as Class<*>) }
                ?: return
            val actualTypeArgument = eventHandlerInterface.actualTypeArguments[0]
            if (actualTypeArgument is TypeVariable<*>) {
                val argumentClass = actualTypeArgument.bounds[0] as Class<*>
                val realType = getRealType(argumentClass, eventHandler::class.java, IEventHandler::class.java)
                addEventHandler(realType as Class<T>, eventHandler)
            } else if (actualTypeArgument is Class<*>) {
                addEventHandler(actualTypeArgument as Class<T>, eventHandler)
            }
        }

        private fun getRealType(typeParameterClass: Class<*>, baseClass: Class<*>, parentClass: Class<*>): Class<*> {
            val genericSuperClass = baseClass.genericSuperclass
            if (genericSuperClass is ParameterizedType) {
                genericSuperClass.actualTypeArguments.firstOrNull {
                    it is Class<*> && typeParameterClass.isAssignableFrom(it)
                }?.let { return it as Class<*> }
            }
            if (baseClass.isAssignableFrom(parentClass)) {
                return typeParameterClass
            }
            return getRealType(typeParameterClass, baseClass.superclass, parentClass)
        }

        private fun getAllGenericInterfaces(refClass: Class<*>): Set<Type> {
            val interfaces = HashSet<Type>()
            if (refClass.superclass != null) {
                interfaces.addAll(getAllGenericInterfaces(refClass.superclass))
            }
            interfaces.addAll(refClass.genericInterfaces)
            return interfaces
        }
    }

}