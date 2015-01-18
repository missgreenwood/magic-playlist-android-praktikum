package models.playlist.database;

import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * Created by TheDaAndy on 14.01.2015.
 */
public class Contracts {

    private Contracts() {}

    private static final String VARCHAR_TYPE = " VARCHAR(255)";
    private static final String INT_TYPE = " INTEGER";
    private static final String UNIQUE = " UNIQUE";
    private static final String NOTNULL = " NOT NULL";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String COMMA_SEP = ",";

    public static class Playlists implements BaseColumns {
        public static final String TABLE_NAME = "playlists";
        public static final String _ID = "playlist_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LIKES = "likes";
        public static final String COLUMN_NAME_GENRE = "genre";
        public static final String COLUMN_NAME_ALREADY_LIKED = "already_liked";
        public static final String COLUMN_NAME_ALREADY_UPLOADED = "already_uploaded";


        public static final String ENTRY_VALUES =
            _ID + INT_TYPE + " PRIMARY KEY" + AUTOINCREMENT + COMMA_SEP +
            COLUMN_NAME_NAME                + VARCHAR_TYPE  + UNIQUE + NOTNULL + COMMA_SEP +
            COLUMN_NAME_GENRE               + VARCHAR_TYPE  + COMMA_SEP +
            COLUMN_NAME_LIKES               + INT_TYPE      + COMMA_SEP +
            COLUMN_NAME_ALREADY_LIKED       + INT_TYPE      + COMMA_SEP +
            COLUMN_NAME_ALREADY_UPLOADED    + INT_TYPE;
    }

    public static class Songs implements BaseColumns {
        public static final String TABLE_NAME = "songs";
        public static final String _ID = "song_id";
        public static final String COLUMN_NAME_NAME = "songname";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_MEDIA_WRAPPER_TYPE = "media_wrapper_type";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_LENGTH = "length";

        public static final String ENTRY_VALUES =
                _ID + INT_TYPE + " PRIMARY KEY" + AUTOINCREMENT + COMMA_SEP +
                COLUMN_NAME_NAME                + VARCHAR_TYPE  + NOTNULL + COMMA_SEP +
                COLUMN_NAME_ARTIST              + VARCHAR_TYPE  + NOTNULL + COMMA_SEP +
                COLUMN_NAME_MEDIA_WRAPPER_TYPE  + VARCHAR_TYPE  + COMMA_SEP +
                COLUMN_NAME_URL                 + VARCHAR_TYPE  + COMMA_SEP +
                COLUMN_NAME_LENGTH              + INT_TYPE      + COMMA_SEP +
                "UNIQUE(" + COLUMN_NAME_NAME + COMMA_SEP + COLUMN_NAME_ARTIST + ") ON CONFLICT REPLACE";
    }

    public static class PlaylistSongLinks implements BaseColumns {
        public static final String TABLE_NAME = "playlist_song_links";
        public static final String _ID = "linkId";
        public static final String COLUMN_NAME_PLAYLIST_ID = "p_id";
        public static final String COLUMN_NAME_SONG_ID = "s_id";

        public static final String ENTRY_VALUES =
                _ID + INT_TYPE + " PRIMARY KEY" + AUTOINCREMENT + COMMA_SEP +
                COLUMN_NAME_PLAYLIST_ID + INT_TYPE + NOTNULL + COMMA_SEP +
                COLUMN_NAME_SONG_ID     + INT_TYPE + NOTNULL + COMMA_SEP +
                "FOREIGN KEY(" + COLUMN_NAME_PLAYLIST_ID + ") REFERENCES " + Playlists.TABLE_NAME + "(" + Playlists._ID + ")" + COMMA_SEP +
                "FOREIGN KEY(" + COLUMN_NAME_SONG_ID     + ") REFERENCES " + Songs.TABLE_NAME     + "(" + Songs._ID + ")" + COMMA_SEP +
                "UNIQUE(" + COLUMN_NAME_PLAYLIST_ID + ", " + COLUMN_NAME_SONG_ID + ")";
    }

    public static String getDropEntrySQL(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }
    public static String getCreateEntrySQL(String tableName, String values) {
        return "CREATE TABLE " + tableName + " (" + values + ")";
    }

}
