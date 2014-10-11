package com.example.pocketknife;

import android.os.Bundle;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;


@RunWith(RobolectricTestRunner.class)
public class SimpleActivityTest {
    // TODO Improve Tests.

    @Test
    public void verifySaveRestoreState() {
        ActivityController<SimpleActivity> initialController = Robolectric.buildActivity(SimpleActivity.class).create();
        initialController.start().restart().visible().get();
        Bundle bundle = new Bundle();
        initialController.saveInstanceState(bundle);
        ActivityController<SimpleActivity> secondaryController = Robolectric.buildActivity(SimpleActivity.class).create(bundle);
        SimpleActivity simpleActivity = secondaryController.start().restart().visible().get();

        // Make sure all saved objects are restored.
    }

    @Test
    public void verifyArgumentInjection() {
        ActivityController<SimpleActivity> initialController = Robolectric.buildActivity(SimpleActivity.class).create();
        SimpleActivity simpleActivity = initialController.start().restart().visible().get();
        SimpleFragment simpleFragment = (SimpleFragment) simpleActivity.getSupportFragmentManager().findFragmentById(R.id.container);
        Assert.assertEquals(simpleFragment.stringArg, "I AM AWESOME");
    }

}
