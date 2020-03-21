package fr.btb.bieregalerie;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.chrisbanes.photoview.PhotoView;
import org.json.JSONArray;
import java.io.FileNotFoundException;

public class FullScreenActivity extends Activity{
    TextView txtcomm;
    Data data = new Data();
    boolean modif = false;
    FileSave fs = new FileSave(this);
    Uri path,hpath ;
    private static final String EXT = ".png";
    private Context c;
    private  String oldName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscrenn_layout);
        PhotoView imgV = findViewById(R.id.imgv);
        TextView txtName = findViewById(R.id.txtv_name);
        fs.treeFileTest();
        c = this;
        txtcomm = findViewById(R.id.txtv_com);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                path= null;
            } else {
                path= (Uri)extras.get("filepath");
                hpath = (Uri)extras.get("hpath");
                data = (Data)extras.getSerializable("data");
            }
        } else {
            hpath= (Uri) savedInstanceState.getSerializable("hpath");
            path= (Uri) savedInstanceState.getSerializable("filepath");
            data = (Data)savedInstanceState.getSerializable("data");
        }
        txtName.setText(data.getName());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            try {
                Bitmap myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
                if (myBitmap.getHeight()<myBitmap.getWidth()) {
                    Matrix rMatrix = new Matrix();
                    rMatrix.postRotate(90);
                    myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), rMatrix, true);
                }
                imgV.setImageBitmap(myBitmap);
            }catch (Exception e){
                e.printStackTrace();
            }

        if(!data.getCom().isEmpty()){
            txtcomm.setText(data.getCom());
            txtcomm.setVisibility(View.VISIBLE);
        }
        if(data.getRat()>0){
            setRatPics(data.getRat());
        }
    }

    public void onClick(View v){
        TextView txtv_name = findViewById(R.id.txtv_name);
        EditText edt_name = findViewById(R.id.edt_name);
        DocumentFile dc=DocumentFile.fromSingleUri(this, path);
        DocumentFile hdc=DocumentFile.fromSingleUri(this, hpath);
        String oldC ="";

        switch (v.getId()){
            case R.id.commIc:
                if(data.getBcom()) {
                    oldC =data.getCom();
                }
                    final CustomDialog cdb = new CustomDialog(this, oldC);
                    cdb.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(final DialogInterface dialog) {
                            txtcomm.setText(cdb.getComm());
                            data.setCom(cdb.getComm());
                            data.setBcom(true);
                            callModif();

                    }});
                    cdb.show();
                txtcomm.setVisibility(View.VISIBLE);
                break;
            case R.id.rat:
                final CustomDialogRat cdbrat = new CustomDialogRat(this);
                cdbrat.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(final DialogInterface dialog) {
                        data.setRat(cdbrat.getRating());
                        setRatPics(cdbrat.getRating());
                        callModif();

                    }
                });
                cdbrat.show();
                break;
            case R.id.supIc:
                dc=DocumentFile.fromSingleUri(this, path);
                dc.delete();
                hdc.delete();
                new AsyncData(new DataFileIF() {
                    @Override
                    public void onResponseReceived(final JSONArray result) {
                        finish();
                    }
                }, fs,this).execute("del", data.getName());
                break;
            case R.id.txtv_name:
                edt_name.setText(txtv_name.getText());
                oldName = txtv_name.getText().toString();
                edt_name.requestFocus();
                InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_name, 0);
                txtcomm.setVisibility(View.GONE);
                findViewById(R.id.txtv_name).setVisibility(View.GONE);
                findViewById(R.id.edt_name).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_name_val).setVisibility(View.VISIBLE);
                break;
            case R.id.btn_name_val:
                txtv_name.setText(edt_name.getText().toString());
                txtv_name.setVisibility(View.VISIBLE);
                txtcomm.setVisibility(View.VISIBLE);
                findViewById(R.id.edt_name).setVisibility(View.GONE);
                findViewById(R.id.btn_name_val).setVisibility(View.GONE);
                data.setName(edt_name.getText().toString());
                try{
                    DocumentsContract.renameDocument(getContentResolver(), dc.getUri(),data.getCat()+data.getName()+ EXT);
                    DocumentsContract.renameDocument(getContentResolver(), hdc.getUri(),data.getCat()+data.getName()+ EXT);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                new AsyncData(new DataFileIF() {
                    @Override
                    public void onResponseReceived(JSONArray result) {

                    }
                }, fs,c).execute("modifname", data, oldName);
                imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_name.getWindowToken(), 0);
                break;

        }

    }
    private void callModif(){
        new AsyncData(new DataFileIF() {
            @Override
            public void onResponseReceived(JSONArray result) {

            }
        }, fs,c).execute("modif", data);
    }

    public void setRatPics(int rat){
        final ImageView ratimv = findViewById(R.id.rat);
        switch (rat){
            case 1:
                ratimv.setImageResource(R.drawable.rat1);
                break;
            case 2:
                ratimv.setImageResource(R.drawable.rat2);
                break;
            case 3:
                ratimv.setImageResource(R.drawable.rat3);
                break;
            case 4:
                ratimv.setImageResource(R.drawable.rat4);
                break;
            case 5:
                ratimv.setImageResource(R.drawable.rat5);
                break;
        }

    }
}
