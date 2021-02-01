package com.bosslur.mtagv2.views.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bosslur.mtagv2.R
import com.bosslur.mtagv2.databinding.FragmentProfileBinding
import com.bosslur.mtagv2.hawkstorage.HawkStorage
import com.bosslur.mtagv2.views.changepassword.ChangePasswordActivity
import com.bosslur.mtagv2.views.login.LoginActivity
import com.bosslur.mtagv2.views.main.MainActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
    }

    private fun onClick() {
        binding?.btnChangeLanguage?.setOnClickListener {
            context?.toast("Coming Soon !")
        }

        binding?.btnChangePassword?.setOnClickListener {
            context?.startActivity<ChangePasswordActivity>()
        }

        binding?.btnLogout?.setOnClickListener {
            val token = HawkStorage.instance(context).deleteAll()
            context?.startActivity<LoginActivity>()
            (activity as MainActivity).finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}