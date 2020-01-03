package com.example.internetcookbook.fragmentview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.internetcookbook.R
import com.example.internetcookbook.network.Common
import com.example.internetcookbook.network.InformationStore
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLogoButton(view)
    }

    private fun initLogoButton(view: View) {
        logo.setOnClickListener {
            val action =
                StartFragmentDirections.transitionToPager()
            val common = Common()
            InformationStore.GetData().execute(common.getAddressApiName())

            view.findNavController().navigate(action)
        }
    }


}