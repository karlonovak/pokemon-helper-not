package hr.kn.pokemon.finder;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PokeVisionPokemon {
	private int pokemonId;
	private float latitude;
	private float longitude;
	
	@JsonProperty("expiration_time")
	private long expires;

	public PokeVisionPokemon(){}

	@JsonProperty("is_alive")
	private Boolean isAlive;

	public int getPokemonId() {
		return pokemonId;
	}

	public void setPokemonId(int pokemonId) {
		this.pokemonId = pokemonId;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public Boolean getIsAlive() {
		return isAlive;
	}

	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}

	public long getExpires() {
		return expires;
	}
	
	public void setExpires(long expires) {
		this.expires = expires;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isAlive == null) ? 0 : isAlive.hashCode());
		result = prime * result + Float.floatToIntBits(latitude);
		result = prime * result + Float.floatToIntBits(longitude);
		result = prime * result + pokemonId;
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokeVisionPokemon other = (PokeVisionPokemon) obj;
		if (pokemonId != other.getPokemonId())
			return false;
		
		return true;
	}


}
