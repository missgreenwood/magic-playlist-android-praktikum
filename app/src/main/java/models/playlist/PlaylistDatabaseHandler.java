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
 *
 */
public class PlaylistDatabaseHandler extends SQLiteOpenHelper implements PlaylistHandler {

    public static final int DATABASE_VERSION = 3;
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

        String query = "SELECT p." + pId + ", p." + pName + ", p." + pGenre + ", p." + pLikes + ", p." + pAlreadyLiked + ", p." + pAlreadyUploaded +
                ", s." + sId + ", s." + sName + ", s." + sArtist + ", s." + sMediaType + ", s." + sUrl + ", s." + sLength +
                " FROM "       + Contracts.Playlists.TABLE_NAME         + " p" +
                " LEFT JOIN " + Contracts.PlaylistSongLinks.TABLE_NAME + " l ON p." + pId + "=l." + lPId +
                " LEFT OUTER JOIN " + Contracts.Songs.TABLE_NAME + " s ON l." + lSId + "=s." + sId + " ORDER BY " + sortOrder;

        Cursor c = getReadableDatabase().rawQuery(
                query,
                new String[] {}
        );

        ArrayList<Playlist> playlists = getPlaylistsOfCursor(c);

        if (c != null) {
            c.close();
        }


        return playlists;
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

    public boolean destroyPlaylist(Playlist playlist) {

        String[] selectionArgs = {String.valueOf(playlist.getId())};
        boolean success = false;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int affectedRowsCount = getWritableDatabase().delete(
                    Contracts.Playlists.TABLE_NAME,
                    Contracts.Playlists._ID + "=?",
                    selectionArgs
            );

            removeUnusedLinks();
            removeUnusedSongs();
            success = affectedRowsCount > 0;
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return success;
    }

    public Song getSong(String artist, String songname) {
        String selection = Contracts.Songs.COLUMN_NAME_ARTIST+"=? and " + Contracts.Songs.COLUMN_NAME_NAME+"=?";
        String[] selectionArgs = {artist, songname};
        return getSongWithSelection(selection, selectionArgs);
    }

    public Song getSong(int id) {
        String selection = Contracts.Songs._ID+"=?";
        String[] selectionArgs = {String.valueOf(id)};
        return getSongWithSelection(selection, selectionArgs);
    }





