package hr.kn.pokemon.finder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

import hr.kn.pokemon.helper.AllPokemons;
import hr.kn.pokemon.helper.Locations;

@Service
public class PokemonFinderService {

	private static List<String> coordinatesSamobor = Lists.newArrayList(
			"45.813449110000000,15.711205600000000",
			"45.811908630000000,15.712385770000000",
			"45.810786890000000,15.712514520000000",
			"45.809373470000000,15.710266830000000",
			"45.806277280000000,15.712498430000000",
			"45.806067870000000,15.717841390000000",
			"45.805155430000000,15.711768870000000",
			"45.802403060000000,15.709837680000000",
			"45.801041780000000,15.709193940000000",
			"45.799351360000000,15.709258320000000",
			"45.797601050000000,15.709537270000000",
			"45.794788460000000,15.712155100000000",
			"45.800159180000000,15.712991950000000",
			"45.799141920000000,15.715459590000000",
			"45.796688470000000,15.719236140000000",
			"45.801565350000000,15.712348220000000",
			"45.801685030000000,15.714880230000000",
			"45.800787470000000,15.717240570000000",
			"45.799441120000000,15.719772580000000",
			"45.797870330000000,15.722218750000000",
			"45.795297130000000,15.726896520000000",
			"45.798558490000000,15.730394120000000",
			"45.801236250000000,15.726338620000000",
			"45.805155430000000,15.724021200000000",
			"45.804557100000000,15.719450710000000",
			"45.805260130000000,15.717283490000000",
			"45.804272890000000,15.716253520000000",
			"45.803240750000000,15.715271830000000",
			"45.803083680000000,15.716612940000000",
			"45.801692510000000,15.719638470000000",
			"45.800622920000000,15.722202660000000",
			"45.803330500000000,15.722733740000000",
			"45.790629160000000,15.719139580000000",
			"45.794384510000000,15.708142520000000",
			"45.803398000000000,15.715379000000000",
			"45.803173000000000,15.716527000000000",
			"45.803555000000000,15.715226000000000",
			"45.813113000000000,15.710388000000000");
	
	private static List<String> coordinatesMaksimir = Lists.newArrayList("45.823132,16.016771",
			"45.822100000000000,16.019064000000000",
			"45.820575000000000,16.020952000000000",
			"45.819648000000000,16.016275000000000",
			"45.821637000000000,16.014944000000000",
			"45.818900000000000,16.020824000000000",
			"45.820889000000000,16.026767000000000");

	private static List<Integer> interestedPokemons = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 15, 18, 22, 23, 24,
			25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 43, 44, 45, 47, 49, 50, 51, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,69, 70,
			71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 100);

	private static List<Integer> notInterestedPokemons = Lists.newArrayList(133, 116, 117, 120);

	@SuppressWarnings("rawtypes")
	public List<PokemonFinderPokemon> findPokemons(Locations location) throws InterruptedException {
		List<PokemonFinderPokemon> nearbyPokemons = Lists.newArrayList();
		
		List<String> coordinates;
		
		switch(location) {
		case MAKSIMIR:
			coordinates = coordinatesMaksimir;
			System.out.println("Scannin Maksimir");
			break;
		case SAMOBOR:
			coordinates = coordinatesSamobor;
			System.out.println("Scannin Samobor");
			break;
		default:
			coordinates = Lists.newArrayList();
			break;
		}
		
		
		for (String coordinate : coordinates) {
			String lat = coordinate.split(",")[0];
			String lon = coordinate.split(",")[1];

			RestTemplate rt = new RestTemplate();
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.set("User-Agent", "Mozilla/5.0");

			HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(null, requestHeaders);

			String url = "https://pokevision.com/map/data/" + lat + "/" + lon;
			HttpEntity<PokeVisionResponse> response = rt.exchange(url, HttpMethod.GET, requestEntity,
					PokeVisionResponse.class);

			Set<PokeVisionPokemon> pokemons = response.getBody().getPokemons();
			StringBuilder emailPokemons = new StringBuilder();
			for (PokeVisionPokemon pokemon : pokemons) {
				if (interestedPokemons.contains(pokemon.getPokemonId())
						|| (pokemon.getPokemonId() > 100 && !notInterestedPokemons.contains(pokemon.getPokemonId()))) {

					int pozicijaPokemona = AllPokemons.pokemonNames
							.indexOf(Integer.valueOf(pokemon.getPokemonId()).toString());
					String ime = AllPokemons.pokemonNames.get(pozicijaPokemona + 1);
					Date expireDate = new Date(pokemon.getExpires() * 1000);
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(expireDate);  

					System.out.println("Nasao sam " + ime + " " + "http://maps.google.com/?q=" + pokemon.getLatitude()
							+ "," + pokemon.getLongitude() + " istice u " + cal.get(Calendar.HOUR_OF_DAY) + ":"
							+ cal.get(Calendar.MINUTE));
					emailPokemons.append("Nasao sam " + ime + " " + "http://maps.google.com/?q=" + pokemon.getLatitude()
							+ "," + pokemon.getLongitude() + " istice u " + cal.get(Calendar.HOUR_OF_DAY) + ":"
							+ cal.get(Calendar.MINUTE));
					
					PokemonFinderPokemon pokemonFinderPokemon = new PokemonFinderPokemon();
					pokemonFinderPokemon.setExpires(expireDate);
					pokemonFinderPokemon.setLatitude(pokemon.getLatitude());
					pokemonFinderPokemon.setLongitude(pokemon.getLongitude());
					pokemonFinderPokemon.setName(ime);
					nearbyPokemons.add(pokemonFinderPokemon);
				}
			}

//			if(emailPokemons.toString().length() > 0)
//				sendMail(emailPokemons.toString());
		}
		
		return nearbyPokemons;
	}

//	private void sendMail(String message) throws MessagingException {
//		 JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		 mailSender.setHost("smtp.gmail.com");
//		 mailSender.setPort(587);
//		 mailSender.setUsername("mkofficemailer@gmail.com");
//		 mailSender.setPassword("mkofficemailer12345");
//		
//		 Properties properties = new Properties();
//		 properties.put("mail.transport.protocol", "smtp");
//		 properties.put("mail.smtp.auth", "true");
//		 properties.put("mail.smtp.starttls.enable", "true");
//		 mailSender.setJavaMailProperties(properties);
//		
//		 MimeMessage mimeMessage = mailSender.createMimeMessage();
//		 MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
//		 mimeMessage.setContent(message, "text/html; charset=utf-8");
//		 helper.setTo("karlo.novak@gmail.com");
//		 helper.setSubject("Nasao sam Pokemone");
//		 mailSender.send(mimeMessage);
//	}
}
