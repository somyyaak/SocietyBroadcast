package com.example.societybroadcast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.societybroadcast.fragment.LoginFragment
import com.example.societybroadcast.fragment.SignupFragment

class CategoryAdapter(fragment : FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0){
            LoginFragment()
        } else{
            SignupFragment()
        }
    }
}