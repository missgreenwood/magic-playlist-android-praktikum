package controllers.mainFragments.generatorFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import controllers.mainFragments.generatorFragments.playlistFragment.OnSwipeListener;
import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.mediawrappers.PlayQueue;
import tests.R;

/**
 * created by Andreas
 */
public class PlaylistFragment extends ListFragment implements
        PlayQueue.Listener, AdapterView.OnItemLongClickListener, Playlist.Listener {

    private Playlist playlist;
    private View currentlyPlayingView;

    private int currentlyDraggedItemPos = -1;
    private float currentDraggedX = 0;
    private Song lastRemovedSong;
    private int lastRemovedPos;
    private TextView removedSongNotification;
    private boolean dragCanceled = false;

    public PlaylistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playlist == null) {
            playlist = new Playlist();
        }
        setListAdapter(new SongArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, playlist.getSongsList(true)));

        PlayQueue.getInstance().addObserver(this);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        removedSongNotification = ((TextView)view.findViewById(R.id.removedSongNotification));
        removedSongNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PlaylistFragment", " ON REVERT CLICKED");
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
                dragItem(listView.pointToPosition((int)endX, (int)endY), startX, endX);
            }

            @Override
            public void onLeftSwipe() {
                dragItem(listView.pointToPosition((int)endX, (int)endY), startX, endX);
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
//        if (removedSongNotification.getAnimation() == null) {
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removedSongNotification.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
        if (dragCanceled) {
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
//        if (removedSongNotification.getAnimation() == null) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        removedSongNotification.setAnimation(fadeIn);
//        }
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
        playlist.addObserver(this);
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
    public void onPlaylistChange() {
        SongArrayAdapter adapter = (SongArrayAdapter)getListAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        onPlaylistChange();
        super.onResume();
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
