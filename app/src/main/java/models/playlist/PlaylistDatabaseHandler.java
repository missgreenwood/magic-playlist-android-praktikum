package models.playlist;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.playlist.database.Contracts;

/**
 * Created by TheDaAndy on 14.01.2015.
 */
public class PlaylistDatabaseHandler extends SQLiteOpenHelper implements PlaylistHandler {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "magic-playlist.db";

    public PlaylistDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<Playlist> loadPlaylists()
    {
        String pName = Contracts.Playlists.COLUMN_NAME_NAME,
               pGenre = Contracts.Playlists.COLUMN_NAME_GENRE,
               pLikes = Contracts.Playlists.COLUMN_NAME_LIKES,
               pAlreadyLiked = Contracts.Playlists.COLUMN_NAME_ALREADY_LIKED,
               pAlreadyUploaded = Contracts.Playlists.COLUMN_NAME_ALREADY_UPLOADED,
               pId = Contracts.Playlists._ID,

               lPId = Contracts.PlaylistSongLinks.COLUMN_NAME_PLAYLIST_ID,
               lSId = Contracts.PlaylistSongLinks.COLUMN_NAME_SONG_ID,

               sId = Contracts.Songs._ID,
               sName = Contracts.Songs.COLUMN_NAME_NAME,
               sArtist = Contracts.Songs.COLUMN_NAME_ARTIST,
               sMediaType = Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE,
               sUrl = Contracts.Songs.COLUMN_NAME_URL,
               sLength = Contracts.Songs.COLUMN_NAME_LENGTH;

        String sortOrder = pName + " ASC";

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT p." + pName + ", p." + pGenre + ", p." + pLikes + ", p." + pAlreadyLiked + ", p." + pAlreadyUploaded +
                      " s." + sId + ", s." + sName + ", s." + sArtist + ", s." + sMediaType + ", s." + sUrl + ", s." + sLength +
                      " FROM "       + Contracts.Playlists.TABLE_NAME         + " p" +
                      " INNER JOIN " + Contracts.PlaylistSongLinks.TABLE_NAME + " l ON p." + pId  + "=l." + lPId +
                      " INNER JOIN " + Contracts.Songs.TABLE_NAME             + " s ON l." + lSId + "=s." + sId  +
                " ORDER BY " + sortOrder,
                new String[] {}
        );

        getPlaylistsOfCursor(c);

        c.close();

        ArrayList<Playlist> playlists = new ArrayList<>();

