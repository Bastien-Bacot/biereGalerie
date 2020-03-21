package fr.btb.bieregalerie;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Collections;

public class GalleryActivity extends AppCompatActivity {
    public FileSave fs = new FileSave(this);
    private JSONArray arr_data;
    private ArrayList<Uri> fullList, fullHList;
    private static final int READ_REQUEST_CODE = 42;
    private static final int ADDER_REQUEST = 53;
    private static final int FULL_REQUEST = 54;
    private static final int TREE_CODE = 43;
    private static final int TREE_DATA_CODE = 44;
    private static final int RESULT_OK = -1;
    private SharedPreferences pref;
    private DocumentFile df;
    private AdaptatorGal adapter;
    private boolean b = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        fullList = new ArrayList<>();
        fullHList = new ArrayList<>();
        pref = getSharedPreferences("MyPref", MODE_PRIVATE);

        if(!pref.contains("aff")){
            SharedPreferences.Editor affedit = pref.edit();
            affedit.putString("aff", "all");
            affedit.apply();
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            getData();
        }
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(b){
            callAsyncData();
            b=false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        b=true;
    }

    private void dialCall(){
        CustomDialogFirst cdf = new CustomDialogFirst(this);
        cdf.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(final DialogInterface dialog) {
                treeCall();
            }
        });
        cdf.show();
    }
    private void treeCall(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                |Intent.FLAG_GRANT_READ_URI_PERMISSION
                |Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        startActivityForResult(intent, TREE_CODE);
    }
    private void getData() {
        fs = new FileSave(this);
        fs.treeFileTest();
        new AsyncData(new DataFileIF() {
            @Override
            public void onResponseReceived(final JSONArray result) {
                arr_data=result;
                new AsynChecker(new CheckerIF() {
                    @Override
                    public void onResponseReceived() {
                        creatRecy();
                        populateFile();
                    }
                }).execute(fs, result, getApplicationContext());
            }
        }, fs,this).execute("read");
    }
    private void refData(){
        new AsyncData(new DataFileIF() {
            @Override
            public void onResponseReceived(final JSONArray result) {
                arr_data=result;
                populateFile();
            }
        }, fs,this).execute("read");
    }

    private void populateFile(){
        if(arr_data==null){
            callAsyncData();
        }else{
            readSDCard();
        }
    }
    private void creatRecy(){
        final RecyclerView recyclerView = findViewById(R.id.gallery);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdaptatorGal(this, fullHList, fullList, arr_data);
        recyclerView.setAdapter(adapter);
    }

    public void callAsyncData(){
        new AsyncData(new DataFileIF() {
            @Override
            public void onResponseReceived(JSONArray result) {
                arr_data =result;
                refreshRecycler();
            }
                }, fs,this).execute("read");
    }
    private void refreshRecycler(){
        readSDCard();
        adapter.arrChanged(arr_data);
    }

    private void readSDCard() {
        fullList.clear();
        fullHList.clear();
        if (pref.getString("aff", null).contains("all")) {
            ArrayList<DocumentFile>  allHDir =fs.getAllFolder(false);
            ArrayList<DocumentFile> allDir = fs.getAllFolder(true);
            for(DocumentFile elem_allDir :allDir){
                for(DocumentFile elem_dir: elem_allDir.listFiles()){
                    fullList.add(elem_dir.getUri());
                }
            }
            for(DocumentFile elem_allHDir :allHDir){
                for(DocumentFile elem_hdir: elem_allHDir.listFiles()){
                    fullHList.add(elem_hdir.getUri());
                }
            }
        } else {
            ArrayList<ArrayList<Uri>> listTemp = fs.getFolder( pref.getString("aff", null));
            fullList.addAll(listTemp.get(0));
            fullHList.addAll(listTemp.get(1));
        }
        Collections.sort(fullHList);
        Collections.sort(fullList);
        findViewById(R.id.progressBar).setVisibility(View.GONE);

    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_plus:
                performFileSearch();
                break;
            case R.id.btn_filter:
                CustomDialogFiltre cdf = new CustomDialogFiltre(this);
                cdf.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(final DialogInterface dialog) {
                        refreshRecycler();
                    }
                });
                cdf.show();
                break;
            case R.id.btn_photo:
                df = fs.saveDir;
                df.createFile("image/png","temp");
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                int request = 2;
                intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, df.findFile("temp.png").getUri());
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                startActivityForResult(intent, request);
                break;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pref = getSharedPreferences("MyPref", MODE_PRIVATE);
                if(pref.contains("perms")&& pref.getString("perms", null).contains(null)){
                    getData();}
                else{
                    if(this.getContentResolver().getPersistedUriPermissions().size()<1){
                        dialCall();
                        return;
                    }if (!DocumentFile.fromSingleUri(this, this.getContentResolver().getPersistedUriPermissions().get(0).getUri()).exists()){
                        dialCall();
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        ArrayList<Uri> uriList = new ArrayList();
        if (resultCode == RESULT_OK && requestCode == 2) {
            uriList.add(df.findFile("temp.png").getUri());
            adderCall(uriList, true);
        }
        else if(requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK){
            if (resultData != null) {
                if(resultData.getClipData() != null){
                    ClipData clipData = resultData.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item path = clipData.getItemAt(i);
                        uriList.add(path.getUri());
                    }}else{
                    uriList.add(resultData.getData());
                }
                adderCall(uriList, false);
            }
        }
        else if(requestCode== ADDER_REQUEST && resultCode == RESULT_OK){
            callAsyncData();
        }else if(requestCode==TREE_CODE || requestCode==TREE_DATA_CODE && resultCode==RESULT_OK){
            Uri treeUri = resultData.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            SharedPreferences.Editor affedit = pref.edit();
            affedit.putString("perms", pickedDir.getUri().toString());
            affedit.apply();
            getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(requestCode==TREE_DATA_CODE){ getData();}
        }else if (requestCode==FULL_REQUEST){
            callAsyncData();
        }
    }

    public void adderCall(ArrayList<Uri> list, boolean b){
        Intent intents = new Intent(this, AdderActivity.class);
        intents.putExtra("photo", b);
        intents.putExtra("list", list);
        startActivityForResult(intents, ADDER_REQUEST);
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }


}
