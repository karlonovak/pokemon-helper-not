package hr.kn.pokemon.api.model;

import com.google.protobuf.InvalidProtocolBufferException;

import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import POGOProtos.Map.Pokemon.MapPokemonOuterClass.MapPokemon;
import POGOProtos.Map.Pokemon.WildPokemonOuterClass.WildPokemon;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import POGOProtos.Networking.Requests.Messages.CatchPokemonMessageOuterClass.CatchPokemonMessage;
import POGOProtos.Networking.Requests.Messages.EncounterMessageOuterClass;
import POGOProtos.Networking.Requests.Messages.UseItemCaptureMessageOuterClass.UseItemCaptureMessage;
import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse;
import POGOProtos.Networking.Responses.EncounterResponseOuterClass;
import POGOProtos.Networking.Responses.UseItemCaptureResponseOuterClass.UseItemCaptureResponse;
import hr.kn.pokemon.api.client.RequestHandlers;
import hr.kn.pokemon.api.client.ServerRequest;

public class CatchablePokemon {

	private long id;
	private double latitude;
	private double longitude;
	private long expirationTimestamp;
	private final long encounterId;
	private final String spawnPointId;

	public CatchablePokemon(MapPokemon mapPokemon) {
		this.id = mapPokemon.getPokemonIdValue();
		this.latitude = mapPokemon.getLatitude();
		this.longitude = mapPokemon.getLongitude();
		this.expirationTimestamp = mapPokemon.getExpirationTimestampMs();
		this.encounterId = mapPokemon.getEncounterId();
		this.spawnPointId = mapPokemon.getSpawnPointId();
	}

	public CatchablePokemon(WildPokemon wildPokemon) {
		this.id = wildPokemon.getPokemonData().getPokemonIdValue();
		this.latitude = wildPokemon.getLatitude();
		this.longitude = wildPokemon.getLongitude();
		this.expirationTimestamp = wildPokemon.getTimeTillHiddenMs();
		this.encounterId = wildPokemon.getEncounterId();
		this.spawnPointId = wildPokemon.getSpawnPointId();
	}

	public EncounterResult encounterPokemon(RequestMandatoryParams mandatoryParams) throws InvalidProtocolBufferException  {
		EncounterMessageOuterClass.EncounterMessage reqMsg = EncounterMessageOuterClass.EncounterMessage.newBuilder()
				.setEncounterId(getEncounterId())
				.setPlayerLatitude(latitude)
				.setPlayerLongitude(longitude)
				.setSpawnPointId(getSpawnPointId())
				.build();
		
		ServerRequest serverRequest = new ServerRequest(RequestType.ENCOUNTER, reqMsg);
		RequestHandlers.myRequestHandler(mandatoryParams).sendServerRequests(serverRequest);
		EncounterResponseOuterClass.EncounterResponse response = null;
		response = EncounterResponseOuterClass.EncounterResponse.parseFrom(serverRequest.getData());
		return new EncounterResult(response);
	}

