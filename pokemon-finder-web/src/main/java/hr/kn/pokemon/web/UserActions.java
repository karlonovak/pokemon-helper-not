package hr.kn.pokemon.web;

public enum UserActions {
	CATCH("CATCH");
	
    public final String name;       

    private UserActions(String s) {
        name = s;
    }
}
