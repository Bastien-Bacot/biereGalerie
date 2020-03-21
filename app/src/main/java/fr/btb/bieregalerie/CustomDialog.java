package fr.btb.bieregalerie;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


public class CustomDialog extends Dialog implements View.OnClickListener {
    private Button ok;
    private EditText edt;
    private String comm, prevCom;

    public String getComm() {
            return comm;
            }

    public CustomDialog(@NonNull Context context) {
            super(context);
            }
    public CustomDialog(@NonNull Context context, String prevCom) {
        super(context);
        this.prevCom = prevCom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            edt = findViewById(R.id.edt_comm);
            edt.findFocus();
            ok = findViewById(R.id.btn_ok);
            ok.setOnClickListener(this);
            if(!TextUtils.isEmpty(prevCom)){
                edt.append(prevCom);
            }
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }

    @Override
    public void onClick(View v) {
            comm = edt.getText().toString();
            dismiss();
            }
}