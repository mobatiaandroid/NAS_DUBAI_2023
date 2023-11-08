package com.mobatia.naisapp.constant

import android.content.Context

class PreferenceManager {

    companion object {

        private val PREFSNAME = "NAS_DUBAI"

        fun setAccessToken(context: Context, id: String?) {
            val prefs = context.getSharedPreferences(
                PREFSNAME, Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putString("access_token", id)
            editor.apply()
        }

        fun getAccessToken(context: Context): String? {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            return prefs.getString("access_token", "")
        }

        fun setIsFirstLaunch(context: Context, result: Boolean) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putBoolean("is_first_launch", result)
            editor.apply()
        }

        fun getIsFirstLaunch(context: Context): Boolean {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            return prefs.getBoolean("is_first_launch", true)
        }

        fun getUserID(context: Context): String {
            var userid = ""
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            userid = prefs.getString("userid", "").toString()
            return userid
        }

        fun setUserID(context: Context, userid: String?) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putString("userid", userid)
            editor.apply()
        }

        fun setStaffID(context: Context, staffId: String?) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putString("staffid", staffId)
            editor.apply()
        }

        fun getStaffID(context: Context): String {
            var staffId = ""
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            staffId = prefs.getString("staffid", "").toString()
            return staffId
        }

        fun setIsSurveyHomeVisible(context: Context, result: Boolean) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putBoolean("is_survey_visible", result)
            editor.apply()
        }

        fun getIsSurveyHomeVisible(context: Context): Boolean {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            return prefs.getBoolean("is_survey_visible", false)
        }

        /**
         * Author:
         * Date:
         * Description:
         */

        fun setIsNoticeHomeVisible(context: Context, result: Boolean) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putBoolean("is_notice_visible", result)
            editor.apply()
        }

        fun getIsNoticeHomeVisible(context: Context): Boolean {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            return prefs.getBoolean("is_notice_visible", false)
        }

        fun setIsDataSurveyHomeVisible(context: Context, result: Boolean) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putBoolean("is_survey_visible_data", result)
            editor.commit()
        }

        fun getIsDataSurveyHomeVisible(context: Context): Boolean {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            return prefs.getBoolean("is_survey_visible_data", false)
        }

        fun getUserType(context: Context): String {
            var userType = ""
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            userType = prefs.getString("user_type", "").toString()
            return userType
        }

        fun setUserType(context: Context, userType: String?) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putString("user_type", userType)
            editor.apply()
        }

        fun getUserEmail(context: Context): String {
            var userid = ""
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            userid = prefs.getString("user_email", "").toString()
            return userid
        }

        fun setUserEmail(context: Context, userid: String?) {
            val prefs = context.getSharedPreferences(
                PREFSNAME,
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putString("user_email", userid)
            editor.commit()
        }


    }
}