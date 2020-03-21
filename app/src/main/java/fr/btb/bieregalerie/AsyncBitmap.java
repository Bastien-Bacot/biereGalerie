package fr.btb.bieregalerie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AsyncBitmap extends AsyncTask<Void, Integer, Boolean> {


    private WeakReference<Context> context;
    private ArrayList<Uri> uriList;
    private ArrayList<Bitmap> arrmap = new ArrayList<>();
    private BitmapListIF delegate;
    private WeakReference<ProgressBar> pb;
    private WeakReference<TextView> txt_load;
    public AsyncBitmap(BitmapListIF delegate, ArrayList uriList, Context context) {
        this.delegate = delegate;
        this.uriList = uriList;
        this.context = new WeakReference<>(context);
        pb = new WeakReference<>((ProgressBar) ((Activity)context).findViewById(R.id.progress_add));
        txt_load = new WeakReference<>((TextView) ((Activity)context).findViewById(R.id.txt_loading));

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pb.get().setVisibility(View.VISIBLE);
        txt_load.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            for(int i=0;i<uriList.size();i++) {
               // InputStream inputStream = context.get().getContentResolver().openInputStream(uriList.get(i));
                //BitmapFactory.Options options = new BitmapFactory.Options();
               // options.inSampleSize = 1;
                Bitmap myBitmap1= MediaStore.Images.Media.getBitmap(context.get().getContentResolver(), uriList.get(i));
                Matrix rmatrix = new Matrix();
                if (myBitmap1.getWidth() > myBitmap1.getHeight()) {
                    rmatrix.postRotate(90);
                    myBitmap1 = Bitmap.createBitmap(myBitmap1, 0, 0, myBitmap1.getWidth(), myBitmap1.getHeight(), rmatrix, true);}
                arrmap.add(myBitmap1);
            }
        }catch (Exception e){e.printStackTrace();}
        return true;
    }


    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);
        pb.get().setVisibility(View.GONE);
        txt_load.get().setVisibility(View.GONE);
        delegate.onResponseReceived(arrmap);
    }
}