package rest.client;

public class ClientConstants {

//    public final static String BASE_URL = "http://141.84.213.249:5050/playlist";
    public final static String BASE_URL = "http://192.168.2.113:5050/playlist";

    public final static String ADD_PLAYLIST_URL = BASE_URL + "/add";

    public final static String FIND_PLAYLIST_URL = BASE_URL + "/find/name?name=%s";

    public final static String FIND_PLAYLISTS_URL = BASE_URL + "/find?";

    public final static String GENRE_PARAM = "genre=%s";

    public final static String ARTIST_PARAM = "artist=%s";

    public final static String LIKE_PLAYLIST_URL = BASE_URL + "/like";

    public final static String CLEAN_DB = BASE_URL + "/clean";
}
