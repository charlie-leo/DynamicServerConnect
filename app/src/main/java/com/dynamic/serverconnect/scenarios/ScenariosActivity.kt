package com.dynamic.serverconnect.scenarios

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dynamic.serverconnect.R
import com.dynamic.serverconnect.databinding.ScenariosActivityBinding
import com.dynamic.serverconnect.login.LoginActivity
import com.dynamic.serverconnect.scenarios.model.ScenarioResponse
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Charles Raj I on 17/08/23.
 * @author Charles Raj I
 */

@AndroidEntryPoint
class ScenariosActivity : AppCompatActivity() {


    private lateinit var scenarioActivityBinding: ScenariosActivityBinding

    private val scenarioViewModel: ScenariosViewModel by lazy {
        ViewModelProvider(this)[ScenariosViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scenarioActivityBinding = DataBindingUtil.setContentView(
            this,
            R.layout.scenarios_activity
        )
        scenarioActivityBinding.viewModel = scenarioViewModel

        scenarioViewModel.responseUpdate.observe(this,scenarioResponse())

        scenarioActivityBinding.logOut.setOnClickListener {
            onBackPressed()
        }
    }

    private fun scenarioResponse() = Observer<ScenarioResponse>{response ->

        response.table?.let {
            if (it.isNotEmpty()) {
                val scenarioAdapter: ScenarioAdapter = ScenarioAdapter(it)
                scenarioActivityBinding.scenariosRecyclerView.layoutManager =
                    LinearLayoutManager(this)
                scenarioActivityBinding.scenariosRecyclerView.adapter = scenarioAdapter
            }else{
                scenarioActivityBinding.noDataFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}