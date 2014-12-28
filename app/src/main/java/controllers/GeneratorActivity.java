package controllers;

        import android.app.FragmentManager;
        import android.app.FragmentTransaction;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;

        import controllers.generatorFragments.GeneratorSettingsFragment;
        import models.playlistGenerator.MainGenerator;
        import tests.R;

/**
 * Created by judith on 27.12.14.
 */
public class GeneratorActivity extends ActionBarActivity implements GeneratorSettingsFragment.Listener {

    private GeneratorSettingsFragment settingsFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        fragmentManager = getFragmentManager();

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
    // TODO: implement class

    public void testStartClicked(View view)
    {
        MainGenerator generator = new MainGenerator();
        generator.getNextSong();
    }

    @Override
    public void buttonClicked(View view) {
        switch(view.getId()) {
            case R.id.testStart:
                testStartClicked(view);
                break;
        }
    }
}
