package hr.kn.pokemon.api.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.protobuf.InvalidProtocolBufferException;

import POGOProtos.Map.MapCellOuterClass;
import POGOProtos.Map.Pokemon.MapPokemonOuterClass.MapPokemon;
import POGOProtos.Map.Pokemon.WildPokemonOuterClass.WildPokemon;
import POGOProtos.Networking.Requests.RequestTypeOuterClass;
import POGOProtos.Networking.Requests.Messages.GetMapObjectsMessageOuterClass;
import POGOProtos.Networking.Requests.Messages.GetMapObjectsMessageOuterClass.GetMapObjectsMessage;
import POGOProtos.Networking.Responses.GetMapObjectsResponseOuterClass;
import hr.kn.google.geometry.GeometryUtil;
import hr.kn.pokemon.api.model.CatchablePokemon;
import hr.kn.pokemon.api.model.RequestMandatoryParams;
import svarzee.gps.gpsoauth.Gpsoauth.TokenRequestFailed;

public class PokemonGoClient {
	
	public  List<CatchablePokemon> fetchCatchablePokemon(RequestMandatoryParams mandatoryParams) throws IOException, TokenRequestFailed {
		GetMapObjectsMessage.Builder builder = 
					GetMapObjectsMessageOuterClass.GetMapObjectsMessage.newBuilder()
						.setLatitude(mandatoryParams.getLatitude())
						.setLongitude(mandatoryParams.getLongitude());
		
		for (Long cellId : GeometryUtil.getCellIds(mandatoryParams.getLatitude(), mandatoryParams.getLongitude(), 9)) {
			builder.addCellId(cellId);
			builder.addSinceTimestampMs(0);
		}
		
		ServerRequest serverRequest = new ServerRequest(RequestTypeOuterClass.RequestType.GET_MAP_OBJECTS, builder.build());
	    RequestHandlers.myRequestHandler(mandatoryParams).sendServerRequests(serverRequest);
		GetMapObjectsResponseOuterClass.GetMapObjectsResponse response = null;
		try {
			response = GetMapObjectsResponseOuterClass.GetMapObjectsResponse
					.parseFrom(serverRequest.getData());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		
		Set<CatchablePokemon> catchablePokemons = new HashSet<>();
		for (MapCellOuterClass.MapCell mapCell : response.getMapCellsList()) {
			for(MapPokemon mapPokemon : mapCell.getCatchablePokemonsList()) {
				catchablePokemons.add(new CatchablePokemon(mapPokemon));
			}
			for(WildPokemon wildPokemon : mapCell.getWildPokemonsList()) {
				catchablePokemons.add(new CatchablePokemon(wildPokemon));
			}
		}
		
		return new ArrayList<>(catchablePokemons);
	}
	
}
