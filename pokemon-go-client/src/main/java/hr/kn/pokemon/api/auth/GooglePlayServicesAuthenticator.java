package hr.kn.pokemon.api.auth;

import java.io.IOException;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;
import svarzee.gps.gpsoauth.AuthToken;
import svarzee.gps.gpsoauth.Gpsoauth;
import svarzee.gps.gpsoauth.Gpsoauth.TokenRequestFailed;

/**
 * Use to login with google username and password via google play services
 */
public class GooglePlayServicesAuthenticator {

	private static String GOOGLE_LOGIN_ANDROID_ID = "9774d56d682e549c";
	private static String GOOGLE_LOGIN_SERVICE = "audience:server:client_id:848232511240-7so421jotr2609rmqakceuu1luuq0ptb.apps.googleusercontent.com";
	private static String GOOGLE_LOGIN_APP = "com.nianticlabs.pokemongo";
	private static String GOOGLE_LOGIN_CLIENT_SIG = "321187995bc7cdc2b5fc91b11a96e2baa8602c62";

	private TokenInfo tokenInfo;
	private Gpsoauth gpsoauth;
	private String username;

	public GooglePlayServicesAuthenticator(Gpsoauth gpsoauth) {
		this.gpsoauth = gpsoauth;
	}

	public TokenInfo login(String username, String password) throws IOException, TokenRequestFailed {
		String refreshToken = gpsoauth.performMasterLoginForToken(username, password, GOOGLE_LOGIN_ANDROID_ID);
		AuthToken authToken = gpsoauth.performOAuthForToken(username, refreshToken, GOOGLE_LOGIN_ANDROID_ID,
				GOOGLE_LOGIN_SERVICE, GOOGLE_LOGIN_APP, GOOGLE_LOGIN_CLIENT_SIG);
		this.tokenInfo = new TokenInfo(authToken, refreshToken);
		this.username = username;
		return this.tokenInfo;
	}

	public TokenInfo refreshToken(String username, String refreshToken) throws IOException, TokenRequestFailed {
		AuthToken authToken = gpsoauth.performOAuthForToken(username, refreshToken, GOOGLE_LOGIN_ANDROID_ID,
				GOOGLE_LOGIN_SERVICE, GOOGLE_LOGIN_APP, GOOGLE_LOGIN_CLIENT_SIG);
		return new TokenInfo(authToken, refreshToken);
	}

	public String getTokenId() throws IOException, TokenRequestFailed {
		if (isTokenIdExpired()) {
			this.tokenInfo = refreshToken(this.username, tokenInfo.getRefreshToken());
		}
		return tokenInfo.getAuthToken().getToken();
	}

	public AuthInfo getAuthInfo() {
		AuthInfo.Builder builder = AuthInfo.newBuilder();
		builder.setProvider("google");
		builder.setToken(AuthInfo.JWT.newBuilder().setContents(tokenInfo.getAuthToken().getToken()).setUnknown2(59).build());
		return builder.build();
	}

	public boolean isTokenIdExpired() {
		return tokenInfo.getAuthToken().getExpiry() > System.currentTimeMillis() / 1000 - 60;
	}
}
