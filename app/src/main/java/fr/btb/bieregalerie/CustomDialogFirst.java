package fr.btb.bieregalerie;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CustomDialogFirst extends Dialog implements View.OnClickListener {
    private Button ok;

    public CustomDialogFirst(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_first);
        ok = findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}