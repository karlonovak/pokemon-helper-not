package hr.kn.pokemon.api.client;

import hr.kn.pokemon.api.client.RequestHandler;
import hr.kn.pokemon.api.model.RequestMandatoryParams;
import okhttp3.OkHttpClient;

public class RequestHandlers {
//	private static Map<String, RequestHandler> byUser = new HashMap<>();
	
	public static RequestHandler myRequestHandler(RequestMandatoryParams mandatoryParams) {
//		if(byUser.get(mandatoryParams.getUsername()) == null) {
			RequestHandler newHandler = new RequestHandler(new OkHttpClient(), mandatoryParams);
//			byUser.put(mandatoryParams.getUsername(), newHandler);
			return newHandler;
//		} else {
//			RequestHandler handler = byUser.get(mandatoryParams.getUsername());
//			handler.setClient(new OkHttpClient());
//			return handler;
//		} 
	}
}
