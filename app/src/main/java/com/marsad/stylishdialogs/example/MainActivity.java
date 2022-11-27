package com.marsad.stylishdialogs.example;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.marsad.stylishdialogs.StylishAlertDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        findViewById(R.id.simpleMsgDialog)
                .setOnClickListener(v ->
                        new StylishAlertDialog(this, StylishAlertDialog.NORMAL)
                                .setContentText("Hey, You try me")
                                .show()
                );

        findViewById(R.id.titleWTextDialog)
                .setOnClickListener(v ->
                        new StylishAlertDialog(this, StylishAlertDialog.NORMAL)
                                .setTitleText("Hey, This is title")
                                .setContentText("Content text")
                                //.setCancellable(false)
                                .setCancelledOnTouchOutside(false)
                                .setConfirmButton("Dismiss", StylishAlertDialog::dismissWithAnimation)
                                .show()

                );

        findViewById(R.id.successMsgDialog)
                .setOnClickListener(v ->
                        new StylishAlertDialog(this, StylishAlertDialog.SUCCESS)
                                .setContentText("Hey, You try me")
                                .show()
                );

        findViewById(R.id.errorMsgDialog)
                .setOnClickListener(v ->
                        new StylishAlertDialog(this, StylishAlertDialog.ERROR)
                                .setContentText("Hey, You try me")
                                .show()
                );

        findViewById(R.id.successWithNormalDialog)
                .setOnClickListener(v -> {

                    StylishAlertDialog alertDialog = new StylishAlertDialog(this, StylishAlertDialog.PROGRESS);
                    alertDialog.setContentText("Processing...")
                            .show();

                    new Handler(Looper.getMainLooper())
                            .postDelayed((Runnable) () -> {
                                alertDialog.changeAlertType(StylishAlertDialog.SUCCESS);
                                alertDialog.setContentText("Task Completed");
                                alertDialog.setConfirmButton("OK", StylishAlertDialog::dismissWithAnimation);
                                alertDialog.setCancelButton("Cancel", StylishAlertDialog::dismissWithAnimation);
                            }, 2500);

                });


        findViewById(R.id.warnWithConfirmBtn)
                .setOnClickListener(v -> {
                    StylishAlertDialog alertDialog = new StylishAlertDialog(this, StylishAlertDialog.WARNING);
                    alertDialog.setContentText("Are you sure you want to proceed")
                            .setConfirmButton("Yes, Proceed", StylishAlertDialog -> {
                                alertDialog.changeAlertType(com.marsad.stylishdialogs.StylishAlertDialog.SUCCESS);
                                alertDialog.setContentText("Job Done")
                                        .setDismissOnClick(true);
                                alertDialog.setConfirmButton("OK", StylishAlertDialog1 -> System.out.println("Completed"));

                            })

                            .show();

                });

    }

}