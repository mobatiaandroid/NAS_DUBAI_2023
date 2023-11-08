package com.mobatia.naisapp.activity.home

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.naisapp.R
import com.mobatia.naisapp.activity.home.adapter.HomeListAdapter
import com.mobatia.naisapp.constant.DragShadowBuilder
import com.mobatia.naisapp.constant.PreferenceManager
import com.mobatia.naisapp.custom_view.ProgressBarDialog
import com.mobatia.naisapp.fragment.home.HomeFragment
import com.mobatia.naisapp.fragment.home.HomeGuestFragment
import com.mobatia.naisapp.fragment.settings.SettingsFragment

class Home : AppCompatActivity(), OnItemLongClickListener {
    lateinit var context: Context
    lateinit var progressBarDialog: ProgressBarDialog
    private lateinit var fragment: Fragment

    private lateinit var calendarPermissionStatus: SharedPreferences
    private lateinit var externalStoragePermissionStatus: SharedPreferences
    private lateinit var locationPermissionStatus: SharedPreferences
    private lateinit var accessNotificationPermissionStatus: SharedPreferences
    private val manager = supportFragmentManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var extras: Bundle? = null
    private var notificationReceived = 0

    var linearLayout: LinearLayout? = null
    private val mHomeListView: ListView? = null

    //    var mListAdapter: HomeListAdapter? = null
//    private val mDrawerToggle: ActionBarDrawerToggle? = null
    private val mDrawerLayout: DrawerLayout? = null

    //    private val mListItemArray: Array<String> = ArrayList()
    private val mDetector: GestureDetector? = null
    var downarrow: ImageView? = null
    private val preLast = 0
    var notificationRecieved = 0
    var imageButton: ImageView? = null
    private val PERMISSION_CALLBACK_CONSTANT_EXTERNAL_STORAGE = 2
    private val PERMISSION_CALLBACK_CONSTANT_LOCATION = 3
    private val PERMISSION_CALLBACK_ACCESS_NOTIFICATION = 4
    private val REQUEST_PERMISSION_EXTERNAL_STORAGE = 102
    private val REQUEST_PERMISSION_LOCATION = 103
    private val REQUEST_PERMISSION_ACCESS_NOTIFICATION = 104


    var permissionsRequiredExternalStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var permissionsRequiredLocation = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var permissionsRequiredNotification = arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY)

    private var externalStorageToSettings = false
    private var locationToSettings = false
    var tabPositionProceed = 0
    var dialogCal: Dialog? = null
    var imageButton2: ImageView? = null

    //    var studentEnrollList: ArrayList<StudentEnrollList>? = null
