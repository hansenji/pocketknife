package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.Random;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class MultiAnnotationTest {

    @Test
    public void testMultiAnnotationActivity() throws Exception {
        Random random = new Random();

        Intent intent = new Intent(RuntimeEnvironment.application, MultiAnnotationActivity.class);
        int extraInt = random.nextInt();
        intent.putExtra(MultiAnnotationActivity.EXTRA_INT, extraInt);

        ActivityController<MultiAnnotationActivity> saveController = Robolectric.buildActivity(MultiAnnotationActivity.class).withIntent(intent).create();
        MultiAnnotationActivity activity = saveController.start().restart().visible().get();

        assertEquals(extraInt, activity.i);

        int activityInt = random.nextInt();
        activity.i = activityInt;


        Bundle saveState = new Bundle();
        saveController.saveInstanceState(saveState);

        ActivityController<MultiAnnotationActivity> restoreController = Robolectric.buildActivity(MultiAnnotationActivity.class).withIntent(intent)
                .create(saveState);
        activity = restoreController.start().restart().visible().get();

        assertEquals(activityInt, activity.i);
    }

    @Test
    public void testMultiAnnotationFragment() throws Exception {
        Random random = new Random();

        Bundle args = new Bundle();
        int argInt = random.nextInt();
        args.putInt(MultiAnnotationFragment.ARG_INT, argInt);

        MultiAnnotationFragment saveFragment = MultiAnnotationFragment.newInstance();
        saveFragment.setArguments(args);

        saveFragment.onActivityCreated(null);

        assertEquals(argInt, saveFragment.i);

        int fragmentInt = random.nextInt();
        saveFragment.i = fragmentInt;

        Bundle saveState = new Bundle();
        saveFragment.onSaveInstanceState(saveState);

        MultiAnnotationFragment restoreFragment = MultiAnnotationFragment.newInstance();
        restoreFragment.setArguments(args);
        restoreFragment.onActivityCreated(saveState);

        assertEquals(fragmentInt, restoreFragment.i);
    }
}
