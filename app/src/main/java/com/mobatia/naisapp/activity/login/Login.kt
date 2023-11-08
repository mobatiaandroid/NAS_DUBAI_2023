package com.mobatia.naisapp.activity.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
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
import com.mobatia.naisapp.constant.GeneralSubmitResponseModel
import com.mobatia.naisapp.constant.PreferenceManager
import com.mobatia.naisapp.custom_view.ProgressBarDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity(), View.OnTouchListener {

    private lateinit var context: Context
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var forgotPasswordButton: Button
    private lateinit var guestUserButton: Button
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var inputEmailEditText: EditText
    private lateinit var emailHelpButton: Button
    private lateinit var staffButton: Button
    lateinit var progressBarDialog: ProgressBarDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        PreferenceManager.setStaffID(context, "")
        initialiseUI()
    }

    private fun initialiseUI() {
        progressBarDialog = ProgressBarDialog(context)
        userNameEditText = findViewById<View>(R.id.userEditText) as EditText
        userNameEditText.setOnEditorActionListener { _, _, _ ->
            userNameEditText.isFocusable = false
            userNameEditText.isFocusableInTouchMode = false
            false
        }
        passwordEditText = findViewById<View>(R.id.passwordEditText) as EditText
        passwordEditText.setOnEditorActionListener { _, _, _ ->
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

        userNameEditText.setOnTouchListener(this)
        passwordEditText.setOnTouchListener(this)

        userNameEditText.setOnEditorActionListener { _, actionId, _ ->
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

        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
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
            showSignUpDialogAlert()
        }

        forgotPasswordButton.setOnClickListener {
            showForgotPasswordDialogAlert()
        }

        emailHelpButton.setOnClickListener {
            showEmailHelpDialogAlert()
        }

        guestUserButton.setOnClickListener {
            PreferenceManager.setUserID(context, "")
            startActivity(Intent(context, Home::class.java))
        }

    }


    private fun showEmailHelpDialogAlert() {

        val intent = Intent(Intent.ACTION_SEND)
        val recipients = arrayOf("communications@nasdubai.ae")
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.type = "text/html"
        intent.setPackage("com.google.android.gm")
        context.startActivity(Intent.createChooser(intent, "Send mail"))

    }

    private fun showForgotPasswordDialogAlert() {
        val dialog = Dialog(context, R.style.NewDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_forgot_password)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        Build.VERSION.SDK_INT
        inputEmailEditText = dialog.findViewById<View>(R.id.text_dialog) as EditText
        inputEmailEditText.isFocusable = true
        inputEmailEditText.isFocusableInTouchMode = true
        inputEmailEditText.setOnTouchListener(this)
        dialog.findViewById<View>(R.id.alertHead) as TextView
        val dialogSubmitButton = dialog
            .findViewById<View>(R.id.btn_signup) as Button

        dialogSubmitButton.setOnClickListener {
            AppUtils.hideKeyboard(context, inputEmailEditText)
            if (!inputEmailEditText.text.toString().trim { it <= ' ' }
                    .equals("", ignoreCase = true)) {
                if (AppUtils.isEmailValid(
                        inputEmailEditText.text
                            .toString()
                    )
                ) {
                    if (AppUtils.isInternetConnected(context)) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isComplete) {
                                val token = task.result
                                callForgotPasswordAPI(
                                    inputEmailEditText.text.trim().toString(),
                                    token
                                )
                            }
                        }
                    } else {
                        AppUtils.showAlertDialogInternetFailure(
                            context
                        )
                    }
                    dialog.dismiss()
                } else {
                    AppUtils.showAlertDialogSingleButton(
                        resources.getString(R.string.alert_heading),
                        resources.getString(R.string.invalid_email),
                        context
                    )

                }
            } else {
                AppUtils.showAlertDialogSingleButton(
                    resources.getString(R.string.alert_heading),
                    resources.getString(R.string.enter_email),
                    context
                )

            }
        }

        val negativeButton = dialog.findViewById<View>(R.id.btn_maybelater) as Button
        negativeButton.setOnClickListener {
            AppUtils.hideKeyboard(context, inputEmailEditText)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun callForgotPasswordAPI(emailID: String, token: String?) {
        progressBarDialog.show()
        val call: Call<GeneralSubmitResponseModel> = APIClient.getClient.forgotPassword(
            emailID,
            token,
            "2",
        )
        call.enqueue(object : Callback<GeneralSubmitResponseModel> {
            override fun onFailure(call: Call<GeneralSubmitResponseModel>, t: Throwable) {
                progressBarDialog.dismiss()
                AppUtils.showAlertDialogSingleButton(
                    resources.getString(R.string.alert_heading),
                    resources.getString(R.string.common_error),
                    context
                )
            }

            override fun onResponse(
                call: Call<GeneralSubmitResponseModel>,
                response: Response<GeneralSubmitResponseModel>
            ) {
                progressBarDialog.dismiss()
                if (response.body()!!.responseCode.equals("200", ignoreCase = true)) {
                    val statusCode = response.body()!!.response!!.statusCode
                    if (statusCode.equals("303", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.success),
                            resources.getString(R.string.frgot_success_alert),
                            context
                        )
                    } else if (statusCode.equals("301", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.error_heading),
                            resources.getString(R.string.missing_parameter),
                            context
                        )
                    } else if (statusCode.equals("304", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.error_heading),
                            resources.getString(R.string.email_exists),
                            context
                        )
                    } else if (statusCode.equals("306", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.error_heading),
                            resources.getString(R.string.invalid_email),
                            context
                        )
                    } else if (statusCode.equals("305", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.error_heading),
                            resources.getString(R.string.incrct_usernamepswd),
                            context
                        )
                    }
                } else {
                    AppUtils.showAlertDialogSingleButton(
                        resources.getString(R.string.alert_heading),
                        resources.getString(R.string.common_error),
                        context
                    )
                }
            }
        })
    }

    private fun showSignUpDialogAlert() {

        val dialog = Dialog(context, R.style.NewDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert_signup)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        inputEmailEditText = dialog.findViewById<View>(R.id.text_dialog) as EditText
        inputEmailEditText.isFocusable = true
        inputEmailEditText.isFocusableInTouchMode = true
        inputEmailEditText.setOnTouchListener(this)

        dialog.findViewById<View>(R.id.alertHead) as TextView
        val dialogSubmitButton = dialog
            .findViewById<View>(R.id.btn_signup) as Button

        dialogSubmitButton.setOnClickListener {
            AppUtils.hideKeyboard(context, inputEmailEditText)
            if (!inputEmailEditText.text.toString().trim { it <= ' ' }
                    .equals("", ignoreCase = true)) {
                if (AppUtils.isEmailValid(
                        inputEmailEditText.text
                            .toString()
                    )
                ) {
                    if (AppUtils.isInternetConnected(context)) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isComplete) {
                                val token = task.result
                                callSignUpAPI(inputEmailEditText.text.trim().toString(), token)
                            }
                        }
                    } else {
                        AppUtils.showAlertDialogInternetFailure(context)
                    }
                    dialog.dismiss()
                } else {

                    AppUtils.showAlertDialogSingleButton(
                        resources.getString(R.string.alert_heading),
                        resources.getString(R.string.enter_valid_email),
                        context
                    )

                }
            } else {
                AppUtils.showAlertDialogSingleButton(
                    resources.getString(R.string.alert_heading),
                    resources.getString(R.string.enter_email),
                    context
                )

            }
        }

        val negativeButton = dialog.findViewById<View>(R.id.btn_maybelater) as Button
        negativeButton.setOnClickListener {
            AppUtils.hideKeyboard(context, inputEmailEditText)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun callSignUpAPI(emailID: String, token: String?) {
        progressBarDialog.show()
        val call: Call<GeneralSubmitResponseModel> = APIClient.getClient.signUp(
            emailID,
            token,
            "2",
        )
        call.enqueue(object : Callback<GeneralSubmitResponseModel> {
            override fun onFailure(call: Call<GeneralSubmitResponseModel>, t: Throwable) {
                progressBarDialog.dismiss()
                AppUtils.showAlertDialogSingleButton(
                    resources.getString(R.string.alert_heading),
                    resources.getString(R.string.common_error),
                    context
                )
            }

            override fun onResponse(
                call: Call<GeneralSubmitResponseModel>,
                response: Response<GeneralSubmitResponseModel>
            ) {
                progressBarDialog.dismiss()
                if (response.body()!!.responseCode.equals("200", ignoreCase = true)) {
                    val statusCode = response.body()!!.response!!.statusCode
                    if (statusCode.equals("303", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.success),
                            resources.getString(R.string.signup_success_alert),
                            context
                        )
                    } else if (statusCode.equals("301", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.error_heading),
                            resources.getString(R.string.missing_parameter),
                            context
                        )
                    } else if (statusCode.equals("304", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.error_heading),
                            resources.getString(R.string.email_exists),
                            context
                        )
                    } else if (statusCode.equals("306", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            resources.getString(R.string.error_heading),
                            resources.getString(R.string.invalid_email),
                            context
                        )
                    }
                } else {
                    AppUtils.showAlertDialogSingleButton(
                        resources.getString(R.string.alert_heading),
                        resources.getString(R.string.common_error),
                        context
                    )
                }
            }
        })
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (view) {
            userNameEditText -> {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        userNameEditText.isFocusable = true
                        userNameEditText.isFocusableInTouchMode = true
                    }

                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        userNameEditText.isFocusable = true
                        userNameEditText.isFocusableInTouchMode = true
                    }
                }
            }

            passwordEditText -> {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        passwordEditText.isFocusable = true
                        passwordEditText.isFocusableInTouchMode = true
                    }

                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        passwordEditText.isFocusable = true
                        passwordEditText.isFocusableInTouchMode = true
                    }
                }
            }

            inputEmailEditText -> {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        userNameEditText.isFocusable = true
                        userNameEditText.isFocusableInTouchMode = true
                    }

                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        userNameEditText.isFocusable = true
                        userNameEditText.isFocusableInTouchMode = true
                    }
                }
            }
        }
        return false
    }


    private fun callLoginAPI(userName: String, password: String, token: String) {
        val deviceBrand = Build.MANUFACTURER
        val deviceModel = Build.MODEL
        val osVersion = Build.VERSION.RELEASE
        val deviceName = "$deviceBrand $deviceModel $osVersion"
        val version: String = BuildConfig.VERSION_NAME
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        progressBarDialog.show()
        val call: Call<LoginResponseModel> = APIClient.getClient.login(
            "password",
            "testclient",
            "testpass",
            userName,
            userName,
            password,
            token,
            "2",
            deviceName,
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
                    apiResponse!!.response!!
                    val statusCode: String = apiResponse.response!!.statuscode!!
                    if (statusCode == "303") {
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
                            userNameEditText.text.toString()
                        )
                        showAlertLoginSuccess(
                            context,
                            getString(R.string.success),
                            getString(R.string.login_success_alert),
                            R.drawable.tick,
                            R.drawable.round
                        )
                    } else if (statusCode.equals("301", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.missing_parameter),
                            context
                        )
                    } else if (statusCode.equals("304", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.email_exists),
                            context
                        )
                    } else if (statusCode.equals("305", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.incrct_usernamepswd),
                            context
                        )
                    } else if (statusCode.equals("306", ignoreCase = true)) {
                        AppUtils.showAlertDialogSingleButton(
                            getString(R.string.error_heading),
                            getString(R.string.invalid_email),
                            context
                        )
                    } else if (statusCode.equals("707", ignoreCase = true)) {
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