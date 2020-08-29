package com.example.mypowerfulandroidapp.util

import android.app.Activity
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.mypowerfulandroidapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
/**
 * Class credit: Allan Veloso
 * I took the concept from Allan Veloso and made alterations to fit our needs.
 * https://stackoverflow.com/questions/50577356/android-jetpack-navigation-bottomnavigationview-with-youtube-or-instagram-like#_=_
 * @property navigationBackStack: Backstack for the bottom navigation
 */
class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val appStartDestinationId: Int,
    val graphChangeListener: OnNavigationGraphChanged?,
    val navGraphProvider: NavGraphProvider
) {
    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    lateinit var navItemChangeListener: OnNavigationItemChanged
    private val navigationBackStack = BackStack.of(appStartDestinationId)

    init {
        if (context is Activity) {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun onNavigationItemSelected(itemId: Int = navigationBackStack.last()): Boolean {
        //Replace fragment representing a navigation item
        val fragment = fragmentManager
            .findFragmentByTag(itemId.toString())
            ?: NavHostFragment.create(navGraphProvider.getNavGraphId(itemId))
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, itemId.toString())
            .addToBackStack(null)
            .commit()
        //Add to backStack
        navigationBackStack.moveLast(itemId)
        //Update checked icon
        navItemChangeListener.onItemChanged(itemId)
        //communicate with activity
        graphChangeListener?.onGraphChange()
        return true
    }

    fun onBackPressed() {
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
            .childFragmentManager
        when {
            childFragmentManager.popBackStackImmediate() -> {
            }
            //fragment backStack is empty so try to back on navigation stack
            navigationBackStack.size > 1 -> {
                //remover last item from backStack
                navigationBackStack.removeLast()
                //update container with new fragment
                onNavigationItemSelected()
            }
            //if the stack has only one and it's not the navigation home
            //we should ensure that the application always leave rom start destination
            navigationBackStack.last()!=appStartDestinationId->{
                navigationBackStack.removeLast()
                navigationBackStack.add(0,appStartDestinationId)
                onNavigationItemSelected()
            }
            else->activity.finish()
        }
    }

    private class BackStack : ArrayList<Int>() {
        companion object {
            fun of(vararg element: Int): BackStack {
                val b = BackStack()
                b.addAll(element.toTypedArray())
                return b;
            }
        }

        fun removeLast() = removeAt(size - 1)
        fun moveLast(item: Int) {
            remove(item)
            add(item)
        }
    }

    //for setting the checked icon in bottom nav
    interface OnNavigationItemChanged {
        fun onItemChanged(itemId: Int)
    }

    //setter for OnNavigationItemChanged
    fun setOnItemNavigationChanged(listener: (itemId: Int) -> Unit) {
        this.navItemChangeListener = object : OnNavigationItemChanged {
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }
        }
    }

    //get id of each graph
    //ex: R.navigation.nav_blog
    interface NavGraphProvider {
        @NavigationRes
        fun getNavGraphId(itemId: Int): Int
    }

    //Execute when navigation graph changes
    //ex: Select a new item on the bottom nav
    //ex: home -> account
    interface OnNavigationGraphChanged {
        fun onGraphChange()
    }

    interface OnNavigationReselectedListener {
        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }


}

fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onNavigationReselectedListener: BottomNavController.OnNavigationReselectedListener
) {
    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)
    }
    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager
            .findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let { fragment ->
            onNavigationReselectedListener.onReselectNavItem(
                bottomNavController.activity.findNavController(bottomNavController.containerId),
                fragment
            )

        }
    }
    bottomNavController.setOnItemNavigationChanged { itemId ->
        menu.findItem(itemId)?.isChecked = true
    }
}