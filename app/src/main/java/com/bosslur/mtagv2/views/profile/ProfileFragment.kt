package com.bosslur.mtagv2.views.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.EXTRA_INTENT
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bosslur.mtagv2.BuildConfig
import com.bosslur.mtagv2.R
import com.bosslur.mtagv2.databinding.FragmentProfileBinding
import com.bosslur.mtagv2.dialog.MyDialog
import com.bosslur.mtagv2.hawkstorage.HawkStorage
import com.bosslur.mtagv2.model.LogoutResponse
import com.bosslur.mtagv2.networking.ApiServices
import com.bosslur.mtagv2.views.changepassword.ChangePasswordActivity
import com.bosslur.mtagv2.views.login.LoginActivity
import com.bosslur.mtagv2.views.main.MainActivity
import com.bumptech.glide.Glide
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        updateView()
    }

    private fun updateView() {
        val user = HawkStorage.instance(context).getUser()
        val imageUrl = BuildConfig.BASE_IMAGE_URL + user.photo

        Glide.with(context!!).load(imageUrl).placeholder(android.R.color.darker_gray).into(binding!!.ivProfile)
        binding?.tvNameProfile?.text = user.name
        binding?.tvEmailProfile?.text = user.email
    }

    private fun onClick() {
        binding?.btnChangeLanguage?.setOnClickListener {
            val content = Intent(ACTION_GET_CONTENT)
            content.type = "/image"
            content.putExtra(EXTRA_INTENT,1003)
            startActivity(content)
        }

        binding?.btnChangePassword?.setOnClickListener {
            context?.startActivity<ChangePasswordActivity>()
        }

        binding?.btnLogout?.setOnClickListener {
            AlertDialog.Builder(context)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.are_you_sure))
                    .setPositiveButton(getString(R.string.yes)){dialog, _ ->
                            logoutRequest(dialog)
                    }
                    .setNegativeButton(getString(R.string.enggak)){ dialog,_->
                        dialog.dismiss()
                    }
                    .show()

        }
    }

    private fun logoutRequest(dialog: DialogInterface?) {
        val token = HawkStorage.instance(context).getToken()
        MyDialog.showProgressDialog(context)
        ApiServices.getMtagServices()
                .logoutRequest("Bearer ${token}")
                .enqueue(object: Callback<LogoutResponse>{
                    override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                        dialog?.dismiss()
                        MyDialog.hideDialog()
                        if (response.isSuccessful){
                            HawkStorage.instance(context).deleteAll()
                            context?.startActivity<LoginActivity>()
                            (activity as MainActivity).finishAffinity()
                        }else{
                            MyDialog.dynamicDialog(context,"Kesalahan",response.message().toString())
                        }
                    }

                    override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                        dialog?.dismiss()
                        MyDialog.hideDialog()
                        MyDialog.dynamicDialog(context,"Kesalahan Fatal","${t.message}")
                    }

                })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}