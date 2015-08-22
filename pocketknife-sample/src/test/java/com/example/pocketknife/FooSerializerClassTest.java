package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import pocketknife.PocketKnife;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class FooSerializerClassTest {

    private Random random;

    @Before
    public void setup() {
        random = new Random(42);
    }

    @Test
    public void testSaveState() {
        String bar = UUID.randomUUID().toString();
        int baz = random.nextInt();

        Foo foo1 = new Foo(bar, baz);
        FooSerializerClass fooSerializerClass1 = new FooSerializerClass();
        fooSerializerClass1.saveFoo = foo1;

        Bundle bundle = new Bundle();
        PocketKnife.saveInstanceState(fooSerializerClass1, bundle);

        Foo foo2 = new Foo();
        FooSerializerClass fooSerializerClass2 = new FooSerializerClass();
        fooSerializerClass2.saveFoo = foo2;

        PocketKnife.restoreInstanceState(fooSerializerClass2, bundle);
        assertTrue(foo2 == fooSerializerClass2.saveFoo); // Same memory address didn't get reassigned
        assertEquals(bar, foo2.getBar());
        assertEquals(baz, foo2.getBaz());
    }

    @Test
    public void testBuildAndBindArguments() {
        String bar = UUID.randomUUID().toString();
        int baz = random.nextInt();
        SerializerBuilder builder = new PocketKnifeSerializerBuilder(RuntimeEnvironment.application);
        Bundle bundle = builder.buildBundle(new Foo(bar, baz));

        Foo foo = new Foo();
        FooSerializerClass fooSerializerClass = new FooSerializerClass();
        fooSerializerClass.argFoo = foo;
        PocketKnife.bindArguments(fooSerializerClass, bundle);


        assertTrue(foo == fooSerializerClass.argFoo); // Same memory address didn't get reassigned
        assertEquals(bar, foo.getBar());
        assertEquals(baz, foo.getBaz());
    }

    @Test
    public void testBuildAndBindExtras() {
        String bar = UUID.randomUUID().toString();
        int baz = random.nextInt();
        SerializerBuilder builder = new PocketKnifeSerializerBuilder(RuntimeEnvironment.application);
        Intent intent = builder.buildIntent(new Foo(bar, baz));

        Foo foo = new Foo();
        FooSerializerClass fooSerializerClass = new FooSerializerClass();
        fooSerializerClass.extraFoo = foo;
        PocketKnife.bindExtras(fooSerializerClass, intent);

        assertTrue(foo == fooSerializerClass.extraFoo); // Same memory address didn't get reassigned
        assertEquals(bar, foo.getBar());
        assertEquals(baz, foo.getBaz());
    }

    @Test
    public void testBuildFragmentAndBindArguments() {
        String bar = UUID.randomUUID().toString();
        int baz = random.nextInt();
        SerializerBuilder builder = new PocketKnifeSerializerBuilder(RuntimeEnvironment.application);
        Fragment fragment = builder.buildFragment(new Foo(bar, baz));
        Bundle bundle = fragment.getArguments();

        Foo foo = new Foo();
        FooSerializerClass fooSerializerClass = new FooSerializerClass();
        fooSerializerClass.argFoo = foo;
        PocketKnife.bindArguments(fooSerializerClass, bundle);

        assertTrue(foo == fooSerializerClass.argFoo); // Same memory address didn't get reassigned
        assertEquals(bar, foo.getBar());
        assertEquals(baz, foo.getBaz());
    }
}