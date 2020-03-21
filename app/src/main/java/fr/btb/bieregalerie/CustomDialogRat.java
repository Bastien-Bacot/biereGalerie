package fr.btb.bieregalerie;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

public class CustomDialogRat extends Dialog implements View.OnClickListener {
    public ImageView rat1,rat2,rat3,rat4,rat5;
    Context ctxt;
    EditText edt;
    String comm;
    int rating = 0;

    public String getComm() {
        return comm;
    }

    public CustomDialogRat(@NonNull Context context) {
        super(context);
        this.ctxt = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_rat);
        rat1 = findViewById(R.id.rat1);
        rat1.setOnClickListener(this);
        rat2 = findViewById(R.id.rat2);
        rat2.setOnClickListener(this);
        rat3 = findViewById(R.id.rat3);
        rat3.setOnClickListener(this);
        rat4 = findViewById(R.id.rat4);
        rat4.setOnClickListener(this);
        rat5 = findViewById(R.id.rat5);
        rat5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rat1:
                rating=1;
                dismiss();
                break;
            case R.id.rat2:
                rating=2;
                dismiss();
                break;
            case R.id.rat3:
                rating=3;
                dismiss();
                break;
            case R.id.rat4:
                rating=4;
                dismiss();
                break;
            case R.id.rat5:
                rating=5;
                dismiss();
                break;
        }
    }
    public int getRating(){
        return rating;
    }
}