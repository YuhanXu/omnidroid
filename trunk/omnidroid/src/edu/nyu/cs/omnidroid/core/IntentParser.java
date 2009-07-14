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

import android.content.Intent;

/**
 * This class parses the received {@link android.content.Intent}'s action field to see if it is an
 * OmniDroid supported event. Then it creates an {@link Event} object which is able to parse the
 * intent for any associated data attributes.
 */
public class IntentParser {
  // TODO(londinop): put intent action fields in a database table rather than hard code
  public static final String SMS_INTENT_ACTION = "android.provider.Telephony.SMS_RECEIVED";

  /**
   * This is a static utility class which cannot be instantiated.
   */
  private IntentParser() {
  }

  /**
   * Given an intent with a supported action type, create and return an Event of the appropriate
   * type.
   * 
   * @param intent
   *          an intent received by the system describing the event that took place
   * @return an OmniDroid Event type that contains the methods to get at the event's data attributes
   */
  public static Event getEvent(Intent intent) {
    Event event = null;
    if (intent.getAction().equals(SMS_INTENT_ACTION)) {
      // Handle SMS event
      event = new SMSReceivedEvent(intent);
    }
    return event;
  }
}