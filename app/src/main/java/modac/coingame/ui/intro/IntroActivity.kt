package modac.coingame.ui.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.linecorp.apng.ApngDrawable
import com.linecorp.apng.RepeatAnimationCallback
import kotlinx.android.synthetic.main.activity_intro.*
import modac.coingame.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        MobileAds.initialize(this, object : OnInitializationCompleteListener {
            override fun onInitializationComplete(initializationStatus: InitializationStatus?) {}
        })
        val r = Runnable {
            val drawable = ApngDrawable.decode(this.resources,R.drawable.splash)
            drawable.registerAnimationCallback(animationCallback)
            runOnUiThread{
                img_splash.setImageDrawable(drawable)
                drawable.start()
            }
        }
        val thread = Thread(r)
        thread.start()
    }
    @SuppressLint("SetTextI18n")
    private val animationCallback = object : AnimationCallbacks(), Drawable.Callback {
        override fun onAnimationStart(drawable: Drawable?) {}
        override fun onAnimationRepeat(drawable: ApngDrawable, nextLoopIndex: Int) {}
        override fun onAnimationEnd(drawable: Drawable?) {
            val r = Runnable {
               Thread.sleep(1000)
                runOnUiThread{
                    startActivity(Intent(this@IntroActivity,MainActivity::class.java))
                    finish()
                }
            }
            val thread = Thread(r)
            thread.start()
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
        override fun invalidateDrawable(who: Drawable) {}
        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}

    }
    private abstract class AnimationCallbacks
        : Animatable2Compat.AnimationCallback(), RepeatAnimationCallback
}
