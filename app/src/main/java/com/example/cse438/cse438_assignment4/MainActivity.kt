package com.example.cse438.cse438_assignment4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.cse438.cse438_assignment4.fragments.LoginFragment
import com.example.cse438.cse438_assignment4.fragments.SignupFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter

        tabs_main.setupWithViewPager(viewpager_main)

        auth = FirebaseAuth.getInstance()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if(fragment is LoginFragment){
            fragment.setFirebase(auth)
        } else if(fragment is SignupFragment){
            fragment.setFirebase(auth)
        }
    }
}



class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount() : Int {
        return 2
    }

    override fun getItem(position: Int) : Fragment {
        return when (position) {
            0 -> { LoginFragment() }
            else -> SignupFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Login"
            else -> "Sign Up"
        }
    }

}
