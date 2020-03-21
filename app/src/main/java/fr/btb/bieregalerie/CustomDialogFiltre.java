package fr.btb.bieregalerie;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static android.content.Context.MODE_PRIVATE;

public class CustomDialogFiltre extends Dialog implements View.OnClickListener {
    public Button ok;
            Context ctxt;

    public CustomDialogFiltre(@NonNull Context context) {
            super(context);
            this.ctxt = context;

            }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_filtre);

        RadioGroup radioGroup =  findViewById(R.id.radiogroup_filtre);
        RadioButton bAll = findViewById(R.id.select_all);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String type;
                switch (checkedId){
                    case R.id.select_beer:
                        type ="Bière";
                        break;
                    case R.id.select_vin:
                        type ="Vin";
                        break;
                    case R.id.select_whisky:
                        type = "Whisky";
                        break;
                    case R.id.select_rhum:
                        type ="Rhum";
                        break;
                    case R.id.select_divers:
                        type ="Autre";
                        break;
                    default:
                        type = "all";
                }
                SharedPreferences.Editor edit = ctxt.getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                edit.putString("aff", type);
                edit.apply();
            }
        });
        bAll.setChecked(true);
        ok = findViewById(R.id.btn_okfiltre);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}