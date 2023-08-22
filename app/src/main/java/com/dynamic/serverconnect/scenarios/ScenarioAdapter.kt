package com.dynamic.serverconnect.scenarios

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dynamic.serverconnect.R
import com.dynamic.serverconnect.databinding.ScenarioItemBinding
import com.dynamic.serverconnect.scenarios.model.TableItem

/**
 * Created by Charles Raj I on 19/08/23
 * @project DynamicServerConnect
 * @author Charles Raj
 */
class ScenarioAdapter(table: List<TableItem?>) : RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder>(){

    val scenarios: List<TableItem?> = table

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val scenarioItemBinding: ScenarioItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.scenario_item,parent,false)
        return ScenarioViewHolder(scenarioItemBinding)
    }

    override fun getItemCount(): Int {
        return scenarios.count()
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        scenarios[position]?.let { holder.onBind(scenario = it) }
    }

    class ScenarioViewHolder(itemView: ScenarioItemBinding): RecyclerView.ViewHolder(itemView.root) {
        private val scenarioItemBinding = itemView
        fun onBind(scenario: TableItem){
            scenarioItemBinding.itemData = scenario
        }

    }

}