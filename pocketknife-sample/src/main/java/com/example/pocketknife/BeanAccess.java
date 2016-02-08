package com.example.pocketknife;

import pocketknife.BindArgument;
import pocketknife.BindExtra;
import pocketknife.BundleSerializer;
import pocketknife.IntentSerializer;
import pocketknife.SaveState;

public class BeanAccess {
    public static final String ARG_KEY_1 = "ARG_KEY_1";
    public static final String ARG_KEY_2 = "ARG_KEY_2";
    public static final String INTENT_KEY = "INTENT_KEY";

    @SaveState
    private String saveString;

    @BindExtra
    private String extraString;

    @BindArgument
    private String argString;

    @BindArgument(ARG_KEY_1)
    @BundleSerializer(StringSerializer.class)
    private String bsString;

    @BindExtra(INTENT_KEY)
    @IntentSerializer(StringSerializer.class)
    private String isString;

    @BindArgument(ARG_KEY_2)
    String pString;

    public String getSaveString() {
        return saveString;
    }

    public void setSaveString(String saveString) {
        this.saveString = saveString;
    }

    public String getExtraString() {
        return extraString;
    }

    public void setExtraString(String extraString) {
        this.extraString = extraString;
    }

    public String getArgString() {
        return argString;
    }

    public void setArgString(String argString) {
        this.argString = argString;
    }

    public String getBsString() {
        return bsString;
    }

    public void setBsString(String bsString) {
        this.bsString = bsString;
    }

    public String getIsString() {
        return isString;
    }

    public void setIsString(String isString) {
        this.isString = isString;
    }
}
