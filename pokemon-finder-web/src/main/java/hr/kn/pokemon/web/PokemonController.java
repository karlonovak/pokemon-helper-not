package hr.kn.pokemon.web;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import hr.kn.pokemon.api.PokemonApiService;
import hr.kn.pokemon.api.model.CatchablePokemon;
import hr.kn.pokemon.api.model.RequestMandatoryParams;
import hr.kn.pokemon.finder.PokemonFinderPokemon;
import hr.kn.pokemon.finder.PokemonFinderService;
import hr.kn.pokemon.helper.Locations;

@RestController
public class PokemonController {

	@Autowired
	private PokemonFinderService pokemonFinderService;
	
	@Autowired
	private PokemonApiService pokemonApiService;

	@RequestMapping("/near/{location}")
	public List<PokemonFinderPokemon> findNearby(@PathVariable("location") Integer location) throws MessagingException, InterruptedException {
		if(location == 1)
			return Lists.newArrayList(Sets.newConcurrentHashSet(pokemonFinderService.findPokemons(Locations.SAMOBOR)));
		else if(location == 2)
			return Lists.newArrayList(Sets.newConcurrentHashSet(pokemonFinderService.findPokemons(Locations.MAKSIMIR)));
		
		return Lists.newArrayList(Sets.newConcurrentHashSet(pokemonFinderService.findPokemons(Locations.SAMOBOR)));
	}
	
//	@RequestMapping(value="/catch",method=RequestMethod.POST, produces="text/plain")
//	public String catchPokemonAtArea(@RequestBody RequestMandatoryParams mandatoryParams) throws Exception {
//		return pokemonApiService.catchPokemonAtArea(mandatoryParams);
//	}
	
	@RequestMapping(value="/catchable",method=RequestMethod.POST, produces="text/plain")
	public List<CatchablePokemon> catchable(@RequestBody RequestMandatoryParams mandatoryParams) throws Exception {
		return pokemonApiService.fetchCatchablePokemon(mandatoryParams);
	}
	
}
