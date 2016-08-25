package ciex.edu.mx.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by azulyoro on 15/08/16.
 */
public class LoadImageFromUrl extends AsyncTask< Object, Void, Bitmap > {
    ImageView mView;
    String url, imageName;
    private Context mContext;

    public LoadImageFromUrl(Context context, String url, String imageName, ImageView mView ) {
        this.mContext = context;
        this.url=url;
        this.mView=mView;
        this.imageName = imageName;
    }

    @Override
    protected Bitmap doInBackground( Object... params ) {
        Bitmap image = getImageBitmap(mContext, imageName );
        if(image == null){
            image = loadBitmap( url );
            if(image!=null)
                saveImage(mContext, image, imageName);
        }
        return image;
    }

    @Override
    protected void onPostExecute( Bitmap image ) {
        super.onPostExecute( image );
        mView.setImageBitmap(image);
    }

    public Bitmap getImageBitmap(Context context, String name){
        try{
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
        }
        return null;
    }

    public Bitmap loadBitmap( String url ) {
        URL newurl;
        Bitmap bitmap = null;
        try {
            newurl = new URL( url );
            bitmap = BitmapFactory.decodeStream( newurl.openConnection( ).getInputStream( ) );
        } catch ( MalformedURLException e ) {
            e.printStackTrace( );
        } catch ( IOException e ) {

            e.printStackTrace( );
        }
        return bitmap;
    }

    public void saveImage(Context context, Bitmap b,String name){
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}