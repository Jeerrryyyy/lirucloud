package de.liruhg.lirucloud.library.network.protocol

enum class PacketId(val id: Int) {

    PACKET_CLIENT_REQUEST_HANDSHAKE(1),
    PACKET_CLIENT_HANDSHAKE_RESULT(2)
}