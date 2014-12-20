package com.example.mymodule.mymodule.app;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;

import com.example.mymodule.metadatawrappers.LastfmMetadataWrapper;

/**
 * Created by charlotte on 07.12.14.
 */
public class MediaPlayTest extends ActivityInstrumentationTestCase2<TestActivity> {
   /* public MediaPlayTest(Class<TestActivity> activityClass) {
        super(activityClass);
    }*/

  /*  public MediaPlayTest(String name)
    {

        super(name, TestActivity.class);
    }*/


    private TestActivity mFirstTestActivity;
    private Button button;

    public MediaPlayTest() {

        super("", TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        mFirstTestActivity = getActivity();


        View nextButton = mFirstTestActivity.findViewById(R.id.button);
        View beforeButton = mFirstTestActivity.findViewById(R.id.button2);
        View pauseButton = mFirstTestActivity.findViewById(R.id.button3);
        View resumeButton = mFirstTestActivity.findViewById(R.id.button4);
        nextButton.setOnClickListener(mFirstTestActivity);
        beforeButton.setOnClickListener(mFirstTestActivity);
        pauseButton.setOnClickListener(mFirstTestActivity);
        resumeButton.setOnClickListener(mFirstTestActivity);


        testPreconditions(); //richtige Stelle?

        LastfmMetadataWrapper metadataWrapper = new LastfmMetadataWrapper();


        assertNotNull(metadataWrapper.findSimilarArtists("Radiohead", 5));


        /*
        assertNotNull(getActivity().getSongs());
        assertNotNull(getActivity().getPlayQueue());


        //verify correct button click behaviour
        int beforeCount = mFirstTestActivity.getPlayQueue().getCounter();
        TouchUtils.clickView(this, nextButton);
        assertTrue(mFirstTestActivity.getPlayQueue().getCounter() == beforeCount + 1);

        TouchUtils.clickView(this, beforeButton);
        assertTrue(mFirstTestActivity.getPlayQueue().getCounter() == beforeCount - 1);

*/
        //TODO: überprüfen, dass nächster Song gespielt wird!

    }


    public void testPreconditions() {
        assertNotNull("mFirstTestActivity is null", mFirstTestActivity);
        assertNotNull("mFirstButton is null", button);
    }


}
