package hr.kn.pokemon.api;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.google.protobuf.InvalidProtocolBufferException;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import hr.kn.pokemon.api.client.PokemonGoClient;
import hr.kn.pokemon.api.model.CatchResult;
import hr.kn.pokemon.api.model.CatchablePokemon;
import hr.kn.pokemon.api.model.EncounterResult;
import hr.kn.pokemon.api.model.RequestMandatoryParams;
import svarzee.gps.gpsoauth.Gpsoauth.TokenRequestFailed;
import static hr.kn.pokemon.helper.SocketMessageSender.*;

@Service
public class PokemonApiService {

	public void catchPokemonAtArea(RequestMandatoryParams mandatoryParams, WebSocketSession session) throws Exception {
		socketSend("Lovim pokemone na lokaciji " + 
						mandatoryParams.getLatitude() + "," + mandatoryParams.getLongitude() + "<br>", session);
		Thread.sleep(5000);		
		List<CatchablePokemon> catchablePokemons = fetchCatchablePokemon(mandatoryParams);

		socketSend("Pronašao sam " + catchablePokemons.size() + " pokemona<br>", session);
		Thread.sleep(3000);
		for (CatchablePokemon cp : catchablePokemons) {
			int count = 0;
			int maxTries = 3;
			while (true) {
				try {
					EncounterResult encResult = cp.encounterPokemon(mandatoryParams);
					Thread.sleep(3000);
					if (encResult.wasSuccessful()) {
						socketSend("<br>Lovim " + PokemonId.forNumber((int) cp.getId()) + "<br>", session);
						Thread.sleep(3000);

						CatchResult result = new CatchResult();
						result = cp.catchPokemonWithRazzBerry(mandatoryParams);
						socketSend("Rezultat lova:" + " " + result.getStatus() + "<br>", session);
						Thread.sleep(3000);
						break;
					}
				} catch (InvalidProtocolBufferException e) {
					if (++count == maxTries)
						throw e;
					else {
						socketSend("Greška kod lova " + e.getLocalizedMessage() + ", retry... <br>", session);
						Thread.sleep(10000);
					}
				}
			}
		}
		
		socketSend("KRAJ", session);
	}

	public List<CatchablePokemon> fetchCatchablePokemon(RequestMandatoryParams mandatoryParams)
			throws IOException, TokenRequestFailed {
		PokemonGoClient client = new PokemonGoClient();
		return client.fetchCatchablePokemon(mandatoryParams);
	}
}
