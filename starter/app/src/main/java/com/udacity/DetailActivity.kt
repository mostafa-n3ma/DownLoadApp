package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager

private var fileName:String?=null
private var downloadStatus:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        //init the notificationManager
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        //cancel the notification after opening the Details Activity
        notificationManager.cancelAll()

        //checking the intent and fetching data
        if (intent!=null){
             fileName=intent.getStringExtra("file_name")
            downloadStatus=intent.getStringExtra("download_status")
        }

        //updating TextViews
        file_name.text=fileName
        status.text=downloadStatus
        when(downloadStatus){
            "Failed"->status.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                else->{

                }
        }

        //finishing the Activity when clicking the Ok Button
        ok_btn.setOnClickListener {
            finish()
        }




    }

}
