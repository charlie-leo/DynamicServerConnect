package com.dynamic.serverconnect.login.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

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

	@field:SerializedName("SAPUser")
	val sAPUser: String? = null,

	@field:SerializedName("WhsCode")
	val whsCode: String? = null,

	@field:SerializedName("Departament")
	val departament: String? = null,

	@field:SerializedName("UserCount")
	val userCount: String? = null,

	@field:SerializedName("Branch")
	val branch: String? = null,

	@field:SerializedName("UserCode")
	val userCode: String? = null
)
