package com.mobatia.naisapp.activity.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.mobatia.naisapp.R
import com.mobatia.naisapp.activity.home.Home
import com.mobatia.naisapp.activity.login.Login
import com.mobatia.naisapp.activity.tutorial.Tutorial
import com.mobatia.naisapp.constant.PreferenceManager

class Splash : AppCompatActivity() {
    private lateinit var context: Context
    private val splashTimeOut: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        context = this
        Handler().postDelayed({
            if (PreferenceManager.getIsFirstLaunch(context) && PreferenceManager.getUserID(context)
                    .equals("") && PreferenceManager.getStaffID(context).equals("")
            ) {
                val tutorialIntent = Intent(
                    context, Tutorial::class.java
                )
                tutorialIntent.putExtra(resources.getString(R.string.TYPE), 1)
                startActivity(tutorialIntent)
                PreferenceManager.setIsFirstLaunch(context, false)
                finish()
            } else if (PreferenceManager.getUserID(context).equals("")) {
                System.out.println("Staff id logout" + PreferenceManager.getStaffID(context))
                if (PreferenceManager.getStaffID(context).equals("")) {
                    val loginIntent = Intent(
                        context, Login::class.java
                    )
                    startActivity(loginIntent)
                    finish()
                } else {
                    PreferenceManager.setIsSurveyHomeVisible(context, false)
                    PreferenceManager.setIsNoticeHomeVisible(context, false)
                    PreferenceManager.setIsDataSurveyHomeVisible(context, false)
                    val loginIntent = Intent(
                        context, Home::class.java
                    )
                    startActivity(loginIntent)
                    finish()
                }
            } else {
                PreferenceManager.setIsSurveyHomeVisible(context, false)
                PreferenceManager.setIsNoticeHomeVisible(context, false)
                PreferenceManager.setIsDataSurveyHomeVisible(context, false)
                val loginIntent = Intent(
                    context, Home::class.java
                )
                startActivity(loginIntent)
                finish()
            }

        }, splashTimeOut)
    }

}