package modac.coingame.ui.ingame

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import kotlinx.android.synthetic.main.activity_mix.*
import modac.coingame.R

class MixActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mix)
        init()
    }
    private fun init(){
        val anims = AnimatorSet()
        val transition1 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,0f,-100f)
        val transition2 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,-100f,100f)
        val transition3 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,100f,-100f)
        val transition5 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,-100f,100f)
        val transition6 = ObjectAnimator.ofFloat(img_bowl, View.TRANSLATION_X,100f,0f)
        anims.apply {
            playSequentially(transition1,transition2,transition3,transition5,transition6)
            duration = 300
            start()
            doOnEnd {
                startActivity(Intent(applicationContext,ResultActivity::class.java))
                finish()
            }
        }
    }
}
