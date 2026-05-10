package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sorametca.yet_another_launcher.AppObject
import com.sorametca.yet_another_launcher.R
import kotlin.math.cos

class SortAppsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        print("SHOWED UP!!")

    }

    private fun sortApps(method : String, appList: ArrayList<AppObject>){
        when (method){
//            "ALPHABETICAL_AES" -> cos()
//            "ALPHABETICAL_DES" -> COS()
            
        }
    }
}