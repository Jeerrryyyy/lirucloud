package de.liruhg.lirucloud.library.network.protocol

enum class PacketId(val id: Int) {

    PACKET_REQUEST_HANDSHAKE(1),
    PACKET_HANDSHAKE_RESULT(2),
    PACKET_REQUEST_SERVERS(3),
    PACKET_UPDATE_LOAD_STATUS(4),
    PACKET_REQUEST_PROXY_PROCESS(5),
    PACKET_REQUEST_SERVER_PROCESS(6),
}