package fr.btb.bieregalerie;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public  class AsyncData extends AsyncTask<Object,Void,JSONArray>{
    private FileSave fs;
    private DocumentFile data_file;
    private JSONArray full;
    private DataFileIF delegate;
    private WeakReference<Context> c;
    private final byte[] UTF8_BOM = {(byte)0xEF,(byte)0xBB,(byte)0xBF};

    AsyncData(DataFileIF delegate, FileSave fs, Context c){
        this.delegate = delegate;
        this.fs = fs;
        this.c=new WeakReference<>(c);
    }

    protected JSONArray doInBackground(Object... params) {
        data_file = fs.getDatPath();
        if (!data_file.exists()) {
            if (data_file.getParentFile().createFile("texte/txt", "bgdata").exists()) {
                write(full.put(params[1]));
            }
        }
        switch ((String) params[0]) {
            case "write":
                write(add(read(),(Data) params[1]));
                break;
            case "del":
                read();
                del((String) params[1]);
                break;
            case "select":
                read();
                arraySelection((String)params[1]);
                break;
            case "modif":
                read();
                write(modif((Data) params[1]));
                break;
            case "modifname":
                read();
                write(modifName((Data) params[1],(String) params[2]));
                break;
            case "clear":
                write(addAll((ArrayList<Data>) params[1]));
                break;
            default:
                read();
                break;
        }
        if(full!=null) {
            checkUp();
        }
        return full;
    }

    @Override
    protected void onPostExecute(JSONArray arr_data) {
        delegate.onResponseReceived(arr_data);
    }

    private JSONArray read(){
        JSONArray unsort;
        try {
            InputStream in = c.get().getContentResolver().openInputStream(data_file.getUri());
            if (data_file.exists() && data_file.length()>0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                unsort= new JSONArray(br.readLine());
                full = unsort;
                if(full.toString().equals("[]")){
                    full = new JSONArray();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return full;
    }
    private JSONArray addAll(ArrayList<Data> dat){
        JSONArray dat_array= new JSONArray();
        for (Data dat_elem : dat){
            dat_array.put(dat_elem);
        }
        return dat_array;}

    private JSONArray add(JSONArray dat_array, Data data){
        dat_array.put(data);
        return dat_array;}

    private void write(JSONArray dat_array){
        try {
            OutputStream out = c.get().getContentResolver().openOutputStream(data_file.getUri());
            assert out != null;
            out.write(UTF8_BOM);
            out.write(dat_array.toString().getBytes());
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private JSONArray modif (Data data){
        try {
            Data tempData;
            for (int i = 0; i < full.length(); i++) {
                String[] separated = full.getString(i).split(",");
                tempData = new Data(separated[0], separated[1], Integer.parseInt(separated[2]), separated[3], separated[4]);
                if(tempData.getName().contains(data.getName())){
                    full.remove(i);
                    i=full.length();
                }
            }
            full.put(data);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return full;
    }
    private JSONArray modifName (Data data, String oldName){
        try {
            Data tempData;
            for (int i = 0; i < full.length(); i++) {
                String[] separated = full.getString(i).split(",");
                tempData = new Data(separated[0], separated[1], Integer.parseInt(separated[2]), separated[3], separated[4]);
                if(tempData.getName().contains(oldName)){
                    full.remove(i);
                    i=full.length();
                }
            }
            full.put(data);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return full;
    }
    private void del (String detail){
        try {
            for (int i = 0; i < full.length(); i++) {
                if(((String)full.get(i)).contains(detail)){
                    full.remove(i);
                    i=full.length();
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        write(full);
    }

    private void checkUp (){
        try {
            boolean b=false;
            Data tempData;
            for (int i = 0; i < full.length(); i++) {
                String[] separated = full.getString(i).split(",");
                tempData = new Data(separated[0], separated[1], Integer.parseInt(separated[2]), separated[3], separated[4]);
                if(tempData.getName().isEmpty()||tempData.getName().contains("temp")){
                    full.remove(i);
                    i=full.length();
                    b=true;
                }
            }
            if(b)write(full);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    private void arraySelection(String select){
        JSONArray temp = new JSONArray();
        try{
        for(int i=0;i<full.length();i++){
            if(((Data)full.get(i)).getName().contains(select)){
                temp.put(full.get(i));
            }
        }
        full = temp;
        }catch(JSONException e){e.printStackTrace();}
    }

}