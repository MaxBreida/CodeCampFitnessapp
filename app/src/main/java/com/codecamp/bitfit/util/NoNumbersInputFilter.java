package com.codecamp.bitfit.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by maxib on 03.03.2018.
 *
 * This
 */

public class NoNumbersInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!Character.isLetter(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }
}
