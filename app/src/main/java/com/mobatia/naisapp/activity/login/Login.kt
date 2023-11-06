package com.mobatia.naisapp.activity.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.BuildConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.mobatia.naisapp.R
import com.mobatia.naisapp.activity.home.Home
import com.mobatia.naisapp.activity.login.model.LoginResponseModel
import com.mobatia.naisapp.api.APIClient
import com.mobatia.naisapp.constant.AppUtils
import com.mobatia.naisapp.constant.PreferenceManager
import com.mobatia.naisapp.custom_view.ProgressBarDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var context: Context
    lateinit var userNameEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var forgotPasswordButton: Button
    lateinit var guestUserButton: Button
    lateinit var loginButton: Button
    lateinit var signUpButton: Button
    lateinit var inputEmailEditText: EditText
    lateinit var emailHelpButton: Button
    lateinit var staffButton: Button
    lateinit var progressBarDialog: ProgressBarDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        PreferenceManager.setStaffID(context, "")
        initialiseUI()
    }

    private fun initialiseUI() {
        userNameEditText = findViewById<View>(R.id.userEditText) as EditText
        userNameEditText.setOnEditorActionListener { v, actionId, event ->
            userNameEditText.isFocusable = false
            userNameEditText.isFocusableInTouchMode = false
            false
        }
        passwordEditText = findViewById<View>(R.id.passwordEditText) as EditText
        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            passwordEditText.isFocusable = false
            passwordEditText.isFocusableInTouchMode = false
            false
        }
        emailHelpButton = findViewById<View>(R.id.helpButton) as Button
        forgotPasswordButton = findViewById<View>(R.id.forgotPasswordButton) as Button
        guestUserButton = findViewById<View>(R.id.guestButton) as Button
        loginButton = findViewById<View>(R.id.loginBtn) as Button
        signUpButton = findViewById<View>(R.id.signUpButton) as Button
        staffButton = findViewById<View>(R.id.staffBtn) as Button
        progressBarDialog = ProgressBarDialog(context)

        userNameEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                userNameEditText.isFocusable = false
                userNameEditText.isFocusableInTouchMode = false
                false
            } else {
                userNameEditText.isFocusable = false
                userNameEditText.isFocusableInTouchMode = false
                false
            }
        }

        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passwordEditText.isFocusable = false
                passwordEditText.isFocusableInTouchMode = false
                false
            } else {
                userNameEditText.isFocusable = false
                userNameEditText.isFocusableInTouchMode = false
                false
            }
        }

        loginButton.setOnClickListener {

            AppUtils.hideKeyboard(context, passwordEditText)
            val validationStatus = loginValidation(
                userNameEditText.text.toString(),
                passwordEditText.text.toString(),
                context
            )
            if (validationStatus) {
                if (AppUtils.isInternetConnected(context)) {
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener { task ->
                            if (task.isComplete) {
                                val token: String? = task.result
                                callLoginAPI(
                                    userNameEditText.text.trim().toString(),
                                    passwordEditText.text.trim().toString(),
                                    token.toString()
                                )
                            }
                        }
                } else {
                    AppUtils.showAlertDialogInternetFailure(context)
                }
            }
        }

        signUpButton.setOnClickListener {

        }

        forgotPasswordButton.setOnClickListener {

        }

        emailHelpButton.setOnClickListener {

        }

        guestUserButton.setOnClickListener {

        }

    }

    private fun callLoginAPI(userName: String, password: String, token: String) {
        val deviceBrand = Build.MANUFACTURER
        val deviceModel = Build.MODEL
        val osVersion = Build.VERSION.RELEASE
        val devicename = "$deviceBrand $deviceModel $osVersion"
        val version: String = BuildConfig.VERSION_NAME
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        progressBarDialog.show()
        val call: Call<LoginResponseModel> = APIClient.getClient.getLogin(
            "password",
            "testclient",
            "testpass",
            userName,
            userName,
            password,
            token,
            "2",
            devicename,
            version,
            androidId
        )
        call.enqueue(object : Callback<LoginResponseModel> {
            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                progressBarDialog.show()
                AppUtils.showAlertDialogSingleButton(
                    resources.getString(R.string.alert_heading),
                    resources.getString(R.string.common_error),
                    context
                )
            }

            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: Response<LoginResponseModel>
            ) {
                progressBarDialog.show()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val responseData: LoginResponseModel.Response = apiResponse!!.response!!
                    val statuscode: String = apiResponse.response!!.statuscode!!
                    if (statuscode == "303") {
                        PreferenceManager.setAccessToken(
                            context,
                            apiResponse.response!!.responseArray!!.token
                        )
                        PreferenceManager.setUserID(
                            context,
                            apiResponse.response!!.responseArray!!.userid
                        )
                        PreferenceManager.setUserType(context, "1")
                        PreferenceManager.setUserEmail(
                            context,
                            userNameEditText.getText().toString()
                        )
                        showAlertLoginSuccess(
                            context,
                            "Success",
                            getString(R.string.login_success_alert),
                            R.drawable.tick,
                            R.drawable.round
                        )
                    } else if (statuscode.equals("301", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.missing_parameter),
                            context
                        )
                    } else if (statuscode.equals("304", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.email_exists),
                            context
                        )
                    } else if (statuscode.equals("305", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.incrct_usernamepswd),
                            context
                        )
                    } else if (statuscode.equals("306", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.invalid_email),
                            context
                        )
                    } else if (statuscode.equals("707", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.user_expired),
                            context
                        )
                    } else {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.common_error),
                            context
                        )
                    }
                } else {
                    AppUtils.showAlertDialogSingleButton(
                        getString(R.string.error_heading),
                        getString(R.string.common_error),
                        context
                    )
                }
            }
        })
    }

    private fun showAlertLoginSuccess(
        context: Context,
        head: String,
        message: String,
        tick: Int,
        round: Int
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialog_ok_layout)
        val icon = dialog.findViewById<View>(R.id.iconImageView) as ImageView
        icon.setBackgroundResource(round)
        icon.setImageResource(tick)
        val text = dialog.findViewById<View>(R.id.text_dialog) as TextView
        val textHead = dialog.findViewById<View>(R.id.alertHead) as TextView
        text.text = message
        textHead.text = head

        val dialogButton = dialog.findViewById<View>(R.id.btn_Ok) as Button
        dialogButton.setOnClickListener {
            dialog.dismiss()
            //				Intent homeIntent = new Intent(mContext, HomeListActivity.class);
            val homeIntent = Intent(context, Home::class.java)
            startActivity(homeIntent)
            finish()
        }

        dialog.show()
    }

    private fun loginValidation(userName: String, password: String, context: Context): Boolean {
        if (userName == "") {
            AppUtils.showAlertDialogSingleButton(
                context.resources.getString(R.string.alert_heading), context.resources.getString(
                    R.string.enter_email
                ), context
            )
            return false
        } else {
            if (!AppUtils.isEmailValid(userName)) {
                AppUtils.showAlertDialogSingleButton(
                    context.resources.getString(R.string.alert_heading),
                    context.resources.getString(
                        R.string.enter_valid_email
                    ),
                    context
                )
                return false

            } else {
                return if (password == "") {
                    AppUtils.showAlertDialogSingleButton(
                        context.resources.getString(R.string.alert_heading),
                        context.resources.getString(
                            R.string.enter_password
                        ),
                        context
                    )
                    false

                } else {
                    true

                }
            }
        }
    }
}