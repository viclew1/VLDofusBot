package fr.lewon.dofus.bot.sniffer.store

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.store.handlers.*
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

object EventStore {

    private const val queueSize = 100
    private val queueMapper = HashMap<Class<out INetworkType>, ArrayBlockingQueue<INetworkType>>()
    private val handlerMapper = HashMap<Class<out INetworkType>, ArrayList<EventHandler<INetworkType>>>()

    init {
        addEventHandler(ChangeMapEventHandler)
        addEventHandler(HavenBagAccessEventHandler)
        //addEventHandler(MessageReceivedEventHandler)
        addEventHandler(TreasureHuntEventHandler)
        addEventHandler(FightEnteredEventHandler)
        addEventHandler(TurnReadyEventHandler)
        addEventHandler(ZaapDestinationsEventHandler)
        //addEventHandler(MoveStartEventHandler)
        //addEventHandler(MoveStopEventHandler)
    }

    /**
     * Add socket event to the parsed list. Remove the first if no space left.
     * @param dofusEvent - Socket event to add to the queue.
     */
    fun addSocketEvent(dofusEvent: INetworkType) {
        val eventQueue: ArrayBlockingQueue<INetworkType>
        synchronized(queueMapper) {
            eventQueue = queueMapper.computeIfAbsent(dofusEvent.javaClass) { ArrayBlockingQueue(queueSize) }
        }
        synchronized(eventQueue) {
            if (!eventQueue.offer(dofusEvent)) {
                eventQueue.poll()
                eventQueue.offer(dofusEvent)
            }
        }
        synchronized(handlerMapper) {
            handlerMapper[dofusEvent.javaClass]?.forEach {
                it.onEventReceived(dofusEvent)
            }
        }
    }

    /**
     * Waits for an event with timeout.
     * @param eventClass - Type of the event to wait for.
     * @param timeout - Timeout in ms.
     * @return The next received event corresponding to the type.
     */
    fun <T : INetworkType> waitForEvent(
        eventClass: Class<T>,
        timeout: Int = DTBConfigManager.config.globalTimeout * 1000
    ): T {
        var socketEvent: INetworkType? = null
        val eventQueue: ArrayBlockingQueue<INetworkType>
        synchronized(queueMapper) {
            eventQueue = queueMapper.computeIfAbsent(eventClass) { ArrayBlockingQueue(queueSize) }
        }
        eventQueue.clear()
        WaitUtil.waitUntil({ eventQueue.poll().also { socketEvent = it } != null }, timeout)
        socketEvent ?: error("No event of type [${eventClass.simpleName}] arrived in time ($timeout millis)")
        synchronized(queueMapper) {
            if (eventQueue.isEmpty()) {
                queueMapper.remove(eventClass)
            }
        }
        return eventClass.cast(socketEvent)
    }

    /**
     * Clear all the events in the store.
     */
    fun clear() {
        synchronized(queueMapper) { queueMapper.clear() }
    }

    /**
     * Clear all the events in a queue.
     * Don't do anything if the queue doesn't exist.
     * @param eventClass - Type of the event to remove.
     */
    fun <T : INetworkType> clear(eventClass: Class<T>) {
        synchronized(queueMapper) {
            queueMapper[eventClass]?.clear()
        }
    }

    /**
     * Add an event handler to the list. The handler will be called every time the event occurs.
     * @param eventClass - Type of the event handler.
     * @param eventHandler - Event handler to add.
     */
    @Synchronized
    fun <T : INetworkType> addEventHandler(eventClass: Class<T>, eventHandler: EventHandler<T>) {
        synchronized(handlerMapper) {
            val eventHandlers = handlerMapper.computeIfAbsent(eventClass) { ArrayList() }
            synchronized(eventHandlers) {
                eventHandlers.add(eventHandler as EventHandler<INetworkType>)
            }
        }
    }

    inline fun <reified T : INetworkType> addEventHandler(eventHandler: EventHandler<T>) {
        addEventHandler(T::class.java, eventHandler)
    }

}