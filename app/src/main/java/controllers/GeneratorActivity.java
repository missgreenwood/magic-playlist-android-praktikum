package controllers;

        import android.app.FragmentManager;
        import android.app.FragmentTransaction;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;

        import controllers.generatorFragments.ArtistsFragment;
        import controllers.generatorFragments.GeneratorSettingsFragment;
        import controllers.generatorFragments.GenresListFragment;
        import controllers.generatorFragments.PlaylistFragment;
        import controllers.generatorFragments.SongsFragment;
        import models.playlistGenerator.MainGenerator;
        import tests.R;

/**
 * Created by judith on 27.12.14.
 */
public class GeneratorActivity extends ActionBarActivity implements
        GeneratorSettingsFragment.Listener,
        PlaylistFragment.Listener
{

    private GeneratorSettingsFragment settingsFragment;
    private FragmentManager fragmentManager;
    private ArtistsFragment artistsFragment;
    private SongsFragment songsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        fragmentManager = getFragmentManager();

        initGeneratorSettingsView();
        initArtistsView();
        initSongsView();
    }

    private void initGeneratorSettingsView() {
        settingsFragment = (GeneratorSettingsFragment) fragmentManager.findFragmentById(R.id.generatorSettingsFragment);
        settingsFragment.setListener(this);
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.generatorMainViewGroup, settingsFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    private void initArtistsView() {
        artistsFragment = (ArtistsFragment) fragmentManager.findFragmentById(R.id.artistsFragment);
        artistsFragment.setListener((ArtistsFragment.Listener) this);
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.generatorMainViewGroup, settingsFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    private void initSongsView() {
        songsFragment = (SongsFragment) fragmentManager.findFragmentById(R.id.songsFragment);
        songsFragment.setListener((SongsFragment.Listener) this);
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
        // MainGenerator generator = new MainGenerator();
        // generator.getNextSong();
    }

    public void artistsClicked(View view)
    {
        //TODO: implement actions on artists button clicked
    }

    public void songsClicked(View view)
    {
        //TODO: implement actions on songs button clicked
    }

    @Override
    public void buttonClicked(View view) {
        switch (view.getId()) {
            case R.id.testStart:
                testStartClicked(view);
                break;
            case R.id.button6:
                GenresListFragment.genresClicked(view);
                break;
            case R.id.button7:
                artistsClicked(view);
                break;
            case R.id.button8:
                songsClicked(view);
                break;
        }
    }
    public void onTrackClick(int id) {

    }
}
