package com.example.pocketknife;

import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class SimpleActivityTest {
  @Test public void verifyContentViewInjection() {
      ActivityController<SimpleActivity> initialController = Robolectric.buildActivity(SimpleActivity.class).create();
      initialController.start().restart().visible().get();
      Bundle bundle = new Bundle();
      initialController.saveInstanceState(bundle);
      ActivityController<SimpleActivity> secondaryController = Robolectric.buildActivity(SimpleActivity.class).create(bundle);
      SimpleActivity simpleActivity = secondaryController.start().restart().visible().get();

      // Make sure all saved objects are restored.

  }
}
