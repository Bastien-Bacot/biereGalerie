package fr.btb.bieregalerie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v4.provider.DocumentFile;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.github.chrisbanes.photoview.PhotoView;
import org.json.JSONArray;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class AdderActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private PhotoView imgV;
    private Spinner spinnerType, spinnerDetail;
    private ArrayAdapter<String> dataAdapter, dataDetail;
    private ArrayList<Uri> uriList;
    private ArrayList<Bitmap> bitArray;
    private String genre, mode;
    private int cursor = 0;
    private FileSave fs;
    private boolean[] bArray;
    private boolean botos;
    private Data dt;
    private DocumentFile rootDF, creater, hCreater, tempDF;
    private int coInt;
    private String annee, fullName;
    private static final int RESULT_OK = -1;
    private static final String EXT = ".png";
    private static final String TEMPN = "temp.png";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adder_layout);
        setResult(RESULT_OK, new Intent());
        imgV = findViewById(R.id.imgv_add);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                uriList = null;
            }else{
                uriList = (ArrayList<Uri>)extras.getSerializable("list");
                botos = extras.getBoolean("photo");
            }
        }else{
            uriList = (ArrayList<Uri>)savedInstanceState.getSerializable("list");
            botos = savedInstanceState.getBoolean("photo");
        }
        bArray = new boolean[uriList.size()];
        for (int i=0;i<uriList.size();i++){
            bArray[i]=false;
        }
        new AsyncBitmap(new BitmapListIF() {
            @Override
            public void onResponseReceived(ArrayList f) {
                bitArray=f;
                setPic();
            }
        }, uriList, this).execute();
        fs = new FileSave(this);
        fs.treeFileTest();
        rootDF = fs.getSaveDir();
        tempDF = rootDF.findFile(TEMPN);
        setSpinner();
    }

    public void onClick(View v){
        coInt =cursor;
        if(checkSaved()){
            if(finalCheck()){
                cursor++;
                setPic();
            }
        }
    }
    private void setSpinner(){
        spinnerDetail =findViewById(R.id.spinner_detail);
        spinnerDetail.setOnItemSelectedListener(this);
        String[] detailArray = getResources().getStringArray(R.array.detail);
        dataDetail = new ArrayAdapter<>(this, R.layout.simple_spinner_item, detailArray);
        dataDetail.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerDetail.setAdapter(dataDetail);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerType.setVisibility(View.GONE);
        spinnerType.setOnItemSelectedListener(this);
        String [] type = getResources().getStringArray(R.array.type_biere);
        dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, type);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(dataAdapter);
    }
    public void setPic(){
        Bitmap bmp = bitArray.get(cursor);
        imgV.setImageBitmap(bmp);
    }


    public boolean checkSaved(){
        EditText edtName = findViewById(R.id.edt_name_add);
        EditText edtDetail = findViewById(R.id.edt_year_add);
        genre = genre.replaceAll(" ", "_");
        if(!edtName.getText().toString().isEmpty()){
            bArray[coInt]=true;

            switch(mode){
                case "Rhum":
                    fullName = genre+edtName.getText();
                    folderPicker(getString(R.string.rhum));
                    dt = new Data(genre, edtName.getText().toString(), mode);
                    edtDetail.setText("");
                    edtName.setText("");
                    break;
                case "Whisky":
                    fullName = genre+edtName.getText();
                    folderPicker(getString(R.string.whisky));
                    dt = new Data(genre, edtName.getText().toString(), mode);
                    edtDetail.setText("");
                    edtName.setText("");
                    break;
                case "Vin":
                    annee = edtDetail.getText().toString()+" ";
                    fullName = genre+annee+edtName.getText();
                    folderPicker(getString(R.string.vin));
                    dt = new Data(genre, annee+edtName.getText().toString(), mode);
                    edtDetail.setText("");
                    edtName.setText("");
                    break;
                case "Autre":
                    annee = edtDetail.getText().toString()+"_";
                    if(!annee.contains(" ")){
                       annee= annee.concat(" ");
                    }
                    fullName = annee.replaceAll(" ", "_")+edtName.getText();
                    folderPicker(getString(R.string.divers));
                    dt = new Data(annee,edtName.getText().toString(), mode);
                    edtDetail.setText("");
                    edtName.setText("");
                    break;
                default:
                    fullName = genre+edtName.getText();
                    folderPicker(getString(R.string.biere));
                    dt = new Data(genre, edtName.getText().toString(), mode);
                    edtName.setText("");
                    break;
            }
            new AsyncData(new DataFileIF() {
                @Override
                public void onResponseReceived(JSONArray result) {}
            }, fs, this).execute("write", dt);
            return true;
        } else{
            Toast.makeText(this,"Nom non renseigné", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean finalCheck(){
        if(cursor+1>=bitArray.size()){
            finish();
            return false;
        }
        return true;
    }

    public void setBgMode(){
    String[] type = getResources().getStringArray(R.array.type_biere);
    EditText edt_divers = findViewById(R.id.edt_year_add);
    switch (mode){
        case "Bière":
            spinnerType.setVisibility(View.VISIBLE);
            type = getResources().getStringArray(R.array.type_biere);
            dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, type);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerType.setAdapter(dataAdapter);
            break;
        case "Vin":
            spinnerType.setVisibility(View.VISIBLE);
            edt_divers.setVisibility(View.VISIBLE);
            type = getResources().getStringArray(R.array.type_vin);
            dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, type);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerType.setAdapter(dataAdapter);
            edt_divers.setInputType(TYPE_CLASS_NUMBER);
            edt_divers.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
            break;
        case "Whisky":
            spinnerType.setVisibility(View.VISIBLE);
            type = getResources().getStringArray(R.array.type_whisky);
            dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, type);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerType.setAdapter(dataAdapter);
            break;
        case "Rhum":
            spinnerType.setVisibility(View.VISIBLE);
            type = getResources().getStringArray(R.array.type_rhum);
            dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, type);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerType.setAdapter(dataAdapter);
            break;
        case "Autre":
            spinnerType.setVisibility(View.GONE);
            edt_divers.setVisibility(View.VISIBLE);
            edt_divers.setHint("Type");
            edt_divers.setInputType(TYPE_CLASS_TEXT);
            break;
        default:
            spinnerType.setVisibility(View.VISIBLE);
            dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item,type);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerType.setAdapter(dataAdapter);
            break;
        }
    }
    private void folderPicker(String name){
        DocumentFile[] child = rootDF.listFiles();
        for (DocumentFile documentFile : child) {
            if (documentFile.isDirectory()) {
                if (documentFile.getName().contains(name)) {
                    if (!documentFile.getName().contains(".")) {
                        creater = documentFile;
                    }else{
                        hCreater = documentFile;
                    }
                }
            }
        }
        new AsyncThumb().execute(fullName, this, hCreater, uriList.get(0).getPath(),bitArray.get(coInt));
       //TODO use copy del old file(if .size()<2) ? rename new one so tempdf == new file
        try {
            DocumentsContract.moveDocument(getContentResolver(), tempDF.getUri(), rootDF.getUri(), creater.getUri());
            tempDF = creater.findFile(TEMPN);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        tempDF.renameTo(fullName+EXT);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_type:
                genre = parent.getItemAtPosition(position).toString()+"_";
            break;
            case R.id.spinner_detail:
                mode = parent.getItemAtPosition(position).toString();
                setBgMode();
                break;
        }

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        switch (arg0.getId()) {
            case R.id.spinner_type:
                genre = arg0.getItemAtPosition(0).toString() + "_";
                break;
            case R.id.spinner_detail:
                mode = arg0.getItemAtPosition(0).toString();
                break;
        }
    }
}