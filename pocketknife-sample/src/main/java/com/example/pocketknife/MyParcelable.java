package com.example.pocketknife;

import android.os.Parcel;
import android.os.Parcelable;

public class MyParcelable implements Parcelable {
    public static final Creator<MyParcelable> CREATOR = new Creator<MyParcelable>() {
        @Override
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        @Override
        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };

    private int data;

    public MyParcelable(int data) {
        this.data = data;
    }

    private MyParcelable(Parcel in) {
        data = in.readInt();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(data).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MyParcelable && ((MyParcelable) obj).data == this.data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(data);
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
