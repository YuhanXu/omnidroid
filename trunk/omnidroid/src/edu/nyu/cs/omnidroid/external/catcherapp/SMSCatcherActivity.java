/*******************************************************************************
 * Copyright 2009 OmniDroid - http://code.google.com/p/omnidroid 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *******************************************************************************/
package edu.nyu.cs.omnidroid.external.catcherapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;
import edu.nyu.cs.omnidroid.core.CP;

/**
 * Activity used to fetch data from the OmniDroid content provider based on the
 * URI's received from DummyActivity.
 * The SMSCatcherActivity needs to have permissions defined to read and write
 * SMSes in the Android Manifest file, in order to be able to send smses.
 *
 *
 */
public class SMSCatcherActivity extends Activity {

  private String uri;
  private String uri2;
  private String text;
  public String txtmsg;
  Intent intent;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.intent = getIntent();
    // First URI in the Extras.
    uri = getURI(intent);
    // Second URI in the Extras
    uri2 = getURI2(intent);
    // Retrieving the reply message by querying the OmniDroid Content provider.
    text = retrieveURI2(uri2.toString());

    displayAction(uri.toString(), text);
    // deleteSMS();
    this.finish();
  }

  /**
   * Gets the first URI from the extras.
   *
   * @param intent The intent that started this activity
   */
  public String getURI(Intent intent) {
    Bundle b = intent.getExtras();
    Object c = b.get("uri");
    String uri = c.toString();
    return uri;

  }

  /**
   * Gets the second URI from the extras.
   *
   * @param intent The intent that started this activity
   */
  public String getURI2(Intent intent) {
    Bundle b = intent.getExtras();
    Object c = b.get("uri2");
    String uri = c.toString();
    return uri;
  }

  /**
   * Retrieves the message out of the second URI by querying the OmniDriod Content Provider.
   *
   * @param uri URI retrieved from the getURI2() function.
   */
  public String retrieveURI2(String uri) {
    String str_uri = uri;
    String[] temp = null;
    temp = str_uri.split("/");
    String num = temp[temp.length - 1];
    String final_uri = str_uri.substring(0, str_uri.length() - num.length() - 1);
    int new_id = Integer.parseInt(num);

    Cursor cur = managedQuery(Uri.parse(final_uri), null, null, null, null);
    if (cur.moveToFirst()) {

      do {

        int id = Integer.parseInt(cur.getString(cur.getColumnIndex("_id")));

        if (new_id == id) {
          txtmsg = cur.getString(cur.getColumnIndex(CP.ACTION_DATA));

        }

      } while (cur.moveToNext());

    }
    return txtmsg;

  }

  /**
   * Processes the first URI. Queries the content provider and sends the text message.
   *
   * @param uri
   * @param txtmsg
   *          - First URI from the extras - txtmsg retrieved from the retrieveURI2() function.
   */
  public void displayAction(String uri, String txtmsg) {
    String str_uri = uri;
    String[] temp = null;
    temp = str_uri.split("/");
    String num = temp[temp.length - 1];
    String final_uri = str_uri.substring(0, str_uri.length() - num.length() - 1);
    int new_id = Integer.parseInt(num);

    Cursor cur = managedQuery(Uri.parse(final_uri), null, null, null, null);
    if (cur.moveToFirst()) {

      do {

        int id = Integer.parseInt(cur.getString(cur.getColumnIndex("_id")));

        if (new_id == id) {
          String number = cur.getString(cur.getColumnIndex(CP.ACTION_DATA));
          Toast.makeText(getBaseContext(), number, Toast.LENGTH_LONG).show();
          try {
            sendSMS(number, txtmsg);
          } catch (Exception e) {
            e.toString();
          }
        }

      } while (cur.moveToNext());

    }

  }

  /**
   * Sends the SMS message.
   *
   * @param phoneNumber
   * @param message
   *          - phone number where the message is to be sent - Message to be sent
   */
  private void sendSMS(String phoneNumber, String message) {
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

    PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

    registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context arg0, Intent arg1) {
        Toast.makeText(getBaseContext(), "SMS SENT", Toast.LENGTH_SHORT).show();
      }
    }, new IntentFilter(SENT));

    registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context arg0, Intent arg1) {
        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();

      }
    }, new IntentFilter(DELIVERED));

    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
  }

  /**
   * Deletes all SMSes from the Inbox
   *
   *
   */
  public void deleteSMS() {
    try {
      getContentResolver().delete(Uri.parse("content://sms/conversations/1"), null, null);
      Toast.makeText(getBaseContext(), "Messages deleted", Toast.LENGTH_LONG).show();
    } catch (Exception e) {
    }
  }
}