package modac.coingame.ui.ingame

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.google.android.gms.ads.AdRequest
import com.linecorp.apng.ApngDrawable
import com.linecorp.apng.RepeatAnimationCallback
import kotlinx.android.synthetic.main.activity_mix.*
import kotlinx.android.synthetic.main.activity_mix.adView
import modac.coingame.R
import modac.coingame.ui.intro.MainActivity.Companion.socket

class MixActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mix)
        init()
    }
    private fun init(){
        adView.loadAd(AdRequest.Builder().build())
        startAnims()

    }
    private fun startAnims(){
        val anims = AnimatorSet()
        val transition1 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,0f,-100f)
        val transition2 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,-100f,100f)
        val transition3 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,100f,-100f)
        val transition4 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,-100f,100f)
        val transition5 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,100f,-100f)
        val transition6 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,-100f,100f)
        val transition7 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,100f,-100f)
        val transition8 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,-100f,100f)
        val transition9 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,100f,0f)
        anims.apply {
            playSequentially(transition1,transition2,transition3,transition4,transition5,transition6,transition7,transition8,transition9)
            duration = 300
            start()
            doOnEnd {
                cl_mix1Layout.visibility = View.GONE
                img_mix2.visibility = View.VISIBLE
                val r = Runnable {
                    val drawable = ApngDrawable.decode(resources,R.drawable.shake_coin)
                    drawable.registerAnimationCallback(animationCallback)
                    runOnUiThread{
                        img_mix2.setImageDrawable(drawable)
                        drawable.start()
                    }
                }
                val thread = Thread(r)
                thread.start()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private val animationCallback = object : AnimationCallbacks(), Drawable.Callback {
        override fun onAnimationStart(drawable: Drawable?) {}
        override fun onAnimationRepeat(drawable: ApngDrawable, nextLoopIndex: Int) {}
        override fun onAnimationEnd(drawable: Drawable?) {
            val inetnt1 = Intent(this@MixActivity, ResultActivity::class.java)
            inetnt1.putExtra("front",intent.getStringExtra("front"))
            inetnt1.putExtra("back",intent.getStringExtra("back"))
            startActivity(inetnt1)
            finish()
        }
        override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
        override fun invalidateDrawable(who: Drawable) {}
        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}

    }
    private abstract class AnimationCallbacks
        : Animatable2Compat.AnimationCallback(), RepeatAnimationCallback
    override fun onDestroy() {
        socket.off()
        super.onDestroy()
    }
}
