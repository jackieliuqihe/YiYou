package com.yiyouapp.controls;

import com.yiyouapp.R;

import android.app.AlertDialog;
import android.content.Context;

public class CustomAlertDialog extends AlertDialog {

	protected CustomAlertDialog(Context context) {
		super(context, R.style.custom_alert_dialog);
		// TODO Auto-generated constructor stub
	}

	protected CustomAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	protected CustomAlertDialog(Context context, int theme) {
		super(context, R.style.custom_alert_dialog);
		// TODO Auto-generated constructor stub
	}

}
