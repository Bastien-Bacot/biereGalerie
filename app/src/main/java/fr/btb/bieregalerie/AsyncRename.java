package fr.btb.bieregalerie;

import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;


public class AsyncRename extends AsyncTask<Object,Void,Boolean> {

    protected Boolean doInBackground(Object... params){
        rename((DocumentFile) params[0],(String)params[1], (String)params[2]);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
    private void rename(DocumentFile df, String name, String temp ){
        df = df.findFile(temp);
        df.renameTo(name+".png");
    }

}
