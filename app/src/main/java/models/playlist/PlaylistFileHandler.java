package models.playlist;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import models.mediaModels.Playlist;
import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 30.12.2014.
 */
public class PlaylistFileHandler {

    private static final String PLAYLISTS_FILE_TYPE = ".pls";

    public static ArrayList<Playlist> loadPlaylists()
    {
        File dir = new File(PlaylistFileHandler.getFilePath(null));
        ArrayList<Playlist> playlists = new ArrayList<>();

        File[] playlistFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches(".+\\.pls");
            }
        });

        for (File playlistFile : playlistFiles) {
            String playlistName = playlistFile.getName().replace(".pls", "");
            Playlist playlist = PlaylistFileHandler.loadPlaylist(playlistName);
            playlists.add(playlist);
        }

        return playlists;
    }

    private static Playlist loadPlaylist(String name) {
        Playlist newPlaylist = new Playlist(name);

        BufferedReader reader = null;

        try {
            Log.d("read Playlist", "start");
            reader = new BufferedReader(new FileReader(getFilePath(name)));
            Log.d("read Playlist", "reader init");

            String line;

            HashMap<Integer, Song> songs = new HashMap<>();
            Log.d("read Playlist", "start reading...");
            while ((line = reader.readLine()) != null) {
                int i = line.indexOf('=');
                int songNumber;

                if (i == -1) {
                    continue;
                }

                try {
                    songNumber = Integer.parseInt(line.substring(i-1,i));
                } catch(Exception e) {
                    continue;
                }

                String label = line.substring(0, i-1).toLowerCase(),
                       value = line.substring(i + 1);
                Log.d("read Playlist", "label: " + label + " value: " + value);

                Song song;
                if (!songs.containsKey(songNumber)) {
                    song = new Song();
                    songs.put(songNumber, song);
                    Log.d("read Playlist", "new song #" + songNumber);
                } else {
                    song = songs.get(songNumber);
                    Log.d("read Playlist", "found song #" + songNumber);
                }

                switch (label) {
                    case "file":
                        Log.d("read Playlist", "set url " + value);
                        song.setSongUrl(value);
                        break;
                    case "title":
                        Log.d("read Playlist", "set title"  + value);
                        String[] valueParts = value.split("-", 2);
                        if (valueParts.length == 2) {
                            song.setArtist(valueParts[0].trim());
                            song.setSongname(valueParts[1].trim());
                            Log.d("read Playlist", "artist: "  + valueParts[0].trim() + "song: " + valueParts[1].trim());
                        } else {
                            song.setArtist("Unknown");
                            song.setSongname(value);
                            Log.d("read Playlist", "artist: Unknown song: " + value);
                        }
                        break;
                    case "length":
                        song.setLength(Integer.parseInt(value));
                        Log.d("read Playlist", "set length " + value);
                        break;
                }
            }
            Log.d("read Playlist", "finished reading file");
            //TODO: check time effort for sorting...
            SortedSet<Integer> keys = new TreeSet<>(songs.keySet());
            for (int key : keys) {
                Log.d("read Playlist", "sorted: " + songs.get(key).toString());
                newPlaylist.addSong(songs.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return newPlaylist;
    }

    public static boolean changePlaylistName (String before, String after) {
        File playlistFile = new File(getFilePath(before));
        return playlistFile.renameTo(new File(getFilePath(after)));
    }

    public static boolean savePlaylist(Playlist playlist) {
        ArrayList<Song> songs = playlist.getSongsList();
        OutputStreamWriter writer = null;
        boolean success = false;

        try {
            writer = new OutputStreamWriter(new FileOutputStream(getFilePath(playlist.getName()), false));

            String playlistEntry = "[playlist]\nNumberOfEntries=" + songs.size() + "\n";
            writer.write(playlistEntry, 0, playlistEntry.length());
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                String songEntry = "File"   + i + "=" + song.getSongUrl() + "\n"
                                 + "Title"  + i + "=" + song.getArtist() + " - " + song.getSongname() + "\n"
                                 + "Length" + i + "=" + song.getLength() + "\n";
                writer.write(songEntry, 0, songEntry.length());
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    private static String getFilePath(String name)
    {
        String directoryPath;

        if (isExternalStorageWritable()) {
            String externalPath = Environment.getExternalStorageDirectory().getPath();
            directoryPath =  externalPath + "/Playlists/MagicPlaylists/";
            new File (directoryPath).mkdirs();
            if (name == null) {
                return directoryPath;
            }
        } else {
            Log.e("ERROR", "external storage is needed!");
            return null;
        }
        return directoryPath + name + PLAYLISTS_FILE_TYPE;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
