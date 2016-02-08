package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import pocketknife.PocketKnife;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class BeanAccessTest {

    @Test
    public void testSaveState() {
        String s = UUID.randomUUID().toString();

        BeanAccess beanAccess1 = new BeanAccess();
        beanAccess1.setSaveString(s);

        Bundle bundle = new Bundle();
        PocketKnife.saveInstanceState(beanAccess1, bundle);
        BeanAccess beanAccess2 = new BeanAccess();
        beanAccess2.setSaveString("");
        assertNotEquals(s, beanAccess2.getSaveString());
        PocketKnife.restoreInstanceState(beanAccess2, bundle);
        assertEquals(s, beanAccess2.getSaveString());
    }

    @Test
    public void testBindExtras() {
        String s1 = UUID.randomUUID().toString();
        String s2 = UUID.randomUUID().toString();
        BeanAccess beanAccess = new BeanAccess();
        Intent intent = new Intent();
        intent.putExtra("EXTRA_EXTRA_STRING", s1);
        new StringSerializer().put(intent, s2, BeanAccess.INTENT_KEY);

        assertNotEquals(s1, beanAccess.getExtraString());
        assertNotEquals(s2, beanAccess.getIsString());
        PocketKnife.bindExtras(beanAccess, intent);
        assertEquals(s1, beanAccess.getExtraString());
        assertEquals(s2, beanAccess.getIsString());
    }

    @Test
    public void testBindArgs() {
        String s1 = UUID.randomUUID().toString();
        String s2 = UUID.randomUUID().toString();
        String s3 = UUID.randomUUID().toString();
        BeanAccess beanAccess = new BeanAccess();
        Bundle bundle = new Bundle();
        bundle.putString("ARG_ARG_STRING", s1);
        new StringSerializer().put(bundle, s2, BeanAccess.ARG_KEY_1);
        bundle.putString(BeanAccess.ARG_KEY_2, s3);

        assertNotEquals(s1, beanAccess.getArgString());
        assertNotEquals(s2, beanAccess.getBsString());
        assertNotEquals(s3, beanAccess.pString);
        PocketKnife.bindArguments(beanAccess, bundle);
        assertEquals(s1, beanAccess.getArgString());
        assertEquals(s2, beanAccess.getBsString());
        assertEquals(s3, beanAccess.pString);
    }

}