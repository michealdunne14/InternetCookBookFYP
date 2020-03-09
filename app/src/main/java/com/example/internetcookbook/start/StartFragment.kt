package com.example.internetcookbook.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.example.internetcookbook.R
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*

class StartFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons(view)

    }

    private fun initButtons(view: View) {
        view.mStartLogin.setOnClickListener {
            val action =
                StartFragmentDirections.actionStartFragment2ToLoginFragmentView()
            val extras = FragmentNavigatorExtras(
                mLogo to getString(R.string.logo_transition)
            )

            view.findNavController().navigate(action,extras)
        }
        view.mStartRegister.setOnClickListener {
            val action =
                StartFragmentDirections.actionStartFragment2ToRegisterFragmentView()
            view.findNavController().navigate(action)
        }
    }


}
