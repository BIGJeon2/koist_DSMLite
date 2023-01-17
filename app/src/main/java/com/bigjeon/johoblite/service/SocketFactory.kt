package com.bigjeon.johoblite.service

import io.socket.client.IO
import java.net.URISyntaxException

class SocketFactory {
	companion object {
		private lateinit var socket: io.socket.client.Socket
		fun get(): io.socket.client.Socket {
			try {
				socket = IO.socket("http://101.101.216.123:5009")
			}catch (e: URISyntaxException){
			
			}
			return socket
		}
	}
	
}