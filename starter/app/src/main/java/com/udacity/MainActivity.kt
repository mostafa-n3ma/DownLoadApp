package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private  var glideRequest:Long?=null
    private  var loadAppRequest:Long?=null
    private  var retrofitRequest:Long?=null

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private var URL: String? = null
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //Creating NotificationChannel
        createChannel(
            getString(R.string.channel_id), getString(R.string.channel_name)
        )

        //init notificationManager
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager


        /*
        - clickListener on the CustomButton
        - changing the ButtonState to Loading so the animation start
        - call the download fun
         */
        custom_button.setOnClickListener {
            custom_button.changState(ButtonState.Loading)
            download()
        }


        initRadioButtons()

    }

      /*
       - init the RadioButtons
       - assigning URL value according to the Checked RadioButton
      */
    private fun initRadioButtons() {
        radioGroup = findViewById(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                radioButton = findViewById(checkedId)
                when (radioButton.id) {
                    R.id.glide_btn -> {
                        URL = GLIDE_URL
                    }
                    R.id.loadApp_btn -> {
                        URL = LOAD_APP_URL
                    }
                    R.id.retrofit_btn -> {
                        URL = RETROFIT_URL
                    }
                }
            }

        })
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var fileName: String = "No File"
            var downloadStatus:String="No Status"
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //knowing which file has been downloaded ?
            when(id){
                glideRequest->{
                   fileName=getString(R.string.glide_description)
                }
                loadAppRequest->{
                    fileName=getString(R.string.load_app_description)
                }
                retrofitRequest->{
                    fileName=getString(R.string.retrofit_description)
                }
            }

            //getting the download status
            val query = DownloadManager.Query()
            query.setFilterById(id!!)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    downloadStatus="Successful"
                    custom_button.changState(ButtonState.Completed)
                }
                if (DownloadManager.STATUS_FAILED == status) {
                    downloadStatus="Failed"
                    custom_button.changState(ButtonState.Completed)
                }
            }

            //sending the data (fileName,downloadStatus) on the intent
            val intent=Intent(this@MainActivity,DetailActivity::class.java)
            intent.putExtra("file_name",fileName)
            intent.putExtra("download_status",downloadStatus)
            // send Notification with the specific intent
            notificationManager.sendNotification(this@MainActivity,intent)

        }
    }


    private fun download() {
        //checking the URL before downloading
        if(URL==null){
            Toast.makeText(this,resources.getString(R.string.no_file),Toast.LENGTH_SHORT).show()
            custom_button.changState(ButtonState.Clicked)
            return
        }
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


         downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        // recording the download Id
        when(URL){
            GLIDE_URL ->
                glideRequest=downloadID
            LOAD_APP_URL->
                loadAppRequest=downloadID
            RETROFIT_URL->
                retrofitRequest=downloadID
        }
    }

    companion object {

        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"



    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "get the download status"

            val notificationManager =
                this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }


}