        return playlists;
    }

    private ArrayList<Playlist> getPlaylistsOfCursor(Cursor c) {
        int count;
        if(c != null)
        {
            count = c.getCount();

            if(count > 0)
            {
                while(c.moveToNext())
                {
                    String  pName = c.getString(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_NAME)),
                            pGenre = c.getString(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_GENRE)),
                            pLikes = c.getString(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_LIKES)),
                            pAlreadyLiked = c.getString(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_ALREADY_LIKED)),
                            pAlreadyUploaded = c.getString(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_ALREADY_UPLOADED)),
                            pId = c.getString(c.getColumnIndex(Contracts.Playlists._ID)),

                            sId = c.getString(c.getColumnIndex(Contracts.Songs._ID)),
                            sName = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_NAME)),
                            sArtist = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_ARTIST)),
                            sMediaType = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE)),
                            sUrl = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_URL)),
                            sLength = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_LENGTH));
                    ArrayList<String> s = new ArrayList<>();
                    s.add(pName);
                    s.add(pGenre);
                    s.add(pLikes);
                    s.add(pAlreadyLiked);
                    s.add(pAlreadyUploaded);
                    s.add(pId);

                    s.add(sId);
                    s.add(sName);
                    s.add(sArtist);
                    s.add(sMediaType);
                    s.add(sUrl);
                    s.add(sLength);
                    Log.d("PlaylistDatabaseHandler", "values: " + s);
                }
            }
        }
        return null;
    }

    public boolean changePlaylistName (String before, String after) {
        ContentValues values = new ContentValues();
        values.put(Contracts.Playlists.COLUMN_NAME_NAME, after);

        String selection = Contracts.Playlists.COLUMN_NAME_NAME + "=?";
        String[] selectionArgs = {before};

        int affectedRowsCount = getWritableDatabase().update(
                Contracts.Playlists.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        return affectedRowsCount > 0;
    }

    public boolean savePlaylist(Playlist playlist) {
        if (!updatePlaylist(playlist)) {
            return createNewPlaylist(playlist);
        } else {
            return true;
        }
    }

    private boolean createNewPlaylist(Playlist playlist) {
        long rowId = getWritableDatabase().insert(
                Contracts.Playlists.TABLE_NAME,
                Contracts.Playlists.COLUMN_NAME_GENRE,
                getPlaylistValues(playlist)
        );
        boolean success = rowId != -1;
        for (Song song : playlist.getSongsList()) {
            if (!saveSong(song)) {
                success = false;
            }
        }
        return success;
    }

    private boolean saveSong(Song song) {
        boolean success = true;
        if (!updateSong(song)) {
            success = createNewSong(song);
        }
        if (success) {
            song.setSongID(getSongId(song));
        }
        return success;
    }

    private boolean createNewSong(Song song) {
        long rowId = getWritableDatabase().insert(
                Contracts.Songs.TABLE_NAME,
                Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE,
                getSongValues(song)
        );
        return rowId != -1;
    }

    private boolean updatePlaylist(Playlist playlist) {
        String[] selectionArgs = {playlist.getName()};
        int affectedRowsCount = getWritableDatabase().update(
                Contracts.Playlists.TABLE_NAME,
                getPlaylistValues(playlist),
                Contracts.Playlists.COLUMN_NAME_NAME + "=?",
                selectionArgs
        );
        return affectedRowsCount > 0;
    }

    private boolean updateSong(Song song) {
        String[] selectionArgs = {String.valueOf(song.getSongID())};
        int affectedRowsCount = getWritableDatabase().update(
                Contracts.Songs.TABLE_NAME,
                getSongValues(song),
                Contracts.Songs._ID + "=?",
                selectionArgs
        );
        return affectedRowsCount > 0;
    }

    private int getSongId(Song song) {
        if (song.getSongID() != -1) {
            return song.getSongID();
        }
        Cursor c = getReadableDatabase().query(
                Contracts.Songs.TABLE_NAME,
                new String[] {Contracts.Songs._ID},
                Contracts.Songs.COLUMN_NAME_ARTIST+"=? and " + Contracts.Songs.COLUMN_NAME_NAME+"=?",
                new String[] {song.getArtist(), song.getSongname()},
                null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return Integer.parseInt(c.getString(c.getColumnIndex(Contracts.Songs._ID)));
        } else {
            return -1;
        }
    }

    private ContentValues getPlaylistValues(Playlist playlist) {
        ContentValues values = new ContentValues();
        values.put(Contracts.Playlists.COLUMN_NAME_NAME, playlist.getName());
        values.put(Contracts.Playlists.COLUMN_NAME_GENRE, playlist.getGenre());
        values.put(Contracts.Playlists.COLUMN_NAME_LIKES, playlist.getLikes());
        values.put(Contracts.Playlists.COLUMN_NAME_ALREADY_LIKED, playlist.isAlreadyLiked());
        values.put(Contracts.Playlists.COLUMN_NAME_ALREADY_UPLOADED, playlist.isAlreadyUploaded());
        return values;
    }

    private ContentValues getSongValues(Song song) {
        ContentValues values = new ContentValues();
        values.put(Contracts.Songs.COLUMN_NAME_NAME, song.getSongname());
        values.put(Contracts.Songs.COLUMN_NAME_ARTIST, song.getArtist());
        values.put(Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE, song.getMediaWrapperType());
        values.put(Contracts.Songs.COLUMN_NAME_URL, song.getSongUrl());
        values.put(Contracts.Songs.COLUMN_NAME_LENGTH, song.getLength());
        return values;
    }


    public boolean destroy(String name) {
        String[] selectionArgs = {name};
        int affectedRowsCount = getWritableDatabase().delete(
                Contracts.Playlists.TABLE_NAME,
                Contracts.Playlists.COLUMN_NAME_NAME + "=?",
                selectionArgs
        );
        return affectedRowsCount > 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contracts.getCreateEntrySQL(
                        Contracts.Playlists.TABLE_NAME,
                        Contracts.Playlists.ENTRY_VALUES)
        );
        db.execSQL(Contracts.getCreateEntrySQL(
                        Contracts.Songs.TABLE_NAME,
                        Contracts.Songs.ENTRY_VALUES)
        );
        db.execSQL(Contracts.getCreateEntrySQL(
                        Contracts.PlaylistSongLinks.TABLE_NAME,
                        Contracts.PlaylistSongLinks.ENTRY_VALUES)
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contracts.getDropEntrySQL(Contracts.PlaylistSongLinks.TABLE_NAME));
        db.execSQL(Contracts.getDropEntrySQL(Contracts.Songs.TABLE_NAME));
        db.execSQL(Contracts.getDropEntrySQL(Contracts.Playlists.TABLE_NAME));
        onCreate(db);
    }
}
