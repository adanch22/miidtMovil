package ciex.edu.mx.adapter;

/**
 * Created by azulyoro on 11/04/16.
 * This adapter class identifies the current logged in user messages by user
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ciex.edu.mx.R;
import ciex.edu.mx.activity.FullScreenViewActivity;
import ciex.edu.mx.model.Message;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;
    private static String today;
    private int end=0;
    private Context mContext;
    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp, title;
        ImageView imageview;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) itemView.findViewById(R.id.title);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            imageview = (ImageView) itemView.findViewById(R.id.image);
        }
    }


    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, String userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_card, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getUser().getId().equals(userId)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        ((ViewHolder) holder).message.setText(message.getMessage());

        String timestamp = getTimeStamp(message.getCreatedAt());
        ((ViewHolder) holder).timestamp.setText(timestamp);

        String title = message.getUser().getName()+":";
        ((ViewHolder) holder).title.setText(title);

        String image = message.getImage();
        final String messg = message.getMessage();
        if(image!=null && !image.equals("")){
            String url = "http://www.itnovacion.com/ciex/images/"+image;
            new LoadImageFromUrl().execute(((ViewHolder) holder).imageview, url, image, messg);
        }else{
            ((ViewHolder) holder).imageview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }
    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    private class LoadImageFromUrl extends AsyncTask< Object, Void, Bitmap > {
        ImageView ivPreview = null;
        String url;
        String imageName;
        String messg;
        @Override
        protected Bitmap doInBackground( Object... params ) {
            this.ivPreview = (ImageView) params[0];
            this.url = (String) params[1];
            this.imageName= (String) params[2];
            this.messg=(String) params[3];
            Bitmap image = getImageBitmap(mContext, imageName );
            if(image == null){
                image = loadBitmap( url );
                saveImage(mContext, image, imageName);
            }
            return image;
        }

        @Override
        protected void onPostExecute( Bitmap image ) {
            super.onPostExecute( image );
            ivPreview.setImageBitmap( image );
            ivPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullScreenViewActivity.class);
                    i.putExtra("image", imageName);
                    i.putExtra("message", messg);
                    mContext.startActivity(i);
                }
            });
            this.cancel(true);
        }
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

    public Bitmap getImageBitmap(Context context,String name){
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

}