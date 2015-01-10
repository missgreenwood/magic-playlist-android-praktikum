package rest.client;

public class ClientConstants {

    public final static String BASE_URL = "http://192.168.178.20:5050/playlist";

    public final static String ADD_PLAYLIST_URL = BASE_URL + "/add";

    public final static String FIND_PLAYLIST_URL = BASE_URL + "/find/name?name=%s";

    public final static String FIND_PLAYLISTS_URL = BASE_URL + "/find/genre?genre=%s&artist=%s";

    public final static String CLEAN_DB = BASE_URL + "/clean";
}
