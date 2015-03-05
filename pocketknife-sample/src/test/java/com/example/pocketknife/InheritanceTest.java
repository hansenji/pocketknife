package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.Random;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class InheritanceTest {

    @Test
    public void testInheritance() throws Exception {
        Random random = new Random(42);

        Bundle args = new Bundle();
        int parentArg = random.nextInt();
        int childArg = random.nextInt();
        args.putInt(ChildFragment.CHILD_ARG, childArg);
        args.putInt(ParentFragment.PARENT_ARG, parentArg);

        Intent intent = new Intent(Robolectric.application, GrandchildActivity.class);
        int parentExtra = random.nextInt();
        int childExtra = random.nextInt();
        intent.putExtra(ParentActivity.PARENT_EXTRA, parentExtra);
        intent.putExtra(ChildActivity.CHILD_EXTRA, childExtra);
        intent.putExtra(ChildActivity.FRAGMENT_ARGS, args);

        ActivityController<GrandchildActivity> initialController = Robolectric.buildActivity(GrandchildActivity.class).withIntent(intent).create();
        GrandchildActivity originalActivity = initialController.start().restart().visible().get();

        assertEquals(childExtra, originalActivity.childExtra);
        assertEquals(parentExtra, originalActivity.parentExtra);

        assertEquals(childArg, originalActivity.fragment.childArg);
        assertEquals(parentArg, originalActivity.fragment.parentArg);

        originalActivity.paInt = random.nextInt();
        originalActivity.caInt = random.nextInt();

        originalActivity.fragment.pfInt = random.nextInt();
        originalActivity.fragment.cfInt = random.nextInt();

        Bundle saveState = new Bundle();
        initialController.saveInstanceState(saveState);


        Bundle copyArgs = new Bundle();
        copyArgs.putInt(ChildFragment.CHILD_ARG, random.nextInt() - childArg);
        copyArgs.putInt(ParentFragment.PARENT_ARG, random.nextInt() - parentArg);

        Intent copyIntent = new Intent(Robolectric.application, GrandchildActivity.class);
        copyIntent.putExtra(ParentActivity.PARENT_EXTRA, random.nextInt() - parentExtra);
        copyIntent.putExtra(ChildActivity.CHILD_EXTRA, random.nextInt() - childExtra);
        copyIntent.putExtra(ChildActivity.FRAGMENT_ARGS, copyArgs);

        ActivityController<GrandchildActivity> secondaryController = Robolectric.buildActivity(GrandchildActivity.class).withIntent(copyIntent).create(saveState);
        GrandchildActivity copyActivity = secondaryController.start().restart().visible().get();

        assertEquals(originalActivity.caInt, copyActivity.caInt);
        assertEquals(originalActivity.paInt, copyActivity.paInt);

        assertEquals(originalActivity.fragment.cfInt, copyActivity.fragment.cfInt);
        assertEquals(originalActivity.fragment.pfInt, copyActivity.fragment.pfInt);
    }
}
