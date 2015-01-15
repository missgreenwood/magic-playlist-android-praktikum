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
 *
 */
public class PlaylistFileHandler implements PlaylistHandler{

    private final String PLAYLISTS_FILE_TYPE = ".pls";

    public ArrayList<Playlist> loadPlaylists()
    {
        File dir = new File(getFilePath(null));
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

    public boolean changePlaylistName (String before, String after) {
        File playlistFile = new File(getFilePath(before));
        return playlistFile.renameTo(new File(getFilePath(after)));
    }

    public boolean savePlaylist(Playlist playlist) {
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

    public boolean destroyPlaylist(Playlist playlist) {
        return new File(getFilePath(playlist.getName())).delete();
    }





    private Playlist loadPlaylist(String name, String type) {
        switch (type) {
            case "pls":
                return loadPlaylistPls(name);
            case "m3u":
                return loadPlaylistM3U(name);
        }
        return null;
    }

    private Playlist loadPlaylistM3U(String name) {
        return loadPlaylistPls(name);
    }

    private Playlist loadPlaylistPls(String name) {
        Playlist newPlaylist = new Playlist(name);

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(getFilePath(name)));

            String line;

            HashMap<Integer, SongInfo> songInfos = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                int songNumber;
                Matcher matcher = Pattern.compile("^(file|title|length)(\\d+)=(.*)$", Pattern.CASE_INSENSITIVE).matcher(line);
                if (!matcher.matches()) {
                    continue;
                }
                String label = matcher.group(1).toLowerCase(),
                        value = matcher.group(3);
                songNumber = Integer.parseInt(matcher.group(2));
                SongInfo songInfo = songInfos.get(songNumber);
                if (songInfo == null) {
                    songInfo = new SongInfo();
                    songInfos.put(songNumber, songInfo);
                }

                switch (label) {
                    case "file":
                        songInfo.setUrl(value);
                        break;
                    case "title":
                        String[] valueParts = value.split("-", 2);
                        if (valueParts.length == 2) {
                            songInfo.setArtist(valueParts[0].trim());
                            songInfo.setSongname(valueParts[1].trim());
                        } else {
                            songInfo.setArtist("Unknown");
                            songInfo.setSongname(value);
                        }
                        break;
                    case "length":
                        songInfo.setLength(Integer.parseInt(value));
                        break;
                }

            }

            SortedSet<Integer> keys = new TreeSet<>(songInfos.keySet());
            for (int key : keys) {
                SongInfo songInfo = songInfos.get(key);
                Song song = Song.Builder.getSong(songInfo.getArtist(), songInfo.getSongname());
                song.setSongUrl(songInfo.getUrl());
                song.setLength(songInfo.getLength());
                newPlaylist.addSong(song);
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

    private String getFilePath(String name)
    {
        String directoryPath;

        if (isExternalStorageWritable()) {
            String externalPath = Environment.getExternalStorageDirectory().getPath();
            directoryPath =  externalPath + "/Playlists/MagicPlaylists/";
            File directory = new File (directoryPath);
            directory.mkdirs();
            if (name == null) {
                return directoryPath;
            }
        } else {
            Log.e("ERROR", "external storage is needed!");
            return null;
        }
        return directoryPath + name + PLAYLISTS_FILE_TYPE;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private class SongInfo {
        private String artist = "";
        private String songname = "";
        private String url = "";
        private int length = -1;


        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getSongname() {
            return songname;
        }

        public void setSongname(String songname) {
            this.songname = songname;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
