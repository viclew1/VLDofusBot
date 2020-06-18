//package fr.lewon.dofus.bot.network
//
//import org.jetbrains.kotlin.com.intellij.icons.AllIcons.Ide.FatalError
//import java.io.BufferedReader
//import java.io.IOException
//import java.io.InputStreamReader
//import java.io.Reader
//import java.net.ServerSocket
//import java.net.Socket
//import java.util.*
//
//
//class Sniffer : Thread() {
//    private val DOFUS_EXE = "Dofus.exe"
//    private val reader = Reader()
//    private var gameServerAddress: String? = null
//    private var clientCo = ServerSocket(5555)
//    private var client: Client
//    private var serverCo: Client
//    private var serverCoThread: Thread
//
//    fun Sniffer() {
//        launch()
//    }
//
//    fun inProcess(processName: String): Boolean {
//        var line: String
//        val tasklist: Process = Runtime.getRuntime().exec("tasklist")
//        val input = BufferedReader(InputStreamReader(tasklist.inputStream))
//        while (input.readLine().also {
//                line = it
//            } != null) if (line.split(" ").toTypedArray()[0] == processName) return true
//        input.close()
//        return false
//    }
//
//    private fun launch() {
//        println("Waiting for $DOFUS_EXE process...")
//        while (!inProcess(DOFUS_EXE)) {
//            sleep(1000)
//        }
//        println("Running sniffer server. Waiting Dofus client connection...")
//        client = Client(clientCo.accept())
//        println("Dofus client connected.")
//        serverCo = Client("213.248.126.39", 5555)
//        println("Running sniffer client. Connection to Dofus server.")
//        start()
//        val buffer = ByteArray(BUFFER_DEFAULT_SIZE)
//        val array = ByteArray()
//        var bytesReceived = 0
//        while (client.receive(buffer).also({ bytesReceived = it }) != -1) {
//            array.setArray(buffer, bytesReceived)
//            processMsgStack(reader.processBuffer(array), "s")
//            serverCo.send(trimBuffer(buffer, bytesReceived))
//        }
//        println("Waiting client reconnection...")
//        client = Client(clientCo!!.accept())
//        println("Dofus client reconnected.")
//        synchronized(this) { serverCoThread.notify() }
//        while (client.receive(buffer).also({ bytesReceived = it }) != -1) {
//            array.setArray(buffer, bytesReceived)
//            processMsgStack(reader.processBuffer(array), "s")
//            serverCo.send(trimBuffer(buffer, bytesReceived))
//        }
//        println("Dofus client deconnected from sniffer server.")
//        client.close()
//    }
//
//    fun run() { // connexion au serveur officiel
//        serverCoThread = this
//        val buffer = ByteArray(BUFFER_DEFAULT_SIZE)
//        val array = ByteArray()
//        var bytesReceived = 0
//        while (serverCo.receive(buffer).also({ bytesReceived = it }) != -1) {
//            array.setArray(buffer, bytesReceived)
//            if (processMsgStack(reader.processBuffer(array), "r")) client.send(trimBuffer(buffer, bytesReceived))
//            if (mustDeconnectClient) break
//        }
//        client.close()
//        println("Deconnection from Dofus client.")
//        serverCo.close()
//        println("Deconnected from authentification server.")
//        synchronized(this) {
//            wait()
//        }
//        if (gameServerAddress != null) {
//            println("Connecting to game server, waiting response...")
//            serverCo = Client(gameServerAddress, Main.SERVER_PORT)
//            while (serverCo.receive(buffer).also({ bytesReceived = it }) != -1) {
//                array.setArray(buffer, bytesReceived)
//                processMsgStack(reader.processBuffer(array), "r")
//                client.send(trimBuffer(buffer, bytesReceived))
//            }
//            serverCo.close()
//            println("Deconnected from game server.")
//        }
//    }
//
//    fun processMsgStack(msgStack: LinkedList<NetworkMessage>, direction: String): Boolean {
//        var msg: NetworkMessage
//        while (msgStack.poll().also({ msg = it }) != null) {
//            println(direction, msg)
//            //if(direction.equals("r"))
////Reflection.displayMessageFields(msg);
//            if (direction == "r" && msg.getId() === 42) {
//                val SSDM: SelectedServerDataMessage = msg as SelectedServerDataMessage
//                gameServerAddress = SSDM.address
//                mustDeconnectClient = true
//                if (msgStack.size > 1) throw FatalError("Little problem !")
//                SSDM.address = Main.LOCALHOST
//                client.send(SSDM.pack(0))
//                return false
//            }
//        }
//        return true
//    }
//
//}
//
//internal class Client(serverIP: String, port: Int) {
//
//    private var client: Socket = Socket(serverIP, port)
//    private var inputStream = client.getInputStream()
//    private var outputStream = client.getOutputStream()
//
//    @Throws(IOException::class)
//    protected fun receive(buffer: ByteArray): Int {
//        return inputStream.read(buffer)
//    }
//
//    @Throws(IOException::class)
//    protected fun receive(buffer: ByteArray, timeout: Int): Int {
//        client.soTimeout = timeout
//        val bytes = receive(buffer)
//        client.soTimeout = 0
//        return bytes
//    }
//
//    protected fun close() {
//        inputStream.close()
//        outputStream.close()
//        client.close()
//    }
//
//    protected val isClosed: Boolean
//        protected get() = client.isClosed
//}