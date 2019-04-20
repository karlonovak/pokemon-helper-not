package hr.kn.pokemon.helper;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class SocketMessageSender {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SocketMessageSender.class);

	public static void socketSend(String message, WebSocketSession session) {
		LOGGER.info("Socket message: " + message);
		try {
			session.sendMessage(new TextMessage((message).getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
