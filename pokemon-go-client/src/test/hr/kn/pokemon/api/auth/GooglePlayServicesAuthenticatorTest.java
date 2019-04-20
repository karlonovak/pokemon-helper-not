package hr.kn.pokemon.api.auth;


import java.io.IOException;

import org.junit.Test;

import hr.kn.pokemon.api.auth.GooglePlayServicesAuthenticator.TokenInfo;
import okhttp3.OkHttpClient;
import svarzee.gps.gpsoauth.Gpsoauth;
import svarzee.gps.gpsoauth.Gpsoauth.TokenRequestFailed;

/**
 * Use to login with google username and password
 */
public class GooglePlayServicesAuthenticatorTest {
	
	private static final String user = "karlonovak";
	private static final String pass = "XXXXX";
	
	@Test
	public void googlePlayLoginTest() throws IOException, TokenRequestFailed {
		GooglePlayServicesAuthenticator authenticator = new GooglePlayServicesAuthenticator(new Gpsoauth(new OkHttpClient()));
		TokenInfo tokenInfo = authenticator.login(user, pass);
		System.out.println(tokenInfo.authToken.getToken());
	}

}
