package com.mobile.shenkar.shani.mytaskteam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Viki on 22/02/2016.
 */
public class imageHelper {

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {


        int squreSize = 110;
        int targetWidth = scaleBitmapImage.getWidth();
        int targetHeight = scaleBitmapImage.getHeight();
        if(targetWidth > targetHeight)
        {
            squreSize = targetHeight;
        }
        else
        {
            squreSize = targetWidth;
        }
        Bitmap targetBitmap = Bitmap.createBitmap(squreSize,
                squreSize,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();

        path.addCircle(((float) squreSize - 1)/2,
                ((float) squreSize - 1)/2,
                (Math.min(((float) squreSize),
                        ((float) squreSize))/2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public static Bitmap scaleDown(Bitmap scaleBitmapImage) {
        return (Bitmap.createScaledBitmap(scaleBitmapImage, scaleBitmapImage.getWidth()/3, scaleBitmapImage.getHeight()/3, false));
    }
    public static Bitmap getAvatar(Bitmap bit)
    {
        return Bitmap.createScaledBitmap(bit, 150, 150, true);
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
