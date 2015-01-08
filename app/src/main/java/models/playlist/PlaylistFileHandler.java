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
                return filename.matches(".+(\\.pls|\\.m3u)$");
            }
        });

        for (File playlistFile : playlistFiles) {
            String playlistName = playlistFile.getName();
            Playlist playlist;
            if (playlistName.matches(".+\\.pls$")) {
                playlist = loadPlaylist(playlistName.replaceAll("\\.pls$", ""), "pls");
            } else if (playlistName.matches(".+\\.m3u$")) {
                playlist = loadPlaylist(playlistName.replaceAll("\\.m3u$", ""), "m3u");
            } else {
                continue;
            }
            playlists.add(playlist);
        }

        return playlists;
    }

    private static Playlist loadPlaylist(String name, String type) {
        switch (type) {
            case "pls":
                return loadPlaylistPls(name);
            case "m3u":
                return loadPlaylistM3U(name);
        }
        return null;
    }

    private static Playlist loadPlaylistM3U(String name) {
        return loadPlaylistPls(name);
    }

    private static Playlist loadPlaylistPls(String name) {
        Playlist newPlaylist = new Playlist(name);

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(getFilePath(name)));

            String line;

            HashMap<Integer, Song> songs = new HashMap<>();
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

                Song song;
                if (!songs.containsKey(songNumber)) {
                    song = new Song();
                    songs.put(songNumber, song);
                } else {
                    song = songs.get(songNumber);
                }

                switch (label) {
                    case "file":
                        song.setSongUrl(value);
                        break;
                    case "title":
                        String[] valueParts = value.split("-", 2);
                        if (valueParts.length == 2) {
                            song.setArtist(valueParts[0].trim());
                            song.setSongname(valueParts[1].trim());
                        } else {
                            song.setArtist("Unknown");
                            song.setSongname(value);
                        }
                        break;
                    case "length":
                        song.setLength(Integer.parseInt(value));
                        break;
                }
            }
            //TODO: check time effort for sorting...
            SortedSet<Integer> keys = new TreeSet<>(songs.keySet());
            for (int key : keys) {
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

    public static boolean destroy(String name) {
        return new File(getFilePath(name)).delete();
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
