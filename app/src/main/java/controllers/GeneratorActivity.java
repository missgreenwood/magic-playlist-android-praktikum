package controllers;

        import android.app.AlertDialog;
        import android.app.FragmentManager;
        import android.content.DialogInterface;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;

        import controllers.generatorFragments.GeneratorSettingsFragment;
        import controllers.generatorFragments.PlaylistFragment;
        import models.mediaModels.Song;
        import models.playlistGenerator.MainGenerator;
        import tests.R;

/**
 * Created by judith on 27.12.14.
 */
public class GeneratorActivity extends ActionBarActivity implements
        GeneratorSettingsFragment.Listener,
        PlaylistFragment.Listener,
        MainGenerator.Listener
{

    private GeneratorSettingsFragment settingsFragment;
    private FragmentManager fragmentManager;
    private MainGenerator generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        fragmentManager = getFragmentManager();
        generator = new MainGenerator(this);
        initGeneratorSettingsView();
    }

    private void initGeneratorSettingsView() {
        settingsFragment = (GeneratorSettingsFragment) fragmentManager.findFragmentById(R.id.generatorSettingsFragment);
        settingsFragment.setListener(this);
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.generatorMainViewGroup, settingsFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.generator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void testStartClicked(View view)
    {
        generator.getNextSong(new Song("Johnny Flynn", "lost and found"));
    }

    @Override
    public void buttonClicked(View view) {
        switch(view.getId()) {
            case R.id.testStart:
                testStartClicked(view);
                break;
            case R.id.button6:
                // genresClicked(view);
                break;
            case R.id.button7:
                // artistsClicked(view);
                break;
            case R.id.button8:
                // songsClicked(view);
                break;
        }
    }

    public void onTrackClick(int id) {

    }

    @Override
    public void nextSongFound(final Song song) {
        final MainGenerator _generator = generator;
        final GeneratorActivity _this = this;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Song zu Playliste hinzufügen?")
                     .setMessage("Song: " + song.getArtist() + " - " + song.getSongname())
                     .setCancelable(false);
        dialogBuilder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _generator.addSongToPlaylist(song);
                _generator.getNextSong(song);
            }
        });
        dialogBuilder.setNeutralButton("Verwerfen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _generator.getNextSong(null);
            }
        });
        dialogBuilder.setNegativeButton("Abschließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _this.finishPlaylistClicked();
            }
        });
        dialogBuilder.create().show();
    }

    @Override
    public void nextSongError(int errorStatus) {
        Log.e("nextSongError", String.valueOf(errorStatus));
    }

    private void finishPlaylistClicked() {

    }
}
