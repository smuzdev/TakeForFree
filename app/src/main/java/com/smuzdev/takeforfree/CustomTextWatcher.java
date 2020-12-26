package com.smuzdev.takeforfree;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CustomTextWatcher implements TextWatcher {
    View v;
    EditText[] editTexts;

    public CustomTextWatcher(EditText[] editTexts, Button v) {
        this.v = v;
        this.editTexts = editTexts;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().length() <= 0) {
                v.getBackground().setAlpha(128);
                v.setEnabled(false);
                break;
            } else {
                v.setEnabled(true);
                v.getBackground().setAlpha(255);
            }
        }
    }
}