	/**
	 * Tries to catch a pokemon (will attempt to use a pokeball, if you have
	 * none will use greatball etc) and uwill use a single razz berry if
	 * available.
	 *
	 * @return CatchResult
	 * @throws InvalidProtocolBufferException 
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchResult catchPokemonWithRazzBerry(RequestMandatoryParams mandatoryParams) throws InvalidProtocolBufferException  {
		Pokeball pokeball;

		ItemBag bag = new ItemBag().fetchItemBag(mandatoryParams);
		if (bag.getItem(ItemId.ITEM_POKE_BALL).getCount() > 0) {
			pokeball = Pokeball.POKEBALL;
		} else if (bag.getItem(ItemId.ITEM_GREAT_BALL).getCount() > 0) {
			pokeball = Pokeball.GREATBALL;
		} else if (bag.getItem(ItemId.ITEM_ULTRA_BALL).getCount() > 0) {
			pokeball = Pokeball.ULTRABALL;
		} else {
			pokeball = Pokeball.MASTERBALL;
		}

		useItem(ItemId.ITEM_RAZZ_BERRY, mandatoryParams);
		return catchPokemon(pokeball, -1, -1, mandatoryParams);
	}

	/**
	 * Tries to catch a pokemon (will attempt to use a pokeball, if you have
	 * none will use greatball etc).
	 *
	 * @return CatchResult
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchResult catchPokemon(RequestMandatoryParams mandatoryParams) throws Exception  {
		Pokeball pokeball;

		ItemBag bag = new ItemBag().fetchItemBag(mandatoryParams);
		if (bag.getItem(ItemId.ITEM_POKE_BALL).getCount() > 0) {
			pokeball = Pokeball.POKEBALL;
		} else if (bag.getItem(ItemId.ITEM_GREAT_BALL).getCount() > 0) {
			pokeball = Pokeball.GREATBALL;
		} else if (bag.getItem(ItemId.ITEM_ULTRA_BALL).getCount() > 0) {
			pokeball = Pokeball.ULTRABALL;
		} else {
			pokeball = Pokeball.MASTERBALL;
		}

		return catchPokemon(pokeball, mandatoryParams);
	}

	/**
	 * Tries to catch a pokeball with the given type.
	 *
	 * @param pokeball
	 *            Type of pokeball
	 * @return CatchResult
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchResult catchPokemon(Pokeball pokeball, RequestMandatoryParams mandatoryParams) throws Exception  {
		return catchPokemon(pokeball, -1, mandatoryParams);
	}

	/**
	 * Tried to catch a pokemon with given pokeball and max number of pokeballs.
	 *
	 * @param pokeball
	 *            Type of pokeball
	 * @param amount
	 *            Max number of pokeballs to use
	 * @return CatchResult
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchResult catchPokemon(Pokeball pokeball, int amount, RequestMandatoryParams mandatoryParams) throws Exception  {
		return catchPokemon(1.0, 1.95 + Math.random() * 0.05, 0.85 + Math.random() * 0.15, pokeball, amount, mandatoryParams);
	}

	/**
	 * Tried to catch a pokemon with given pokeball and max number of pokeballs.
	 *
	 * @param pokeball
	 *            Type of pokeball
	 * @param amount
	 *            Max number of pokeballs to use
	 * @param razberryLimit
	 *            Max number of razberrys to use
	 * @return CatchResult
	 * @throws InvalidProtocolBufferException 
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchResult catchPokemon(Pokeball pokeball, int amount, int razberryLimit, RequestMandatoryParams mandatoryParams) throws InvalidProtocolBufferException {
		return catchPokemon(1.0, 1.95 + Math.random() * 0.05, 0.85 + Math.random() * 0.15, pokeball, amount,
				razberryLimit, mandatoryParams);
	}

	/**
	 * Tries to catch a pokemon.
	 *
	 * @param normalizedHitPosition
	 *            the normalized hit position
	 * @param normalizedReticleSize
	 *            the normalized hit reticle
	 * @param spinModifier
	 *            the spin modifier
	 * @param type
	 *            Type of pokeball to throw
	 * @param amount
	 *            Max number of Pokeballs to throw, negative number for
	 *            unlimited
	 * @return CatchResult of resulted try to catch pokemon
	 * @throws InvalidProtocolBufferException 
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchResult catchPokemon(double normalizedHitPosition, double normalizedReticleSize, double spinModifier,
			Pokeball type, int amount, RequestMandatoryParams mandatoryParams) throws InvalidProtocolBufferException  {

		return catchPokemon(normalizedHitPosition, normalizedReticleSize, spinModifier, type, amount, -1, mandatoryParams);
	}

	/**
	 * Tries to catch a pokemon.
	 *
	 * @param normalizedHitPosition
	 *            the normalized hit position
	 * @param normalizedReticleSize
	 *            the normalized hit reticle
	 * @param spinModifier
	 *            the spin modifier
	 * @param type
	 *            Type of pokeball to throw
	 * @param amount
	 *            Max number of Pokeballs to throw, negative number for
	 *            unlimited
	 * @param razberriesLimit
	 *            The maximum amount of razberries to use, -1 for unlimited
	 * @return CatchResult of resulted try to catch pokemon
	 * @throws InvalidProtocolBufferException 
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchResult catchPokemon(double normalizedHitPosition, double normalizedReticleSize, double spinModifier,
			Pokeball type, int amount, int razberriesLimit, RequestMandatoryParams mandatoryParams) throws InvalidProtocolBufferException  {
		int razberries = 0;
		int numThrows = 0;
		CatchPokemonResponse response = null;
		do {

			if (razberries < razberriesLimit || razberriesLimit == -1) {
				useItem(ItemId.ITEM_RAZZ_BERRY, mandatoryParams);
				razberries++;
			}

			CatchPokemonMessage reqMsg = CatchPokemonMessage.newBuilder().setEncounterId(getEncounterId())
					.setHitPokemon(true).setNormalizedHitPosition(normalizedHitPosition)
					.setNormalizedReticleSize(normalizedReticleSize).setSpawnPointId(getSpawnPointId())
					.setSpinModifier(spinModifier).setPokeball(type.getBallType()).build();
			ServerRequest serverRequest = new ServerRequest(RequestType.CATCH_POKEMON, reqMsg);
			RequestHandlers.myRequestHandler(mandatoryParams).sendServerRequests(serverRequest);

			response = CatchPokemonResponse.parseFrom(serverRequest.getData());

			if (response.getStatus() != CatchPokemonResponse.CatchStatus.CATCH_ESCAPE
					&& response.getStatus() != CatchPokemonResponse.CatchStatus.CATCH_MISSED) {
				break;
			}
			numThrows++;
		} while (amount < 0 || numThrows < amount);

		return new CatchResult(response);
	}

	/**
	 * Tries to use an item on a catchable pokemon (ie razzberry).
	 *
	 * @param item
	 *            the item ID
	 * @return CatchItemResult info about the new modifiers about the pokemon
	 *         (can move, item capture multi) eg
	 * @throws InvalidProtocolBufferException 
	 * @throws Exception 
	 * @throws LoginFailedException
	 *             if failed to login
	 * @throws RemoteServerException
	 *             if the server failed to respond
	 */
	public CatchItemResult useItem(ItemId item, RequestMandatoryParams mandatoryParams) throws InvalidProtocolBufferException  {

		UseItemCaptureMessage reqMsg = UseItemCaptureMessage.newBuilder().setEncounterId(this.getEncounterId())
				.setSpawnPointId(this.getSpawnPointId()).setItemId(item).build();

		ServerRequest serverRequest = new ServerRequest(RequestType.USE_ITEM_CAPTURE, reqMsg);
		RequestHandlers.myRequestHandler(mandatoryParams).sendServerRequests(serverRequest);
		UseItemCaptureResponse response = null;
		response = UseItemCaptureResponse.parseFrom(serverRequest.getData());
		return new CatchItemResult(response);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CatchablePokemon other = (CatchablePokemon) obj;
		if (encounterId != other.encounterId)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (encounterId ^ (encounterId >>> 32));
		return result;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getExpirationTimestamp() {
		return expirationTimestamp;
	}

	public void setExpirationTimestamp(long expirationTimestamp) {
		this.expirationTimestamp = expirationTimestamp;
	}

	public long getEncounterId() {
		return encounterId;
	}

	public String getSpawnPointId() {
		return spawnPointId;
	}

}
