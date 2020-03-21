package fr.btb.bieregalerie;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class AsyncThumb extends AsyncTask<Object,Void,Boolean> {
    private WeakReference<Context> c;
    private Bitmap btm;

    protected Boolean doInBackground(Object... params){
        c = new WeakReference<>((Context)params[1]);
        FileSave fs = new FileSave(c.get());
        this.btm = (Bitmap) params[4];
        DocumentFile pic = (DocumentFile)params[2];
        fs.treeFileTest();
        thumbCreate(pic,(String) params[0]);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    private boolean thumbCreate (DocumentFile pic,String fullName){
           DocumentFile df = pic.createFile("image/png", fullName);
           ByteArrayOutputStream bytes = new ByteArrayOutputStream();
           Bitmap ThumbImage =ThumbnailUtils.extractThumbnail(btm, btm.getWidth()/5, btm.getHeight()/5);
           ThumbImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
           try {
               OutputStream fo = c.get().getContentResolver().openOutputStream(df.getUri());
               fo.write(bytes.toByteArray());
               fo.flush();
               fo.close();
               Log.d("BGBTB", "thumb done");
           } catch (Exception e) {
               e.printStackTrace();
           }

       return true;
   }
}
