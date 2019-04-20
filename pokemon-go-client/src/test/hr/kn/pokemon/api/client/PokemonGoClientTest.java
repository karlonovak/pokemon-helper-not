package hr.kn.pokemon.api.client;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import hr.kn.pokemon.api.model.CatchablePokemon;
import svarzee.gps.gpsoauth.Gpsoauth.TokenRequestFailed;

public class PokemonGoClientTest {
	
	@Test
	public void fetchCatchablePokemonTest() throws IOException, TokenRequestFailed {
		PokemonGoClient client = new PokemonGoClient();
		List<CatchablePokemon> catchablePokemons = client.fetchCatchablePokemon(45.822563, 16.017508);
		
		catchablePokemons
			.stream()
			.forEach(catchablePokemon -> System.out.println("Found pokemon with ID: " + catchablePokemon.getId()));
	}

}
