package sk.xeito.nfc;

import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.TextView;

public class NfcMainActivity extends Activity implements CreateNdefMessageCallback {

	private static final String NFC_MIME_TYPE = "application/vnd.sk.xeito.nfc.beam";

	private NfcAdapter nfcAdapter;
	private TextView label;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		label = (TextView) findViewById(R.id.label);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			label.setText("NFC not supported");
		}
		else {
			label.setText("NFC is supported");
			nfcAdapter.setNdefPushMessageCallback(this, this);
		}
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String text = "Hello from Bratislava";

		String nfcAar = getApplicationContext().getPackageName();

		NdefMessage nfcMsg = new NdefMessage(
			new NdefRecord[] {
				createMime(NFC_MIME_TYPE, text.getBytes()),
				createApplicationRecord(nfcAar),
			}
		);

		return nfcMsg;
	}


	private static final Charset US_ASCII = Charset.forName("US-ASCII");
	private static NdefRecord createMime(String mimeType, byte [] mimeData) {
		if (mimeType == null) throw new NullPointerException("mimeType is null");

		// We only do basic MIME type validation: trying to follow the
		// RFCs strictly only ends in tears, since there are lots of MIME
		// types in common use that are not strictly valid as per RFC rules
		mimeType = Intent.normalizeMimeType(mimeType);
		if (mimeType.length() == 0) throw new IllegalArgumentException("mimeType is empty");
		int slashIndex = mimeType.indexOf('/');
		if (slashIndex == 0) throw new IllegalArgumentException("mimeType must have major type");
		if (slashIndex == mimeType.length() - 1) {
			throw new IllegalArgumentException("mimeType must have minor type");
		}
		// missing '/' is allowed

		// MIME RFCs suggest ASCII encoding for content-type
		byte [] typeBytes = mimeType.getBytes(US_ASCII);
		return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, typeBytes, null, mimeData);
	}

	private static final byte [] RTD_ANDROID_APP = "android.com:pkg".getBytes();
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static NdefRecord createApplicationRecord(String packageName) {
		if (packageName == null) throw new NullPointerException("packageName is null");
		if (packageName.length() == 0) throw new IllegalArgumentException("packageName is empty");

		return new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, RTD_ANDROID_APP, null, packageName.getBytes(UTF_8));
	}
}