    private ArrayList<Playlist> getPlaylistsOfCursor(Cursor c) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        if(c != null)
        {
            int count = c.getCount();

            if(count > 0)
            {
                Playlist playlist = null;
                while(c.moveToNext())
                {
                    String  pName = c.getString(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_NAME)),
                            pGenre = c.getString(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_GENRE));
                    int     pLikes = c.getInt(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_LIKES)),
                            pAlreadyLiked = c.getInt(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_ALREADY_LIKED)),
                            pAlreadyUploaded = c.getInt(c.getColumnIndex(Contracts.Playlists.COLUMN_NAME_ALREADY_UPLOADED)),
                            pId = c.getInt(c.getColumnIndex(Contracts.Playlists._ID)),

                            sId = c.getInt(c.getColumnIndex(Contracts.Songs._ID)),
                            sLength = c.getInt(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_LENGTH));
                    String  sName = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_NAME)),
                            sArtist = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_ARTIST)),
                            sMediaType = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE)),
                            sUrl = c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_URL));



                    if (playlist == null || playlist.getId() != pId) {
                        playlist = new Playlist(pName);
                        playlist.setId(pId);
                        playlist.setGenre(pGenre);
                        playlist.setLikes(pLikes);
                        playlist.setAlreadyLiked(pAlreadyLiked == 1);
                        playlist.setAlreadyUploaded(pAlreadyUploaded == 1);
                        playlists.add(playlist);
                    }
                    Song song = Song.Builder.getSongDb(sId, sArtist, sName, sMediaType, sUrl, sLength);
                    if (song != null) {
                        playlist.addSong(song);
                    }
                }
            }
        }
        return playlists;
    }

    private boolean createNewPlaylist(Playlist playlist) {
        SQLiteDatabase db = getWritableDatabase();
        boolean success = false;
        db.beginTransaction();
        try {
            long rowId = db.insert(
                    Contracts.Playlists.TABLE_NAME,
                    Contracts.Playlists.COLUMN_NAME_GENRE,
                    getPlaylistValues(playlist)
            );

            success = rowId != -1;
            playlist.setId(getPlaylistId(playlist));
            for (Song song : playlist.getSongsList()) {
                if (!saveSong(song, playlist)) {
                    success = false;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return success;
    }

    private boolean saveSong(Song song, Playlist playlist) {
        boolean success = true;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            if (!updateSong(song)) {
                success = createNewSong(song);
            }
            success = linkSongWithPlaylist(song, playlist) && success;
            if (success && song.getId() == -1) {
                song.setSongID(getSongId(song.getArtist(), song.getSongname()));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return success;
    }

    private boolean linkSongWithPlaylist(Song song, Playlist playlist) {
        if (!linkExists(song, playlist)) {
            long rowId = getWritableDatabase().insert(
                    Contracts.PlaylistSongLinks.TABLE_NAME,
                    null,
                    getLinkValues(song, playlist)
            );
            return rowId != -1;
        } else {
            return true;
        }
    }

    private boolean updatePlaylist(Playlist playlist) {
        String[] selectionArgs = {String.valueOf(playlist.getId())};
        boolean success = false;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int affectedRowsCount = db.update(
                    Contracts.Playlists.TABLE_NAME,
                    getPlaylistValues(playlist),
                    Contracts.Playlists._ID + "=?",
                    selectionArgs
            );
            success = affectedRowsCount > 0;
            if (success) {
                int deleted = db.delete(
                        Contracts.PlaylistSongLinks.TABLE_NAME,
                        Contracts.PlaylistSongLinks.COLUMN_NAME_PLAYLIST_ID + "=?",
                        selectionArgs
                );

                Log.d("PlaylistDatabaseHandler", "Playlist deleted songs amount=" + deleted);
                for (Song song : playlist.getSongsList()) {
                    if (!saveSong(song, playlist)) {
                        success = false;
                    }
                }
                removeUnusedSongs();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Log.d("PlaylistDatabaseHandler", "Playlist update success=" + success);
        return success;
    }


    // single operation functions


    private boolean createNewSong(Song song) {
        long rowId = getWritableDatabase().insert(
                Contracts.Songs.TABLE_NAME,
                Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE,
                getSongValues(song)
        );
        return rowId != -1;
    }

    private boolean updateSong(Song song) {
        String[] selectionArgs = {String.valueOf(song.getId())};
        int affectedRowsCount = getWritableDatabase().update(
                Contracts.Songs.TABLE_NAME,
                getSongValues(song),
                Contracts.Songs._ID + "=?",
                selectionArgs
        );
        return affectedRowsCount > 0;
    }

    //getter

    private int getPlaylistId(Playlist playlist) {
        if (playlist.getId() != -1) {
            return playlist.getId();
        }
        Cursor c = getReadableDatabase().query(
                Contracts.Playlists.TABLE_NAME,
                new String[]{Contracts.Playlists._ID},
                Contracts.Playlists.COLUMN_NAME_NAME + "=?",
                new String[]{playlist.getName()},
                null, null, null);

        int id = -1;

        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                id = c.getInt(c.getColumnIndex(Contracts.Playlists._ID));
            }
            c.close();
        }
        return id;
    }

    private int getSongId(String artist, String songname) {
        Cursor c = getReadableDatabase().query(
                Contracts.Songs.TABLE_NAME,
                new String[] {Contracts.Songs._ID},
                Contracts.Songs.COLUMN_NAME_ARTIST+"=? and " + Contracts.Songs.COLUMN_NAME_NAME+"=?",
                new String[] {artist, songname},
                null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return Integer.parseInt(c.getString(c.getColumnIndex(Contracts.Songs._ID)));
        } else {
            return -1;
        }
    }

    private Song getSongWithSelection(String selection, String[] selectionArgs) {
        String[] projection = {
                Contracts.Songs._ID,
                Contracts.Songs.COLUMN_NAME_ARTIST,
                Contracts.Songs.COLUMN_NAME_NAME,
                Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE,
                Contracts.Songs.COLUMN_NAME_URL,
                Contracts.Songs.COLUMN_NAME_LENGTH
        };

        Cursor c = getReadableDatabase().query(
                Contracts.Songs.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null, null);

        Song song = null;
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                song = Song.Builder.getSongDb(
                        c.getInt(c.getColumnIndex(Contracts.Songs._ID)),
                        c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_ARTIST)),
                        c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_NAME)),
                        c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_MEDIA_WRAPPER_TYPE)),
                        c.getString(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_URL)),
                        c.getInt(c.getColumnIndex(Contracts.Songs.COLUMN_NAME_LENGTH)));
            }
            c.close();
        }
        return song;
    }

    private boolean linkExists(Song song, Playlist playlist) {
        if (song.getId() == -1) {
            song.setSongID(getSongId(song.getArtist(), song.getSongname()));
        }
        if (playlist.getId() == -1) {
            playlist.setId(getPlaylistId(playlist));
        }
        Cursor c = getReadableDatabase().query(
                Contracts.PlaylistSongLinks.TABLE_NAME,
                new String[] {Contracts.PlaylistSongLinks._ID},
                Contracts.PlaylistSongLinks.COLUMN_NAME_SONG_ID+"=? and " + Contracts.PlaylistSongLinks.COLUMN_NAME_PLAYLIST_ID+"=?",
                new String[] {String.valueOf(song.getId()), String.valueOf(playlist.getId())},
                null, null, null);
        boolean exists = false;
        if (c != null) {
            exists = c.getCount() > 0;
            c.close();
        }
        return exists;
    }

    // garbage collection

    private void removeUnusedLinks() {
        int removedLinks = getWritableDatabase().delete(
                Contracts.PlaylistSongLinks.TABLE_NAME,
                Contracts.PlaylistSongLinks.COLUMN_NAME_PLAYLIST_ID + " NOT IN (SELECT " +
                        Contracts.Playlists._ID +
                        " FROM " + Contracts.Playlists.TABLE_NAME +
                        ") OR " +
                        Contracts.PlaylistSongLinks.COLUMN_NAME_SONG_ID + " NOT IN (SELECT " +
                        Contracts.Songs._ID +
                        " FROM " + Contracts.Songs.TABLE_NAME + ")",
                new String[0]
        );
        Log.d("PlaylistDatabaseHandler", "removed unused Links: " + removedLinks);
    }

    private void removeUnusedSongs() {
        int removedSongs = getWritableDatabase().delete(
                Contracts.Songs.TABLE_NAME,
                Contracts.Songs._ID + " NOT IN (SELECT " +
                        Contracts.PlaylistSongLinks.COLUMN_NAME_SONG_ID +
                        " FROM " + Contracts.PlaylistSongLinks.TABLE_NAME +
                        ")",
                new String[0]
        );
        Log.d("PlaylistDatabaseHandler", " removed " + removedSongs + " unused songs!");
    }


    // values

    private ContentValues getLinkValues(Song song, Playlist playlist) {
        ContentValues values = new ContentValues();
        values.put(Contracts.PlaylistSongLinks.COLUMN_NAME_PLAYLIST_ID, playlist.getId());
        values.put(Contracts.PlaylistSongLinks.COLUMN_NAME_SONG_ID, song.getId());
        return values;
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
        Log.d("PlaylistDatabaseHandler", "drop tables!");
        db.execSQL(Contracts.getDropEntrySQL(Contracts.PlaylistSongLinks.TABLE_NAME));
        db.execSQL(Contracts.getDropEntrySQL(Contracts.Songs.TABLE_NAME));
        db.execSQL(Contracts.getDropEntrySQL(Contracts.Playlists.TABLE_NAME));
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("PlaylistDatabaseHandler", "drop tables!");
        db.execSQL(Contracts.getDropEntrySQL(Contracts.PlaylistSongLinks.TABLE_NAME));
        db.execSQL(Contracts.getDropEntrySQL(Contracts.Songs.TABLE_NAME));
        db.execSQL(Contracts.getDropEntrySQL(Contracts.Playlists.TABLE_NAME));
        onCreate(db);
    }

}
