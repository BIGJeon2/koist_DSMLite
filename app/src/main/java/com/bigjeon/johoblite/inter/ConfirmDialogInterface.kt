package com.bigjeon.johoblite.inter

import com.bigjeon.johoblite.data.Crack

interface ConfirmDialogInterface {
	fun onSaveButtonClick(crack: Crack, id: Int?)
}