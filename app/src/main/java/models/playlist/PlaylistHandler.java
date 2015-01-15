package models.playlist;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.mediaModels.Playlist;
import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 30.12.2014.
 */
public interface PlaylistHandler {

    ArrayList<Playlist> loadPlaylists();

    boolean changePlaylistName (String before, String after);

    boolean savePlaylist(Playlist playlist);

    boolean destroyPlaylist(Playlist playlist);
}
