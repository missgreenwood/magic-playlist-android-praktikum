package controllers.mainFragments.generatorFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;

import java.util.List;

import controllers.mainFragments.generatorFragments.playlistFragment.OnSwipeListener;
import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.mediaModels.PlayQueue;
import models.playlist.PlaylistsManager;
import rest.client.Client;
import rest.client.ClientListener;
import tests.R;

/**
 * created by Andreas
 */
public class PlaylistFragment extends ListFragment implements
        PlayQueue.Listener,
        AdapterView.OnItemLongClickListener,
        Playlist.Listener,
        ClientListener.AddPlaylistListener, ClientListener.FindSinglePlaylistListener {

    protected Playlist playlist;
    private View currentlyPlayingView;

    private Button uploadBtn;

    private int currentlyDraggedItemPos = -1;
    private float currentDraggedX = 0;
    private Song lastRemovedSong;
    private int lastRemovedPos;
    private TextView removedSongNotification;
    private boolean dragCanceled = false;
    private ProgressDialog loadingDialog;
    private RequestHandle uploadRequestHandle;

    public PlaylistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playlist == null) {
            playlist = PlaylistsManager.getInstance().getCurrentPlaylist();
            if (playlist == null) {
                playlist = new Playlist();
            }
        }
        setPlaylist(playlist); // sets observer and himself into the global "currentPlaylist"

        setListAdapter(new SongArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, playlist.getSongsList(true)));

        PlayQueue.getInstance().addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);
        uploadBtn = (Button) v.findViewById(R.id.uploadBtn);
        if (playlist.getSongsList().size() == 0) {
            disableBtn(uploadBtn, "Cannot upload Playlist without songs!");
        }
        Log.d("PlaylistFragment", "startFindPlaylistByName: " + playlist.getName());
        Client.getInstance().findPlaylistByName(playlist.getName(), this);
        if (playlist.isAlreadyUploaded()) {
            disableBtn(uploadBtn, "already uploaded");
        } else {
            final PlaylistFragment _this = this;
            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLoading(true);
                    uploadRequestHandle = Client.getInstance().addPlaylist(playlist, _this);
                }
            });
        }
        return v;
    }

    private void markAsCurrentlyPlaying(int index) {
        removeCurrentlyPlayingMark();
        int correctedIndex = index - getListView().getFirstVisiblePosition();

        currentlyPlayingView = getListView().getChildAt(correctedIndex);
        if (currentlyPlayingView != null) {
            currentlyPlayingView.setBackgroundColor(Color.argb(100, 80, 80, 80));
        }
    }

    private void removeCurrentlyPlayingMark() {
        if (currentlyPlayingView != null) {
            currentlyPlayingView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    protected void disableBtn(Button btn, String text) {
        btn.setText(text);
        btn.setEnabled(false);
        btn.setBackgroundColor(Color.DKGRAY);
        btn.setTextColor(Color.WHITE);
    }

    private void setLoading(boolean visible) {
        if (visible) {
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = new ProgressDialog(getActivity());
            loadingDialog.setMessage("Uploading playlist \"" + playlist.getName() + "\"...");
            loadingDialog.setTitle("Upload playlist");
            loadingDialog.setCancelable(true);
            loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (uploadRequestHandle != null && !uploadRequestHandle.isFinished()) {
                        uploadRequestHandle.cancel(true);
                        Toast.makeText(getActivity(), "Uploading request canceled!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            loadingDialog.show();
        } else if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListeners(view);
    }

    protected void initListeners (View view)
    {
        removedSongNotification = ((TextView)view.findViewById(R.id.removedSongNotification));
        removedSongNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastRemovedSong != null && lastRemovedPos != -1)
                    playlist.getSongsList(true).add(lastRemovedPos, lastRemovedSong);
                lastRemovedSong = null;
                lastRemovedPos = -1;
                hideRemovedSongNotification();
                SongArrayAdapter adapter = (SongArrayAdapter) getListAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
        final ListView listView = getListView();
//        listView.setOnItemLongClickListener(this);
        listView.setOnTouchListener(new OnSwipeListener() {
            @Override
            public void onRightSwipe() {
                dragItem(listView.pointToPosition((int) endX, (int) endY), startX, endX);
            }

            @Override
            public void onLeftSwipe() {

                dragItem(listView.pointToPosition((int) endX, (int) endY), startX, endX);
            }

            @Override
            public void onUp() {
                cancelDrag();
                dragCanceled = false;
            }
        });
    }

    private void hideRemovedSongNotification() {
        removedSongNotification.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                removedSongNotification.setVisibility(View.GONE);
            }
        });

        removedSongNotification.setAnimation(fadeIn);
    }

    private void cancelDrag()
    {
        if (currentlyDraggedItemPos == -1) {
            return;
        }
        dragCanceled = true;
        ListView listView = getListView();
        View item = listView.getChildAt(currentlyDraggedItemPos - listView.getFirstVisiblePosition());
        if (item != null) {
            item.setX(0);
            currentDraggedX = 0;
            currentlyDraggedItemPos = -1;
        }
    }

    private void dragItem(int position, float startX, float endX) {
        if (dragCanceled || position == -1) {
            return;
        }
        ListView listView = getListView();

        if (currentlyDraggedItemPos == -1) {
            currentlyDraggedItemPos = position;
        }
        View item = listView.getChildAt(currentlyDraggedItemPos - listView.getFirstVisiblePosition());
        if (item != null) {
            currentDraggedX = endX - startX;
            Point size = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
            if ((endX >= size.x * 0.95 || endX <= size.x * 0.05) && Math.abs(currentDraggedX) > size.x * 0.4) {
                cancelDrag();
                item.setVisibility(View.GONE);
                SongArrayAdapter adapter = (SongArrayAdapter)getListAdapter();
                if (adapter != null) {
                    lastRemovedSong = adapter.getItem(position);
                    lastRemovedPos = position;
                    playlist.removeSong(lastRemovedSong);
                    adapter.notifyDataSetChanged();
                    showRevertRemoveToast();
                }
            } else {
                item.setX(currentDraggedX);
            }
        }
    }

    private void showRevertRemoveToast() {
        removedSongNotification.setText("The song \"" + lastRemovedSong.getSongname() + "\" has been removed from your playlist. To revert, simply click this message.");
        removedSongNotification.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        removedSongNotification.setAnimation(fadeIn);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        PlayQueue.getInstance().removeObserver(this);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (v.isEnabled()) {
            if (PlayQueue.getInstance().getCurrentPlaylist() != playlist) {
                PlayQueue.getInstance().importPlaylist(playlist);
            }
            PlayQueue.getInstance().jumpToTrack(position);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Song song = (Song)getListAdapter().getItem(position);
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setTitle("delete song from playlist?");
        deleteDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playlist.removeSong(song);
            }
        });
        deleteDialog.setNegativeButton("no", null);
        deleteDialog.setMessage("Are you shure you want to delete \"" + song.getArtist() + " - " + song.getSongname() + "\" from your playlist \"" + playlist.getName() + "\"?");
        deleteDialog.create().show();
        return true;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        PlaylistsManager.getInstance().setCurrentPlaylist(playlist);
        playlist.addObserver(this);
        SongArrayAdapter adapter = (SongArrayAdapter)getListAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private int getSongIndex (Song song)
    {
        Playlist currentPlaylist = PlayQueue.getInstance().getCurrentPlaylist();
        if (currentPlaylist == playlist) {
            return playlist.getSongsList().indexOf(song);
        } else {
            return -1;
        }
    }

    @Override
    public void onNewSongPlaying(Song song) {
        int index = getSongIndex(song);
        if (index != -1) {
            markAsCurrentlyPlaying(index);
        } else {
            removeCurrentlyPlayingMark();
        }
    }

    @Override
    public void onCannotInitializeSong(Song song) {
        int index = getSongIndex(song);
        TextView v = (TextView) getListView().getChildAt(index);
        if (v != null) {
            v.setEnabled(false);
            v.setText(v.getText() + " (not found!)");
            v.setTextColor(Color.rgb(100, 100, 100));
            v.setPaintFlags(v.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public void onPlaylistChange(final Playlist playlist) {
        if (playlist.getId() == this.playlist.getId()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (playlist.getSongsList().size() == 0) {
                        disableBtn(uploadBtn, "Cannot upload Playlist with no songs!");
                    }

                    SongArrayAdapter adapter = (SongArrayAdapter)getListAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        onPlaylistChange(playlist);
        super.onResume();
    }

    @Override
    public void onAddPlaylistSuccess() {
        setLoading(false);
        disableBtn(uploadBtn, "uploaded!");
    }

    @Override
    public void onAddPlaylistError(boolean alreadyExists) {
        setLoading(false);
        String alreadyExistsString = alreadyExists ? " Playlist name already exists." : "Error occured while trying to upload playlist!";
        Toast.makeText(getActivity(), alreadyExistsString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFindSinglePlaylistSuccess(Playlist onlinePlaylist) {
        if (!playlist.getName().equals(onlinePlaylist.getName())) {
            return;
        }
        if (onlinePlaylist.equals(playlist)) {
            disableBtn(uploadBtn, "already uploaded!");
            playlist.setAlreadyUploaded(true);
        } else {
            disableBtn(uploadBtn, "Not uploadable, playlist with this name already exists!");
        }
    }

    @Override
    public void onFindSinglePlaylistError() {
        Log.w("PlaylistFragment", "error while searching playlist with name");
    }

    private class SongArrayAdapter extends ArrayAdapter<Song>
    {
        public SongArrayAdapter(Context context, int resource, int textViewResourceId, List<Song> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final Song song = getItem(position);
            TextView view = (TextView) super.getView(position, convertView, parent);

            String text = (position + 1) + ". " + song.getArtist() + " - " + song.getSongname();

            view.setVisibility(View.VISIBLE);

            if (song.isNotPlayable()) {
                view.setEnabled(false);
                view.setText(text + " (not found!)");
                view.setTextColor(Color.rgb(100, 100, 100));
                view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                view.setEnabled(true);
                view.setText(text);
                view.setPaintFlags(view.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                view.setTextColor(Color.BLACK);
            }
            if (PlayQueue.getInstance().getCurrentSong() == song) {
                currentlyPlayingView = view;
                currentlyPlayingView.setBackgroundColor(Color.argb(100, 80, 80, 80));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            if (position == currentlyDraggedItemPos) {
                view.setX(currentDraggedX);
            } else {
                view.setX(0);
            }
            return view;
        }
    }
}
