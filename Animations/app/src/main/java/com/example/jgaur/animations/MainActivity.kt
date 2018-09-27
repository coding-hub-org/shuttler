package com.example.jgaur.animations

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Animation.AnimationListener {

    lateinit var mRotateAnim: Animation
    lateinit var mScaleAnim: Animation
    lateinit var mTranslateAnim: Animation
    lateinit var mAlphaAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAlpha.setOnClickListener { alphaAnimation() }
        buttonRotate.setOnClickListener { rotateAnimation() }
        buttonTranslate.setOnClickListener { translateAnimation() }
        buttonScale.setOnClickListener { scaleAnimation() }
    }

    private fun alphaAnimation() {
        mAlphaAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        mAlphaAnim.setAnimationListener(this)
        textView.startAnimation(mAlphaAnim)
    }

    private fun rotateAnimation() {
        mRotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        mRotateAnim.setAnimationListener(this)
        textView.startAnimation(mRotateAnim)
    }

    private fun translateAnimation() {
        mTranslateAnim = AnimationUtils.loadAnimation(this, R.anim.translate)
        mTranslateAnim.setAnimationListener(this)
        textView.startAnimation(mTranslateAnim)
    }

    private fun scaleAnimation() {
        mScaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale)
        mScaleAnim.setAnimationListener(this)
        textView.startAnimation(mScaleAnim)
    }

    override fun onAnimationEnd(p0: Animation?) {
        if(p0 == mTranslateAnim){
            Toast.makeText(this, "ended translate", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAnimationStart(p0: Animation?) {
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }
}
