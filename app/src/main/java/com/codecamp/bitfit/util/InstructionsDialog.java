package com.codecamp.bitfit.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.codecamp.bitfit.R;

/**
 * Created by maxib on 16.02.2018.
 */

public class InstructionsDialog extends Dialog {

    private final String title;
    private final Drawable instructionsImage;
    private final String instructions;

    /**
     *
     * @param context for dialog
     * @param title of instruction
     * @param instructionImage drawable
     * @param instructions instruction text
     */
    public InstructionsDialog(Context context, String title, Drawable instructionImage, String instructions) {
        super(context);

        this.title = title;
        this.instructionsImage = instructionImage;
        this.instructions = instructions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_instructions);

        TextView titleTextView = findViewById(R.id.instructions_title);
        ImageView imageView = findViewById(R.id.instructions_image);
        TextView instructionsTextView = findViewById(R.id.instructions_text);
        TextView okButton = findViewById(R.id.instructions_ok_button);

        titleTextView.setText(title);
        imageView.setImageDrawable(instructionsImage);
        instructionsTextView.setText(instructions);

        // dismiss dialog
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
}
