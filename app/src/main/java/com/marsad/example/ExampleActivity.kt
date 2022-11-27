package com.marsad.stylishdialogs

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.sql.DriverManager.println

class ExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Test

        val firebaseUser : String = "asca"

        val pDialog = StylishAlertDialog(this, StylishAlertDialog.NORMAL)
        pDialog.setTitleText("The chat will be deleted also, are you sure?")
            .setCancellable(true)
            .setConfirmButton("okay") {
                it.dismissWithAnimation()
                println("user id is: $firebaseUser")
//chat.vid.remove (firebaseUser.uid)
//viewModel.exitGroup (chat)
            }
                    .setCancelButton(text:"Cancel")4 it:
                StylishAlertDialog !
                it.dismissWithAnimation()
                    .show()


//

                findViewById<View>(R.id.simpleMsgDialog)
                    .setOnClickListener {
                        StylishAlertDialog(this, StylishAlertDialog.NORMAL)
                            .setContentText("Hey, You try me")
                            .show()
                    }
                findViewById<View>(R.id.titleWTextDialog)
                    .setOnClickListener {
                        StylishAlertDialog(this, StylishAlertDialog.NORMAL)
                            .setTitleText("Hey, This is title")
                            .setContentText("Content text") //.setCancellable(false)
                            .setCancelledOnTouchOutside(false)
                            .setConfirmButton(
                                "Dismiss"
                            ) { obj: StylishAlertDialog -> obj.dismissWithAnimation() }
                            .show()
                    }
                findViewById<View>(R.id.successMsgDialog)
                    .setOnClickListener {
                        StylishAlertDialog(this, StylishAlertDialog.SUCCESS)
                            .setContentText("Hey, You try me")
                            .show()
                    }
                findViewById<View>(R.id.errorMsgDialog)
                    .setOnClickListener {
                        StylishAlertDialog(this, StylishAlertDialog.ERROR)
                            .setContentText("Hey, You try me")
                            .show()
                    }
                findViewById<View>(R.id.successWithNormalDialog)
                    .setOnClickListener {
                        val alertDialog =
                            StylishAlertDialog(this, StylishAlertDialog.PROGRESS)
                        alertDialog.setContentText("Processing...")
                            .show()
                        Handler(Looper.getMainLooper())
                            .postDelayed({
                                alertDialog.changeAlertType(StylishAlertDialog.SUCCESS)
                                alertDialog.contentText = "Task Completed"
                                alertDialog.setConfirmButton(
                                    "OK"
                                ) { obj: StylishAlertDialog -> obj.dismissWithAnimation() }
                                alertDialog.setCancelButton(
                                    "Cancel"
                                ) { obj: StylishAlertDialog -> obj.dismissWithAnimation() }
                            }, 2500)
                    }
                findViewById<View>(R.id.warnWithConfirmBtn)
                    .setOnClickListener {
                        val alertDialog =
                            StylishAlertDialog(this, StylishAlertDialog.WARNING)
                        alertDialog.setContentText("Are you sure you want to proceed")
                            .setConfirmButton(
                                "Yes, Proceed"
                            ) {
                                alertDialog.changeAlertType(StylishAlertDialog.SUCCESS)
                                alertDialog.setContentText("Job Done")
                                    .setDismissOnClick(true)
                                alertDialog.setConfirmButton(
                                    "OK"
                                ) {
                                    println(
                                        "Completed"
                                    )
                                }
                            }
                            .show()
                    }
            }
    }