package com.akitsugutamagawa.firebasetest_android

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.akitsugutamagawa.firebasetest_android.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings



class MainActivity : AppCompatActivity() {

    var binding :ActivityMainBinding? = null
    var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    var firebaseAnalytics: FirebaseAnalytics? = null

    private val configKey = "background_image"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val settingBuilder = FirebaseRemoteConfigSettings.Builder()

        //デバッグモードを有効に
        settingBuilder.setDeveloperModeEnabled(true)

        firebaseRemoteConfig?.setConfigSettings(settingBuilder.build())
        // デフォルトの値を設定
        firebaseRemoteConfig?.setDefaults(R.xml.firebase_param)

        // キャッシュの保持時間
        var cacheExpiration: Long = 3600 // 一時間
        if ((firebaseRemoteConfig as FirebaseRemoteConfig).info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        firebaseRemoteConfig?.fetch(cacheExpiration)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 取得した値を利用するように設定
                firebaseRemoteConfig?.activateFetched()
            }
            setBackgroundImage()
        }
    }

    private fun setBackgroundImage() {
        //フェッチした内容から表示するimageが変わる
        val imageName = (firebaseRemoteConfig as FirebaseRemoteConfig).getString(configKey)
        binding?.image?.setImageResource(this.resources.getIdentifier(imageName,"drawable",this.packageName))

    }
}
