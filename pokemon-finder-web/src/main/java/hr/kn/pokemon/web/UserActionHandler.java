package hr.kn.pokemon.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import hr.kn.pokemon.api.PokemonApiService;
import hr.kn.pokemon.api.model.RequestMandatoryParams;

public class UserActionHandler extends TextWebSocketHandler {

	@Autowired
	private PokemonApiService pokemonApiService;

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		try {
			RequestMandatoryParams mandatoryParams = new ObjectMapper().readValue(message.getPayload(),
					RequestMandatoryParams.class);
			if (mandatoryParams.getAction().equals(UserActions.CATCH.name)) {
				pokemonApiService.catchPokemonAtArea(mandatoryParams, session);
			}
		} catch (Exception e) {
			session.sendMessage(new TextMessage(("Greška kod lova... " + e.getLocalizedMessage()).getBytes()));
		}
	}

}
