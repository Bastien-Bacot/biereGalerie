package fr.btb.bieregalerie;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import org.json.JSONArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class AsynChecker extends AsyncTask<Object,Void,Boolean> {
    private FileSave fs;
    private JSONArray data;
    private WeakReference<Context> c;
    private CheckerIF delegate;

    AsynChecker(CheckerIF delegate){
        this.delegate=delegate;
    }

    protected Boolean doInBackground(Object... params) {
        fs = (FileSave) params[0];
        data = (JSONArray) params[1];
        c = new WeakReference<> ((Context) params[2]);
            checker();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.onResponseReceived();
    }

    private void checker(){
        ArrayList<DocumentFile> listFolder= fs.getAllFolder(true);
        ArrayList<DocumentFile> listHfolder= fs.getAllFolder(false);
        picCount(listFolder);
        Iterator<DocumentFile> ite_folder = listFolder.iterator();
        Iterator<DocumentFile> ite_hfolder = listHfolder.iterator();
        while (ite_folder.hasNext() && ite_hfolder.hasNext()) {
            DocumentFile folder = ite_folder.next();
            DocumentFile hFolder = ite_hfolder.next();
            if(folder.listFiles().length!=hFolder.listFiles().length){
                if (folder.listFiles().length < hFolder.listFiles().length) {
                    delThumb(folder.listFiles(), hFolder.listFiles());
                }
                else {
                    createThumb(folder.listFiles(), hFolder.listFiles(),hFolder );
                }
            }

        }
    }

    private void picCount(ArrayList<DocumentFile> root){
        int l =0;
        for (DocumentFile elem_folder: root) {
            l+=elem_folder.listFiles().length;
        }
        Log.d("BGBTB", "pic number == "+l);
        Log.d("BGBTB", "data length == "+data.length());
        if(l!=data.length()){
            if (l > data.length()) {

                for (DocumentFile elem_folder: root) {
                    createData(elem_folder.listFiles(), elem_folder);
                }
            }else if (l< data.length()){
                Log.d("BGBTB", "clear data ??");
                ArrayList<Data> dataGen = new ArrayList<>();
                for (DocumentFile elem_folder: root) {
                    if(elem_folder.listFiles().length>0){
                        dataGen.addAll(datGen(elem_folder.listFiles(), elem_folder));
                    }
                }
                new AsyncData(new DataFileIF() {
                    @Override
                    public void onResponseReceived(JSONArray result) {
                    }
                }, fs,c.get()).execute("clear", dataGen);
            }
        }
    }

    private ArrayList<Data> datGen(DocumentFile[] picArray, DocumentFile parentFolder){
        ArrayList<Data> dataGen = new ArrayList<>();
        for(DocumentFile pic_elem:picArray){
            Data nDat = new Data(Objects.requireNonNull(pic_elem.getName()).substring(0,
                    pic_elem.getName().lastIndexOf("_")+1),
                    pic_elem.getName().substring(pic_elem.getName().lastIndexOf("_") + 1, pic_elem.getName().lastIndexOf(".")),
                    parentFolder.getName());
            dataGen.add(nDat);
        }
        return dataGen;
    }
    private void forceClear(ArrayList<DocumentFile> root){
        Log.d("BGBTB", "clear data ??");
        ArrayList<Data> dataGen = new ArrayList<>();
        for (DocumentFile elem_folder: root) {
            if(elem_folder.listFiles().length>0){
                dataGen.addAll(datGen(elem_folder.listFiles(), elem_folder));
            }
        }
        Log.d("BGBTB", "data gen == "+ dataGen.toString());
        new AsyncData(new DataFileIF() {
            @Override
            public void onResponseReceived(JSONArray result) {
            }
        }, fs,c.get()).execute("clear", dataGen);
    }

    private void createData(DocumentFile[] picArray, DocumentFile parentFolder){
        //check
        for(DocumentFile pic_elem:picArray){
            Data nDat = new Data(pic_elem.getName().substring(0,
                    pic_elem.getName().lastIndexOf("_")+1),
                    pic_elem.getName().substring(pic_elem.getName().lastIndexOf("_") + 1, pic_elem.getName().lastIndexOf(".")),
                    parentFolder.getName());
            new AsyncData(new DataFileIF() {
                @Override
                public void onResponseReceived(JSONArray result) {
                }
            }, fs,c.get()).execute("write", nDat);
        }
    }

    private void delThumb(DocumentFile[] picArray, DocumentFile[] thumbArray) {
        if (picArray.length < thumbArray.length && picArray.length > 0) {
            boolean b=false;
            for (DocumentFile thumb_elem : thumbArray) {
                for (DocumentFile pic_elem : picArray) {
                    if(thumb_elem.getName().contains(pic_elem.getName())){
                        b=false;
                        break;
                    }else{
                        b=true;
                    }
                }
                if(b){
                    new AsyncData(new DataFileIF() {
                        @Override
                        public void onResponseReceived(JSONArray result) {
                        }
                    }, fs,c.get()).execute("del", thumb_elem.getName());
                    thumb_elem.delete();
                }
            }
        } else if (picArray.length < thumbArray.length) {
            for(DocumentFile thumb_elem:thumbArray){
                new AsyncData(new DataFileIF() {
                    @Override
                    public void onResponseReceived(JSONArray result) {
                    }
                }, fs,c.get()).execute("del", thumb_elem.getName());
                thumb_elem.delete();
            }
        }
    }

    private void createThumb(DocumentFile[] picArray, DocumentFile[] thumbArray,DocumentFile thumbFolder){
        int j = 0;
        try {
        if (thumbArray.length == 0) {
            for(DocumentFile pic_elem:picArray){
                new AsyncThumb().execute(pic_elem.getName(), c.get(),thumbFolder,null
                        , MediaStore.Images.Media.getBitmap(c.get().getContentResolver(), pic_elem.getUri()));
            }
        }else {
            boolean b=false;
            for (DocumentFile pic_elem : picArray) {
                for (DocumentFile thumb_elem : thumbArray) {
                    if(pic_elem.getName().contains(thumb_elem.getName())){
                        b=false;
                        break;
                    }else{
                        b=true;
                    }
                }
                if(b){
                    new AsyncThumb().execute(pic_elem.getName(), c,thumbFolder,null
                            , MediaStore.Images.Media.getBitmap(c.get().getContentResolver(), pic_elem.getUri()));
                }
            }
        }
        }catch (Exception e){
                e.printStackTrace();
        }
    }

}