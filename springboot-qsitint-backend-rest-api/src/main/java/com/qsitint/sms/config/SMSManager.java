package com.qsitint.sms.config;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

public class SMSManager {
	
	private TwilioConfiguration twilioConfiguration;

	public SMSManager(TwilioConfiguration twilioConfiguration) {
		this.twilioConfiguration = twilioConfiguration;
	}
	
    public void sendSms(String smsTo, String smsFrom, String sms) {
        if (isPhoneNumberValid(smsTo)) {
            PhoneNumber to = new PhoneNumber(smsTo);
            PhoneNumber from = new PhoneNumber(smsFrom);
            String message = sms;
            MessageCreator creator = Message.creator(to, from, message);
            creator.create();
        } else {
            throw new IllegalArgumentException(
                    "Phone number [" + smsTo + "] is not valid number"
            );
        }

    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        // TODO: Implement phone number validator
        return true;
    }


}
