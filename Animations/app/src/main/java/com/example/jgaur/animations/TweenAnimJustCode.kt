package com.example.jgaur.animations

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class TweenAnimJustCode : AppCompatActivity(), Animation.AnimationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            buttonAlpha.setOnClickListener { alphaAnimation() }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        buttonRotate.setOnClickListener { rotateAnimation() }
        buttonTranslate.setOnClickListener { translateAnimation() }
        buttonScale.setOnClickListener { scaleAnimation() }
    }

    private fun alphaAnimation() {

        val mAlphaAnim = AlphaAnimation(1.0f, 0.0f)
        mAlphaAnim.duration = 1000
        mAlphaAnim.setAnimationListener(this)
        textView.animation = mAlphaAnim

    }

    private fun rotateAnimation() {
        val mRotateAnim = RotateAnimation(
                0.0f, -180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        )
        mRotateAnim.duration = 1000
        mRotateAnim.repeatMode = Animation.REVERSE
        mRotateAnim.repeatCount = 1
        mRotateAnim.setAnimationListener(this)
        textView.animation = mRotateAnim

    }

    private fun translateAnimation() {
        val mTranslateAnim = TranslateAnimation(
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 150f,
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f
        )

        mTranslateAnim.duration = 1000
        mTranslateAnim.setAnimationListener(this)
        textView.animation = mTranslateAnim
    }

    private fun scaleAnimation() {

        val mScaleAnim = ScaleAnimation(
                1.0f, 1.5f,
                1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        )

        mScaleAnim.duration = 1000
        mScaleAnim.setAnimationListener(this)
        textView.animation = mScaleAnim
    }

    override fun onAnimationRepeat(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {
        Toast.makeText(this,"complete",Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationStart(p0: Animation?) {

    }
}
