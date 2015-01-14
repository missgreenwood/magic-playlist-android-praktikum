package rest.client;

import java.util.List;

import models.mediaModels.Playlist;

/**
 * Created by TheDaAndy on 12.01.2015.
 */
public interface ClientListener {

    public interface AddPlaylistListener extends ClientListener {
        void onAddPlaylistSuccess();
        void onAddPlaylistError(boolean alreadyExists);
    }

    public interface FindSinglePlaylistListener extends ClientListener {
        void onFindSinglePlaylistSuccess(Playlist playlist);
        void onFindSinglePlaylistError();
    }

    public interface FindPlaylistsListener extends ClientListener {
        void onFindPlaylistsSuccess(List<Playlist> playlists);
        void onFindPlaylistsError();
    }

    public interface LikePlaylistListener extends ClientListener {
        void onLikePlaylistSuccess();
        void onLikePlaylistError();
    }
}
