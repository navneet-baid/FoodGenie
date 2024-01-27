package com.navneet.foodgenie.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.navneet.foodgenie.activity.ForgotPasswordActivity
import com.navneet.foodgenie.activity.LoginActivity
import com.navneet.foodgenie.R


class ProfileFragment : Fragment() {

    lateinit var name: TextView
    lateinit var mobile: TextView
    lateinit var email: TextView
    lateinit var address: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var profileFragmentLayout: RelativeLayout
    lateinit var txtLinkForgotPassword: TextView
    lateinit var btnLogin: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        name = view.findViewById(R.id.profile_name)
        mobile = view.findViewById(R.id.profile_mobile)
        email = view.findViewById(R.id.profile_email)
        address = view.findViewById(R.id.profile_address)
        profileFragmentLayout = view.findViewById(R.id.profileRelativeayout)
        txtLinkForgotPassword = view.findViewById(R.id.txtLinkForgotPassword)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            name.text = sharedPreferences.getString("user_name", "Name").toString()
            mobile.text = sharedPreferences.getString("user_mobile_number", "xxxxxxxxxx")
            email.text = sharedPreferences.getString("user_email", "abc@email.com")
            address.text =
                sharedPreferences.getString("user_address", "Location not found").toString()
            txtLinkForgotPassword.setOnClickListener {
                val intent = Intent(context, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
        } else {
            profileFragmentLayout.visibility = View.GONE
            view = inflater.inflate(R.layout.login_to_continue, container, false)
            btnLogin = view.findViewById(R.id.loginBtn)
            btnLogin.setOnClickListener {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("mobile_number", "")
                intent.putExtra("forced_login", "true")
                startActivity(intent)
            }
        }
        return view
    }

}