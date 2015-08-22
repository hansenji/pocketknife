package com.example.pocketknife;

public class Foo {
    private String bar;
    private int baz;

    public Foo() {

    }

    public Foo(String bar, int baz) {
        this.bar = bar;
        this.baz = baz;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public int getBaz() {
        return baz;
    }

    public void setBaz(int baz) {
        this.baz = baz;
    }
}
