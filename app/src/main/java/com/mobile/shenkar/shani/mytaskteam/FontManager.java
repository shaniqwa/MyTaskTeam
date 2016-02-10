package com.mobile.shenkar.shani.mytaskteam;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Shani on 1/10/16.
 */
public class FontManager {
    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