//    var reEnrollSaveArray: ArrayList<ReEnrollSubmitModel>? = null
//    var studentList: ArrayList<StudentEnrollList> = ArrayList<StudentEnrollList>()
    var reEnrollRecycler: RecyclerView? = null
    var selectedItem: String? = null
    var text_content: TextView? = null
    var text_dialog: TextView? = null
    lateinit var navigation_menu: ImageView
    lateinit var settings_icon: ImageView
    lateinit var shadowBuilder: DragShadowBuilder
    lateinit var mActivity: Activity

    lateinit var clipData: ClipData
    lateinit var navigationMenuItemNameArray: Array<String>
    lateinit var navigationMenuItemImageArray: TypedArray
    lateinit var navigationMenuItemNameArrayGuest: Array<String>
    lateinit var navigationMenuItemImageArrayGuest: TypedArray
    lateinit var linear_layout: LinearLayout
    lateinit var drawer_layout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var logoClickImgView: ImageView
    lateinit var homelist: ListView
    var sPosition: Int = 0
    private val PERMISSION_CALLBACK_CONSTANT_CALENDAR = 1
    private val REQUEST_PERMISSION_CALENDAR = 101
    var permissionsRequiredCalendar = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
    private var calendarToSettings = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        context = this
        Intent.FLAG_ACTIVITY_CLEAR_TASK
        initialiseUI()
        calendarPermissionStatus =
            getSharedPreferences("calendarPermissionStatus", Context.MODE_PRIVATE)
        if (PreferenceManager.getAccessToken(context).equals("")) {
            showFragmentHome()
        } else {
            showFragmentHomeGuest()
        }

    }

    private fun showFragmentHome() {
        val transaction = manager.beginTransaction()
        val fragment = HomeFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    private fun showFragmentHomeGuest() {
        val transaction = manager.beginTransaction()
        val fragment = HomeFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    private fun initialiseUI() {
        homelist = findViewById(R.id.homelistview)
        drawer_layout = findViewById(R.id.drawer_layout)
        linear_layout = findViewById(R.id.linear_layout)
        var downarrow = findViewById<ImageView>(R.id.downarrow)

        navigationMenuItemNameArray =
            applicationContext.resources.getStringArray(R.array.navigation_item_names)
        navigationMenuItemImageArray =
            applicationContext.resources.obtainTypedArray(R.array.navigation_item_icons)
        navigationMenuItemNameArrayGuest =
            applicationContext.resources.getStringArray(R.array.navigation_item_names_guest)
        navigationMenuItemImageArrayGuest =
            applicationContext.resources.obtainTypedArray(R.array.navigation_item_icons_guest)

        val width = (resources.displayMetrics.widthPixels / 1.7).toInt()
        val params = linear_layout
            .layoutParams as DrawerLayout.LayoutParams
        params.width = width
        linear_layout.layoutParams = params
        if (!PreferenceManager.getAccessToken(context).equals("")) {
            val homeListAdapter =
                HomeListAdapter(this, navigationMenuItemNameArray, navigationMenuItemImageArray!!)
            homelist.adapter = homeListAdapter
        } else {
            val homeListAdapter =
                HomeListAdapter(
                    this,
                    navigationMenuItemNameArrayGuest,
                    navigationMenuItemImageArrayGuest!!
                )
            homelist.adapter = homeListAdapter
        }
        homelist.onItemLongClickListener = this

        askForNotificationPermission()

        homelist.setOnItemClickListener { adapterView, view, position, id ->
            val itemAtPos = adapterView.getItemAtPosition(position)
            val itemIdAtPos = adapterView.getItemIdAtPosition(position)

            if (PreferenceManager.getAccessToken(context).equals("")) {
                when (position) {
                    0 -> {
                        fragment = HomeGuestFragment()
                        replaceFragmentsSelected(position)
                    }

                    1 -> {
                        /** Communications**/
                        // TODO
                    }

                    2 -> {
                        /** Parent Essential**/
                        // TODO

                    }

                    3 -> {
                        /** Early Years**/
                        // TODO

                    }

                    4 -> {
                        /** Primary**/
                        // TODO

                    }

                    5 -> {
                        /** Secondary**/
                        // TODO


                    }

                    6 -> {
                        /** Sixth Form**/
                        // TODO

                    }

                    7 -> {
                        /** Performing Arts**/
                        // TODO
                    }

                    8 -> {
                        /** NAE Programmes**/
                        // TODO
                    }

                    9 -> {
                        /** About Us**/
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CALL_PHONE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            checkPermission()


                        } else {
                            // TODO
                            /*fragment = ContactUsFragment()
                            replaceFragmentsSelected(position)*/
                        }
                    }

                    10 -> {
                        /** Contact Us**/
                        // TODO
                        /*fragment = AboutUsFragment()
                        replaceFragmentsSelected(position)*/

                    }
                }
            } else {
//                when (position) {
//                    0 -> {
//                        fragment = HomeFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    1 -> {
//                        //Notification
//                        fragment = NotificationFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    2 -> {
//                        //Calendar
//                        fragment = CalendarFragment()
//
//                        if (ActivityCompat.checkSelfPermission(
//                                mActivity,
//                                permissionsRequiredCalendar.get(0)
//                            ) != PackageManager.PERMISSION_GRANTED
//                            || ActivityCompat.checkSelfPermission(
//                                mActivity,
//                                permissionsRequiredCalendar.get(1)
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                                    mActivity,
//                                    permissionsRequiredCalendar.get(0)
//                                )
//                                || ActivityCompat.shouldShowRequestPermissionRationale(
//                                    mActivity,
//                                    permissionsRequiredCalendar.get(1)
//                                )
//                            ) {
//                                //Show information about why you need the permission
//                                val builder = AlertDialog.Builder(mActivity)
//                                builder.setTitle("Need Calendar Permission")
//                                builder.setMessage("This module needs Calendar permissions.")
//                                builder.setCancelable(false)
//                                builder.setPositiveButton(
//                                    "Grant"
//                                ) { dialog, which ->
//                                    dialog.cancel()
//                                    ActivityCompat.requestPermissions(
//                                        mActivity,
//                                        permissionsRequiredCalendar,
//                                        PERMISSION_CALLBACK_CONSTANT_CALENDAR
//                                    )
//                                }
//                                builder.setNegativeButton(
//                                    "Cancel"
//                                ) { dialog, which -> dialog.cancel() }
//                                builder.show()
//                            } else if (calendarPermissionStatus.getBoolean(
//                                    permissionsRequiredCalendar.get(0),
//                                    false
//                                )
//                            ) {
//                                //Previously Permission Request was cancelled with 'Dont Ask Again',
//                                // Redirect to Settings after showing information about why you need the permission
//                                println("Permission0")
//                                val builder = AlertDialog.Builder(mActivity)
//                                builder.setTitle("Need Calendar Permission")
//                                builder.setMessage("This module needs Calendar permissions.")
//                                builder.setCancelable(false)
//                                builder.setPositiveButton(
//                                    "Grant"
//                                ) { dialog, which ->
//                                    dialog.cancel()
//                                    calendarToSettings = true
//                                    val intent =
//                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                    val uri = Uri.fromParts(
//                                        "package",
//                                        mActivity.getPackageName(),
//                                        null
//                                    )
//                                    intent.data = uri
//                                    startActivityForResult(
//                                        intent,
//                                        HomeActivity().REQUEST_PERMISSION_CALENDAR
//                                    )
//                                    Toast.makeText(
//                                        mContext,
//                                        "Go to settings and grant access to calendar",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//                                builder.setNegativeButton(
//                                    "Cancel"
//                                ) { dialog, which ->
//                                    dialog.cancel()
//                                    calendarToSettings = false
//                                }
//                                builder.show()
//                            } else if (calendarPermissionStatus.getBoolean(
//                                    permissionsRequiredCalendar.get(1),
//                                    false
//                                )
//                            ) {
//                                //Previously Permission Request was cancelled with 'Dont Ask Again',
//                                // Redirect to Settings after showing information about why you need the permission
//                                println("Permission1")
//                                val builder = AlertDialog.Builder(mActivity)
//                                builder.setTitle("Need Calendar Permission")
//                                builder.setMessage("This module needs Calendar permissions.")
//                                builder.setCancelable(false)
//                                builder.setPositiveButton(
//                                    "Grant"
//                                ) { dialog, which ->
//                                    dialog.cancel()
//                                    calendarToSettings = true
//                                    val intent =
//                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                    val uri = Uri.fromParts(
//                                        "package",
//                                        mActivity.getPackageName(),
//                                        null
//                                    )
//                                    intent.data = uri
//                                    startActivityForResult(
//                                        intent,
//                                        HomeActivity().REQUEST_PERMISSION_CALENDAR
//                                    )
//                                    Toast.makeText(
//                                        mContext,
//                                        "Go to settings and grant access to calendar",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//                                builder.setNegativeButton(
//                                    "Cancel"
//                                ) { dialog, which ->
//                                    dialog.cancel()
//                                    calendarToSettings = false
//                                }
//                                builder.show()
//                            } else {
//                                println("Permission3")
//
//                                //just request the permission
////                        ActivityCompat.requestPermissions(mActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT_CALENDAR);
//                                ActivityCompat.requestPermissions(
//                                    mActivity,
//                                    permissionsRequiredCalendar,
//                                    HomeActivity().PERMISSION_CALLBACK_CONSTANT_CALENDAR
//                                )
//                            }
//                            val editor: SharedPreferences.Editor =
//                                calendarPermissionStatus.edit()
//                            editor.putBoolean(permissionsRequiredCalendar.get(0), true)
//                            editor.commit()
//                        } else {
//                            replaceFragmentsSelected(position)
//                        }
//                    }
//
//                    3 -> {
//                        //About Us
//                        fragment = PaymentFragment()
//                        replaceFragmentsSelected(position)
//
//
//                    }
//
//                    4 -> {
//                        //payment
//                        fragment = CanteenFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    5 -> {
//
//                        fragment = ParentsEssentialFragment()
//                        replaceFragmentsSelected(position)
////                        DialogFunctions.commonErrorAlertDialog("Coming Soon!","This Feature will be available shortly",
////                            mContext
////                        )
//                    }
//
//                    6 -> {
//                        PreferenceManager.setStudentID(mContext, "")
//                        fragment = AbsenceFragment()
//                        replaceFragmentsSelected(position)
////                        DialogFunctions.commonErrorAlertDialog("Coming Soon!","This Feature will be available shortly",
////                            mContext
////                        )
//                    }
//
//                    7 -> {
//                        //Early years
//                        fragment = EarlyYearsFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    8 -> {
//                        //Primary
//                        fragment = PrimaryFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    9 -> {
//                        //Secondary
//                        fragment = SecondaryFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    10 -> {
//                        //Reports
//                        PreferenceManager.setStudentID(context, "")
//                        fragment = ReportsFragment()
//                        replaceFragmentsSelected(position)
//
//
//                    }
//
//                    11 -> {
//                        //Permission_form
//                        PreferenceManager.setStudentID(context, "")
//                        fragment = PermissionSlipFragment()
//                        replaceFragmentsSelected(position)
//
//                    }
//
//                    12 -> {
//                        //CCa
//                        fragment = CCAFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    13 -> {
//                        //Parents meeting
//                        fragment = ParentMeetingsFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    14 -> {
//                        //Gallery
//                        fragment = GalleryFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//                    15 -> {
//                        //About Us
//
//
//                        if (ActivityCompat.checkSelfPermission(
//                                context,
//                                Manifest.permission.ACCESS_FINE_LOCATION
//                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                                context,
//                                Manifest.permission.ACCESS_COARSE_LOCATION
//                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                                context,
//                                Manifest.permission.CALL_PHONE
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            checkPermission()
//
//
//                        } else {
//                            fragment = ContactUsFragment()
//                            replaceFragmentsSelected(position)
//                        }
//                    }
//
//                    16 -> {
//                        // Contact Us
//                        fragment = AboutUsFragment()
//                        replaceFragmentsSelected(position)
//                    }
//
//
//                }
            }


        }

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.layout_custom_titlebar)
        supportActionBar!!.elevation = 0F
        var view = supportActionBar!!.customView
        toolbar = view.parent as Toolbar
        toolbar.setBackgroundColor(resources.getColor(R.color.white))
        toolbar.setContentInsetsAbsolute(0, 0)

        navigation_menu = view.findViewById(R.id.action_bar_back)
        settings_icon = view.findViewById(R.id.action_bar_forward)
        logoClickImgView = view.findViewById(R.id.logoClickImgView)
        settings_icon.visibility = View.VISIBLE
        homelist.setBackgroundColor(getColor(R.color.split_bg))
        homelist.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (view.id == homelist.id) {
                    val currentFirstVisibleItem: Int = homelist.lastVisiblePosition

                    if (currentFirstVisibleItem == totalItemCount - 1) {
                        downarrow.visibility = View.INVISIBLE
                    } else {
                        downarrow.visibility = View.VISIBLE
                    }
                }
            }
        })
        navigationMenuItemNameArray =
            context.resources.getStringArray(R.array.navigation_item_names)
        navigationMenuItemImageArray =
            context.resources.obtainTypedArray(R.array.navigation_item_icons)
        navigation_menu.setOnClickListener {
            if (drawer_layout.isDrawerOpen(linear_layout)) {
                drawer_layout.closeDrawer(linear_layout)
            } else {
                drawer_layout.openDrawer(linear_layout)
            }
        }

        logoClickImgView.setOnClickListener(View.OnClickListener {
            settings_icon.visibility = View.VISIBLE
            if (drawer_layout.isDrawerOpen(linear_layout)) {
                drawer_layout.closeDrawer(linear_layout)
            }
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            fragment = HomeFragment()
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        })



        settings_icon.setOnClickListener {
            val fm = supportFragmentManager

            if (drawer_layout.isDrawerOpen(linear_layout)) {
                drawer_layout.closeDrawer(linear_layout)
            }
            fragment = SettingsFragment()
            if (fragment != null) {
                val fragmentManager =
                    supportFragmentManager
                fragmentManager.beginTransaction()
                    .add(R.id.fragment_holder, fragment!!, "Settings")
                    .addToBackStack("Settings").commit()

                supportActionBar!!.setTitle(R.string.null_value)
                settings_icon.visibility = View.INVISIBLE

            }
        }

    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawer_layout.isDrawerOpen(linear_layout)) {
            drawer_layout.closeDrawer(linear_layout)
        }
        settings_icon.visibility = View.VISIBLE

    }


    private fun replaceFragmentsSelected(position: Int) {
        settings_icon.visibility = View.VISIBLE
        if (fragment != null) {
            val fragmentManager = supportFragmentManager

            var itemListArray: Array<String>? =
                if (PreferenceManager.getAccessToken(context)!!.equals("")) {
                    navigationMenuItemNameArrayGuest
                } else {
                    navigationMenuItemNameArray
                }
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_holder, fragment!!,
                    itemListArray!![position]
                )
                .addToBackStack(itemListArray[position]).commitAllowingStateLoss()
            homelist.setItemChecked(position, true)
            homelist.setSelection(position)
            supportActionBar!!.setTitle(R.string.null_value)
            if (drawer_layout.isDrawerOpen(linear_layout)) {
                drawer_layout.closeDrawer(linear_layout)
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
//            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE
//                    ,
//                    Manifest.permission.ACCESS_NOTIFICATION_POLICY
                ),
                123
            )
        }
    }


    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {

        shadowBuilder = DragShadowBuilder(view)
        sPosition = position
        val selecteditem = parent?.getItemIdAtPosition(position)
        view?.setBackgroundColor(Color.parseColor("#47C2D1"))
        val data = ClipData.newPlainText("", "")
        view?.startDrag(data, shadowBuilder, view, 0)
        view!!.visibility = View.VISIBLE
        drawer_layout.closeDrawer(linear_layout)
        return false
    }


}