package hr.kn.pokemon.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hr.kn.pokemon.api.auth.GooglePlayServicesAuthenticator;
import hr.kn.pokemon.api.model.RequestMandatoryParams;
import svarzee.gps.gpsoauth.Gpsoauth.TokenRequestFailed;

@RestController
public class LoginController {
	
	@Autowired
	private GooglePlayServicesAuthenticator authenticator;
	
	@RequestMapping(value= "/login")
	public RequestMandatoryParams login(@RequestParam("username") String username, 
						 @RequestParam("password") String password) throws IOException, TokenRequestFailed {
		RequestMandatoryParams mandatoryParams = new RequestMandatoryParams();
		mandatoryParams.setTokenInfo(authenticator.login(username, password));
		mandatoryParams.setLongitude(0);
		mandatoryParams.setLatitude(0);
		mandatoryParams.setUsername(username);
		
		return mandatoryParams;
//		state.setAuthInfo(authenticator.getAuthInfo());
	}
}
