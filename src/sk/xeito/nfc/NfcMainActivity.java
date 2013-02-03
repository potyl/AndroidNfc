package sk.xeito.nfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.TextView;

public class NfcMainActivity extends Activity implements CreateNdefMessageCallback {

	private static final String NFC_AAR = "sk.xeito.nfc.beam";
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String text = "Hello from Bratislava";

		NdefMessage nfcMsg = new NdefMessage(
			new NdefRecord[] {
				NdefRecord.createMime(NFC_MIME_TYPE, text.getBytes()),
//				NdefRecord.createApplicationRecord(NFC_AAR),
			}
		);

		return nfcMsg;
	}


	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();

		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			processIntent(intent);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		label.setText(new String(msg.getRecords()[0].getPayload()));
	}
}
