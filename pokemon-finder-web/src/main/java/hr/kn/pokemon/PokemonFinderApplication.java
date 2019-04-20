package hr.kn.pokemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import hr.kn.pokemon.api.auth.GooglePlayServicesAuthenticator;
import okhttp3.OkHttpClient;
import svarzee.gps.gpsoauth.Gpsoauth;

@SpringBootApplication
public class PokemonFinderApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PokemonFinderApplication.class, args);
	}
	
	@Bean
	public GooglePlayServicesAuthenticator authenticatior() {
		return new GooglePlayServicesAuthenticator(new Gpsoauth(new OkHttpClient()));
	}

}
