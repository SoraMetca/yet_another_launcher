package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sorametca.yet_another_launcher.R

class AboutFragment : Fragment() {

    private lateinit var versionInfo : TextView
    private lateinit var sorametcaGithub : TextView
    private lateinit var daraxtlarGithub : TextView
    private lateinit var flaticonsLink : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        versionInfo = view.findViewById(R.id.version_info)
        sorametcaGithub = view.findViewById(R.id.sorametca_github)
        daraxtlarGithub = view.findViewById(R.id.daraxtlar_github)
        flaticonsLink = view.findViewById(R.id.flaticons_link)

        versionInfo.movementMethod = LinkMovementMethod.getInstance()
        sorametcaGithub.movementMethod = LinkMovementMethod.getInstance()
        daraxtlarGithub.movementMethod = LinkMovementMethod.getInstance()
        flaticonsLink.movementMethod = LinkMovementMethod.getInstance()

    }
}