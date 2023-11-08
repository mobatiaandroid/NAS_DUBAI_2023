package com.mobatia.naisapp.activity.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.mobatia.naisapp.R
import com.mobatia.naisapp.activity.login.Login
import com.mobatia.naisapp.activity.tutorial.adapter.TutorialViewPagerAdapter
import com.mobatia.naisapp.constant.PreferenceManager

class Tutorial : AppCompatActivity() {
    lateinit var context: Context
    lateinit var tutorialViewPager: ViewPager
    lateinit var imageSkip: ImageView
    lateinit var linearLayout: LinearLayout
    lateinit var indicatorCircle: Array<ImageView?>
    var bannerarray = ArrayList<Int>()
    var dataType: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        context = this

        bannerarray.add(R.drawable.tut_1)
        bannerarray.add(R.drawable.tut_2)
        bannerarray.add(R.drawable.tut_3)
        bannerarray.add(R.drawable.tut_4)
        bannerarray.add(R.drawable.tut_6)
        bannerarray.add(R.drawable.tut_7)
        initialiseUI()
    }

    private fun initialiseUI() {
        tutorialViewPager = findViewById(R.id.tutorialViewPager)
        imageSkip = findViewById(R.id.imageSkip)
        linearLayout = findViewById(R.id.linear)
        indicatorCircle = arrayOfNulls(bannerarray.size)
        val mTutorialViewPagerAdapter = TutorialViewPagerAdapter(context, bannerarray)
        tutorialViewPager.currentItem = 0
        tutorialViewPager.adapter = mTutorialViewPagerAdapter
        addShowCountView(0)
        imageSkip.setOnClickListener {
            if (PreferenceManager.getIsFirstLaunch(context)) {
                startActivity(Intent(this, Login::class.java))
                PreferenceManager.setIsFirstLaunch(context, false)
                finish()
            } else {
                startActivity(Intent(this, Login::class.java))
                PreferenceManager.setIsFirstLaunch(context, false)
                finish()

            }

        }
        tutorialViewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                for (i in bannerarray.indices) {
                    indicatorCircle[i]!!.setBackgroundDrawable(
                        resources
                            .getDrawable(R.drawable.blackround)
                    )
                }
                if (position < bannerarray.size) {
                    indicatorCircle[position]!!.setBackgroundDrawable(
                        resources
                            .getDrawable(R.drawable.redround)
                    )
                    linearLayout.removeAllViews()
                    addShowCountView(position)
                } else {
                    linearLayout.removeAllViews()
                    finish()
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
        tutorialViewPager.adapter!!.notifyDataSetChanged()
    }

    private fun addShowCountView(position: Int) {
        for (i in bannerarray.indices) {
            indicatorCircle[i] = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(
                resources
                    .getDimension(R.dimen.home_circle_width).toInt(), resources.getDimension(
                    R.dimen.home_circle_height
                ).toInt()
            )
            indicatorCircle[i]!!.layoutParams = layoutParams
            if (i == position) {
                indicatorCircle[i]!!.setBackgroundResource(R.drawable.redround)
            } else {
                indicatorCircle[i]!!.setBackgroundResource(R.drawable.blackround)
            }
            linearLayout.addView(indicatorCircle[i])
        }
    }
}