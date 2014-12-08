package com.example.mymodule.mymodule.app;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.widget.Button;

import com.example.mymodule.mediawrappers.FileStreamingMediaService;

/**
 * Created by charlotte on 07.12.14.
 */
public class MediPlayTestUnit extends ActivityUnitTestCase<TestActivity> {
    private Intent mLaunchIntent;

    public MediPlayTestUnit() {
        super(TestActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.Theme_AppCompat);
        setActivityContext(context);
        mLaunchIntent = new Intent(context, TestActivity.class);
        // startActivity(mLaunchIntent, null, null);
        // startActivity(mLaunchIntent, null, null);


        //    launchNextButton.performClick();
//-> Use performClick here!


        //TODO: weiter benutzen!

        //testEverything();
    }


    public void testEverything() {

        startActivity(mLaunchIntent, null, null);


        final Button nextButton =
                (Button) getActivity()
                        .findViewById(R.id.button);
        nextButton.performClick();

    /*
        assertNotNull("Intent was null", newIntent);
        assertTrue(isFinishCalled());


        assertEquals(newIntent.getAction(), FileStreamingMediaService.ACTION_PLAY);
        */

    }


}
