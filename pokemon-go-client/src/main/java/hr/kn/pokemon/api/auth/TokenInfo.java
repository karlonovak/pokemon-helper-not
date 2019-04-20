package hr.kn.pokemon.api.auth;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;
import svarzee.gps.gpsoauth.AuthToken;

public class TokenInfo {
	private AuthToken authToken;
	private String refreshToken;
	
	public TokenInfo(){}

	public TokenInfo(AuthToken authToken, String refreshToken) {
		this.authToken = authToken;
		this.refreshToken = refreshToken;
	}
	
	public AuthInfo tokenToAuthInfo() {
		AuthInfo.Builder builder = AuthInfo.newBuilder();
		builder.setProvider("google");
		builder.setToken(AuthInfo.JWT.newBuilder().setContents(getAuthToken().getToken()).setUnknown2(59).build());
		return builder.build();
	}

	public AuthToken getAuthToken() {
		return authToken;
	}

	public void setAuthToken(AuthToken authToken) {
		this.authToken = authToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
