package com.example.pocketknife;

import android.os.Bundle;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;


@RunWith(RobolectricTestRunner.class)
public class SimpleFragmentActivityTest {
    @Test
    public void verifySaveRestoreState() {
        ActivityController<SimpleFragmentActivity> initialController = Robolectric.buildActivity(SimpleFragmentActivity.class).create();
        initialController.start().restart().visible().get();
        Bundle bundle = new Bundle();
        initialController.saveInstanceState(bundle);
        ActivityController<SimpleFragmentActivity> secondaryController = Robolectric.buildActivity(SimpleFragmentActivity.class).create(bundle);
        SimpleFragmentActivity simpleActivity = secondaryController.start().restart().visible().get();
        SimpleFragment simpleFragment = (SimpleFragment) simpleActivity.getSupportFragmentManager().findFragmentById(R.id.container);

        // Make sure all saved objects are restored.
        Assert.assertEquals(1, simpleFragment.counter);
    }

    @Test
    public void verifyArgumentInjection() {
        ActivityController<SimpleFragmentActivity> initialController = Robolectric.buildActivity(SimpleFragmentActivity.class).create();
        SimpleFragmentActivity simpleActivity = initialController.start().restart().visible().get();
        SimpleFragment simpleFragment = (SimpleFragment) simpleActivity.getSupportFragmentManager().findFragmentById(R.id.container);
        Assert.assertEquals("I AM AWESOME", simpleFragment.stringArg);
    }

}
