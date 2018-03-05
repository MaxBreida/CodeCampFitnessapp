package com.codecamp.bitfit.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by maxib on 03.03.2018.
 *
 * This Class represents and EditText where the Software Keyboard automatically closes after
 * the EditText loses its focus
 */
public class CustomEditText
        extends android.support.v7.widget.AppCompatEditText
        implements View.OnFocusChangeListener {

    private Context context;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.setOnFocusChangeListener(this);
    }

    /**
     * This method gets called when the focus of the edittext changes, hence we want to
     * hide the software keyboard if we dont have focus anymore
     * @param v the view which calls onFocusChange
     * @param hasFocus the focus value
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(!hasFocus) {
            // hide
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } else {
            // show
            if (inputMethodManager != null) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }
}
