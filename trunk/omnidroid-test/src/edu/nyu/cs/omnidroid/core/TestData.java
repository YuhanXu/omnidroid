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
package edu.nyu.cs.omnidroid.core;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import edu.nyu.cs.omnidroid.util.OmnidroidException;

// TODO(kaijohnson): Replace with mock database code.
public class TestData {
  /**
   * Sample phone number and message text for an SMS event
   */
  public static final String TEST_PHONE_NO = "5556";
  public static final String TEST_PHONE_NO2 = "5557";
  public static final String TEST_MESSAGE_TEXT = "The moon in June is a big, big balloon";

  public static Rule getRule() {
    return createRule(TEST_PHONE_NO);
  }
  
  public static Rule getAnotherRule() {
    return createRule(TEST_PHONE_NO2);
  }
  
  private static Rule createRule(String phoneNumber) {
    String name = "Auto-Reply DND";
    String eventName = "SMS";
    HashMap<String, String> parameters = new HashMap<String, String>();
    // TODO(kaijohnson): Find appropriate constant for "Phone Number" and "Text Message" instead
    // of String literals.
    parameters.put("Phone Number", TEST_PHONE_NO);
    parameters.put("Text Message", TEST_MESSAGE_TEXT);
    ArrayList<Filter> filters = new ArrayList<Filter>();
    Filter filter = new Filter(SMSReceivedEvent.ATTRIB_PHONE_NO, phoneNumber);
    filters.add(filter);
    ArrayList<Action> actions = new ArrayList<Action>();
    SendSmsAction action = null;
    try {
      action = new SendSmsAction(parameters);
    } catch (OmnidroidException e) {
      // Some input was null
      e.printStackTrace();
    }
    actions.add(action);
    return new Rule(name, eventName, filters, actions);
  }

  public static ArrayList<Rule> getRulesForEvent(String eventName) {
    ArrayList<Rule> rules = new ArrayList<Rule>();
    rules.add(getRule());
    return rules;
  }

  public static SMSReceivedEvent getSMSEvent() {
    return new MockSMSReceivedEvent(getIntent(TEST_PHONE_NO, TEST_MESSAGE_TEXT));
  }

  public static Intent getIntent(String phone, String text) {
    Intent intent = new Intent();
    // TODO(londinop): SMSReceivedEvent is expecting an extra by the name of "pdus" here
    intent.putExtra(SMSReceivedEvent.ATTRIB_PHONE_NO, phone);
    intent.putExtra(SMSReceivedEvent.ATTRIB_MESSAGE_TEXT, text);
    return intent;
  }
}
