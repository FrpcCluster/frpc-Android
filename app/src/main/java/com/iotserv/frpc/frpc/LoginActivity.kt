package com.iotserv.frpc.frpc

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.view.View
import com.iotserv.frpc.frpc.databinding.ActivityLoginBinding
import frpclib.Frpclib
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val CONFIG_FILE_PATH = "frpc.ini"
    }

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.emailSignInButton.setOnClickListener { attemptLogin() }
        binding.fileChooseButton.setOnClickListener {
            Utils.doBrowseFile(this)
        }
        Utils.verifyStoragePermissions(this@LoginActivity as Activity)
        if (File(getConfigPath()).exists()) {
            binding.email.setText(getConfigPath())
        }
    }

    private fun attemptLogin() {
        showProgress(true)
        binding.email.error = null
        val email = binding.email.text.toString()
        GlobalScope.launch {
            Frpclib.run(email)
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)
        binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
        binding.loginForm.animate().setDuration(shortAnimTime.toLong()).alpha(
            if (show) 0F else 1.toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
            }
        })
        binding.loginProgress.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginProgress.animate().setDuration(shortAnimTime.toLong()).alpha(
            if (show) 1F else 0.toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.loginProgress.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == Activity.RESULT_OK) {
            val result = Utils.parseBrowseIntent(requestCode, resultData)
            result?.let {
                binding.email.setText(getConfigPath())
                GlobalScope.launch {
                    Utils.saveFile(this@LoginActivity, result, getConfigPath())
                }
            }
        }
    }

    private fun getConfigPath(): String {
        return cacheDir.absolutePath + File.separator + CONFIG_FILE_PATH
    }
}