package fr.btb.bieregalerie;

import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import org.json.JSONArray;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSave implements Serializable {
    String[] paths = {"Bière","Vin","Whisky","Rhum","Autre"};
    JSONArray jarray = new JSONArray();
    Context c;
    DocumentFile saveDir;

    public FileSave(Context c){
        this.c=c;
    }


    public void treeFileTest(){
        List<UriPermission> perms = c.getContentResolver().getPersistedUriPermissions();
        Uri treeUri = perms.get(0).getUri();
        DocumentFile folder = DocumentFile.fromTreeUri(c, treeUri);
        if (folder.findFile("Bg")!= null){
            saveDir = folder.findFile("Bg");
        }else{
            folder.createDirectory("Bg");
            saveDir = folder.findFile("Bg");
        }
        if(saveDir.listFiles().length!=11){
            for (String folder_name : paths ) {
                if(saveDir.findFile(folder_name)==null){
                    dirCeate(saveDir, folder_name);
            }
                if(saveDir.findFile("."+folder_name)==null){
                    dirCeate(saveDir, "."+folder_name);
            }
            }
            if(saveDir.findFile("bgdata.txt")==null){
                creatData();
        }}
    }

    private void creatData(){
        try {
            DocumentFile newFile = saveDir.createFile("text/plain", "bgdata");
            jarray.put(new Data("temp", "temp", "temp"));
            OutputStream out = c.getContentResolver().openOutputStream(newFile.getUri());
            out.write(jarray.toString().getBytes());
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public DocumentFile getSaveDir(){
        return saveDir;
    }

    private void dirCeate(DocumentFile saveDir, String path){
        saveDir.createDirectory(path);
        saveDir.createDirectory("." + path);
    }


    public ArrayList<DocumentFile> getAllFolder(boolean b){
        ArrayList<DocumentFile> raw = new ArrayList<>();
        raw.addAll(Arrays.asList(saveDir.listFiles()));
        ArrayList<DocumentFile> clear = new ArrayList<>();
        if(saveDir.findFile("temp.png")!=null){
            raw.remove(saveDir.findFile("temp.png"));
        }
        if(b){
            for(DocumentFile elem_raw:raw){
                if(!elem_raw.getName().contains(".") && elem_raw.isDirectory()){
                    clear.add(elem_raw);
                }
            }
        }else{
            for(DocumentFile elem_raw:raw){
                if(elem_raw.getName().contains(".") && elem_raw.isDirectory()){
                    clear.add(elem_raw);
                }
            }
        }
        return clear;
    }

    public ArrayList<ArrayList<Uri>> getFolder(String type){
        DocumentFile[] clear = new DocumentFile[2];
        ArrayList<Uri> norm = new ArrayList();
        ArrayList<Uri> hide = new ArrayList();
        clear[0]= saveDir.findFile(type);
        clear[1]= saveDir.findFile("."+type);
        ArrayList<ArrayList<Uri>> listResult = new ArrayList();
        for(DocumentFile elem_oneDir : Arrays.asList(saveDir.findFile(type).listFiles())){
            norm.add(elem_oneDir.getUri());
        }
        listResult.add(norm);
        for(DocumentFile elem_oneHDir : Arrays.asList(saveDir.findFile("."+type).listFiles())){
            hide.add(elem_oneHDir.getUri());
        }
        listResult.add(hide);
        return listResult;
    }

    public DocumentFile getDatPath(){
        DocumentFile dat =saveDir;
        DocumentFile[] raw = saveDir.listFiles();
        for (int i =0;i<raw.length;i++){
            if(raw[i].isFile()&&raw[i].getName().contains("bgdata")){
               dat =raw[i];
            }
        }
        return dat;
    }

}