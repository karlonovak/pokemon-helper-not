package hr.kn.pokemon.finder;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PokeVisionResponse {

	@JsonProperty("pokemon")
	private Set<PokeVisionPokemon> pokemons;
	
	public PokeVisionResponse(){}

	public PokeVisionResponse(Set<PokeVisionPokemon> pokemons) {
		super();
		this.pokemons = pokemons;
	}

	public Set<PokeVisionPokemon> getPokemons() {
		return pokemons;
	}

	public void setPokemons(Set<PokeVisionPokemon> pokemons) {
		this.pokemons = pokemons;
	}
}
