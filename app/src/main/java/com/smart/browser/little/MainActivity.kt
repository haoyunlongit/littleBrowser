package com.smart.browser.little

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.smart.browser.little.databinding.MainActivityBinding
import com.smart.browser.little.manager.jumpGPMarket
import com.smart.browser.little.manager.sendFeedback
import com.sea.proxy.ProxyInit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val noActionBarDestinations = setOf(R.id.splash_fragment) // 没有ActionBar的页面
    private val topDestinations = // 顶层页面，不显示向上箭头，显示底部导航栏
        setOf(
            R.id.navigation_home, R.id.navigation_categories, R.id.navigation_games, R.id.navigation_favorites,
            R.id.feed_fragment, R.id.exit_app_dialog
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        binding.nvDrawer.itemIconTintList = null
        appBarConfiguration = AppBarConfiguration(topDestinations, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val topLevelDestinations = appBarConfiguration.topLevelDestinations
            val destinationId = destination.id

            if (topLevelDestinations.contains(destinationId)) {
                binding.navView.visibility = View.VISIBLE
            } else {
                binding.navView.visibility = View.GONE
            }

            if (noActionBarDestinations.contains(destinationId)) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

        binding.nvDrawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drawer_feedback -> {
                    sendFeedback(this)
                }
                R.id.drawer_privacy -> {
                    val directions = NavGraphDirections.toWebViewFragment(getString(R.string.privacy_policy),
                        getString(R.string.privacy_policy_url))
                    navController.navigate(directions)
                }
                R.id.drawer_rate_us -> {
                    jumpGPMarket(this)
                }
                R.id.drawer_about -> {
                    navController.navigate(R.id.to_about)
                }
            }
            binding.drawerLayout.close()
            false
        }
        ProxyInit.INSTANCE.recordNewUserSimInfo()
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isOpen){
            binding.drawerLayout.close()
            return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}