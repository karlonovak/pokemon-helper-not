package hr.kn.pokemon.api.client;

import POGOProtos.Networking.Envelopes.AuthTicketOuterClass;
import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import POGOProtos.Networking.Envelopes.ResponseEnvelopeOuterClass;
import hr.kn.pokemon.api.model.RequestMandatoryParams;

import com.google.protobuf.ByteString;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class RequestHandler {
	
	private RequestMandatoryParams mandatoryParams;
	
	private static final String API_ENDPOINT = "https://pgorelease.nianticlabs.com/plfe/rpc";
	
	private RequestEnvelopeOuterClass.RequestEnvelope.Builder builder;
	private boolean hasRequests;
	private String apiEndpoint;
	private OkHttpClient client;
	private Long requestId = new Random().nextLong();

	private AuthTicketOuterClass.AuthTicket lastAuth;
	
	public RequestHandler(RequestMandatoryParams mandatoryParams) {
		this.client = new OkHttpClient();
		this.mandatoryParams = mandatoryParams;
	}
	
	public RequestHandler(OkHttpClient client, RequestMandatoryParams mandatoryParams){
		this.client = client;
		this.mandatoryParams = mandatoryParams;
		this.apiEndpoint = API_ENDPOINT;
	}

	public void sendServerRequests(ServerRequest... serverRequests) {
		if (serverRequests.length == 0) {
			return;
		}
		RequestEnvelopeOuterClass.RequestEnvelope.Builder builder = RequestEnvelopeOuterClass.RequestEnvelope.newBuilder();
		resetBuilder(builder);

		for (ServerRequest serverRequest : serverRequests) {
			builder.addRequests(serverRequest.getRequest());
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		RequestEnvelopeOuterClass.RequestEnvelope request = builder.build();
		try {
			request.writeTo(stream);
		} catch (IOException e) {
			//Should never happen
		}

		if(apiEndpoint == null)
			apiEndpoint = API_ENDPOINT;
		
		RequestBody body = RequestBody.create(null, stream.toByteArray());
		okhttp3.Request httpRequest = new okhttp3.Request.Builder()
				.url(apiEndpoint)
				.post(body)
				.build();

		try (Response response = client.newCall(httpRequest).execute()) {
			if (response.code() != 200) {
				System.out.println("Got a unexpected http code : " + response.code());
			}

			ResponseEnvelopeOuterClass.ResponseEnvelope responseEnvelop  = null;
			try (InputStream content = response.body().byteStream()) {
				responseEnvelop = ResponseEnvelopeOuterClass.ResponseEnvelope.parseFrom(content);
			} catch (IOException e) {
				// retrieved garbage from the server
				System.out.println("Received malformed response : " + e.getMessage());
			}

			if (responseEnvelop.getApiUrl() != null && responseEnvelop.getApiUrl().length() > 0) {
				apiEndpoint = "https://" + responseEnvelop.getApiUrl() + "/rpc";
			}

			if (responseEnvelop.hasAuthTicket()) {
				lastAuth = responseEnvelop.getAuthTicket();
			}
			
			if (responseEnvelop.getStatusCode() == 102) {
				System.out.println(String.format("Error %s in API Url %s",
						responseEnvelop.getApiUrl(), responseEnvelop.getError()));
			} else if (responseEnvelop.getStatusCode() == 53) {
				// 53 means that the api_endpoint was not correctly set, should be at this point, though, so redo the request
				sendServerRequests(serverRequests);
				return;
			}

			/**
			 * map each reply to the numeric response,
			 * ie first response = first request and send back to the requests to handle.
			 * */
			int count = 0;
			for (ByteString payload : responseEnvelop.getReturnsList()) {
				ServerRequest serverReq = serverRequests[count];
				/**
				 * TODO: Probably all other payloads are garbage as well in this case,
				 * so might as well throw an exception and leave this loop */
				if (payload != null) {
					serverReq.handleData(payload);
				}
				count++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void resetBuilder(RequestEnvelopeOuterClass.RequestEnvelope.Builder builder) {
		builder.setStatusCode(2);
		builder.setRequestId(getRequestId());
		if (lastAuth != null
				&& lastAuth.getExpireTimestampMs() > 0
				&& lastAuth.getExpireTimestampMs() > System.currentTimeMillis()) {
			builder.setAuthTicket(lastAuth);
		} else {
//			System.out.println("Authenticated with static token");
			builder.setAuthInfo(mandatoryParams.getTokenInfo().tokenToAuthInfo());
		}
		builder.setUnknown12(989);
		builder.setLatitude(mandatoryParams.getLatitude());
		builder.setLongitude(mandatoryParams.getLongitude());
		builder.setAltitude(0);
	}


	/**
	 * Build request envelope outer class . request envelope.
	 *
	 * @return the request envelope outer class . request envelope
	 */
	public RequestEnvelopeOuterClass.RequestEnvelope build() {
		if (!hasRequests) {
			throw new IllegalStateException("Attempting to send request envelop with no requests");
		}
		return builder.build();
	}

	public void setLatitude(double latitude) {
		builder.setLatitude(latitude);
	}

	public void setLongitude(double longitude) {
		builder.setLongitude(longitude);
	}

	public void setAltitude(double altitude) {
		builder.setAltitude(altitude);
	}
	
	public Long getRequestId() {
		return ++requestId;
	}

	public AuthTicketOuterClass.AuthTicket getLastAuth() {
		return lastAuth;
	}

	public void setLastAuth(AuthTicketOuterClass.AuthTicket lastAuth) {
		this.lastAuth = lastAuth;
	}

	public OkHttpClient getClient() {
		return client;
	}

	public void setClient(OkHttpClient client) {
		this.client = client;
	}
}
