package com.dynamic.serverconnect.scenarios.model

import com.google.gson.annotations.SerializedName

data class ScenarioResponse(

	@field:SerializedName("table")
	val table: List<TableItem?>? = null,

	@field:SerializedName("status")
	val status: Status? = null
)

data class Status(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class TableItem(

	@field:SerializedName("Destination")
	val destination: String? = null,

	@field:SerializedName("bcolor")
	val bcolor: String? = null,

	@field:SerializedName("WhsCode")
	val whsCode: String? = null,

	@field:SerializedName("Locations")
	val locations: String? = null,

	@field:SerializedName("fcolor")
	val fcolor: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("Process")
	val process: String? = null,

	@field:SerializedName("Source")
	val source: String? = null,

	@field:SerializedName("Scenario")
	val scenario: String? = null,

	@field:SerializedName("Role")
	val role: String? = null,

	@field:SerializedName("sequence")
	val sequence: String? = null,

	@field:SerializedName("badge")
	val badge: String? = null,

	@field:SerializedName("ObjType")
	val objType: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("short")
	val jsonMemberShort: String? = null
)
