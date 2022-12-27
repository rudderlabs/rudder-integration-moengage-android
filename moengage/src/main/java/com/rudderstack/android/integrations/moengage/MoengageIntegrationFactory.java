package com.rudderstack.android.integrations.moengage;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.moengage.core.MoECoreHelper;
import com.moengage.core.analytics.MoEAnalyticsHelper;
import com.moengage.core.Properties;
import com.moengage.core.model.AppStatus;
import com.moengage.core.model.UserGender;
import com.rudderstack.android.sdk.core.MessageType;
import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderConfig;
import com.rudderstack.android.sdk.core.RudderIntegration;
import com.rudderstack.android.sdk.core.RudderLogger;
import com.rudderstack.android.sdk.core.RudderMessage;
import com.rudderstack.android.sdk.core.RudderTraits;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MoengageIntegrationFactory extends RudderIntegration<MoEAnalyticsHelper> {

    private static final String MOENGAGE_KEY = "MoEngage";
    private final MoEAnalyticsHelper helper;
    private final Context context;

    public static Factory FACTORY = new Factory() {
        @Override
        public RudderIntegration<?> create(Object settings, RudderClient client, RudderConfig rudderConfig) {
            return new MoengageIntegrationFactory();
        }

        @Override
        public String key() {
            return MOENGAGE_KEY;
        }
    };

    private static final Set<String> MALE_KEYS = new HashSet<>(Arrays.asList("M",
            "MALE"));
    private static final Set<String> FEMALE_KEYS = new HashSet<>(Arrays.asList("F",
            "FEMALE"));
    private static final List<String> RESERVED_KEY_SET = Arrays.asList("USER_ATTRIBUTE_UNIQUE_ID", "USER_ATTRIBUTE_USER_EMAIL", "USER_ATTRIBUTE_USER_MOBILE",
            "USER_ATTRIBUTE_USER_NAME", "USER_ATTRIBUTE_USER_GENDER", "USER_ATTRIBUTE_USER_FIRST_NAME", "USER_ATTRIBUTE_USER_LAST_NAME",
            "USER_ATTRIBUTE_USER_BDAY", "MOE_TIME_FORMAT", "MOE_TIME_TIMEZONE",
            "USER_ATTRIBUTE_NOTIFICATION_PREF", "USER_ATTRIBUTE_OLD_ID", "MOE_TIME_FORMAT", "MOE_TIME_TIMEZONE",
            "USER_ATTRIBUTE_DND_START_TIME", "USER_ATTRIBUTE_DND_END_TIME", "MOE_GAID", "MOE_ISLAT", "status");

    private MoengageIntegrationFactory() {
        this.helper = MoEAnalyticsHelper.INSTANCE;
        this.context = RudderClient.getApplication();
    }

    private void processRudderEvent(RudderMessage element) {
        if (element.getType() != null) {
            switch (element.getType()) {
                // handling actions performed by a user
                case MessageType.TRACK:
                    String event = element.getEventName();
                    if (event == null) {
                        return;
                    } else if (event.equals("Application Installed")) {
                        helper.setAppStatus(context, AppStatus.INSTALL);
                    } else if (event.equals("Application Updated")) {
                        helper.setAppStatus(context, AppStatus.UPDATE);
                    }

                    Map<String, Object> eventProperties = element.getProperties();
                    if (eventProperties == null || eventProperties.size() == 0) {
                        RudderLogger.logDebug("MoEngage event has no properties");
                        helper.trackEvent(context, element.getEventName(), new Properties());
                        return;
                    }
                    JSONObject propertiesJson = new JSONObject(eventProperties);
                    helper.trackEvent(context, element.getEventName(), jsonToProperties(propertiesJson));
                    break;
                // identifying a user in the MoEngage and setting attributes in his profile
                case MessageType.IDENTIFY:
                    // logging out the previous user if any existing
                    reset();
                    String userId = element.getUserId();
                    if (!TextUtils.isEmpty(userId)) {
                        // logging in the user into MoEngage
                        helper.setUniqueId(context, userId);
                    }
                    Map<String, Object> traitsMap = element.getTraits();
                    if (traitsMap == null) {
                        return;
                    }
                    // handling standard attributes of a user on MoEngage
                    Date birthday = dateFromString(RudderTraits.getBirthday(traitsMap));
                    if (birthday != null) {
                        helper.setBirthDate(context, birthday);
                        traitsMap.remove("birthday");
                    }
                    String email = RudderTraits.getEmail(traitsMap);
                    if (!TextUtils.isEmpty(email)) {
                        helper.setEmailId(context, email);
                        traitsMap.remove("email");
                    }
                    String firstName = RudderTraits.getFirstname(traitsMap);
                    if (!TextUtils.isEmpty(firstName)) {
                        helper.setFirstName(context, firstName);
                        traitsMap.remove("firstname");
                    }
                    String lastName = RudderTraits.getLastname(traitsMap);
                    if (!TextUtils.isEmpty(lastName)) {
                        helper.setLastName(context, lastName);
                        traitsMap.remove("lastname");
                    }
                    String fullName = RudderTraits.getName(traitsMap);
                    if (!TextUtils.isEmpty(fullName)) {
                        helper.setUserName(context, fullName);
                        traitsMap.remove("name");
                    }
                    String gender = RudderTraits.getGender(traitsMap);
                    if (!TextUtils.isEmpty(gender)) {
                        if (MALE_KEYS.contains(gender.toUpperCase())) {
                            helper.setGender(context, UserGender.MALE);
                        } else if (FEMALE_KEYS.contains(gender.toUpperCase())) {
                            helper.setGender(context, UserGender.FEMALE);
                        }
                        traitsMap.remove("gender");
                    }
                    String phone = RudderTraits.getPhone(traitsMap);
                    if (!TextUtils.isEmpty(phone)) {
                        helper.setMobileNumber(context, phone);
                        traitsMap.remove("phone");
                    }
                    String address = RudderTraits.getAddress(traitsMap);
                    if (!TextUtils.isEmpty(address)) {
                        helper.setUserAttribute(context, "address", address);
                        traitsMap.remove("address");
                    }
                    String age = RudderTraits.getAge(traitsMap);
                    if (!TextUtils.isEmpty(age)) {
                        helper.setUserAttribute(context, "age", age);
                        traitsMap.remove("age");
                    }
                    // handling custom attributes of a user on MoEngage
                    for (String key : traitsMap.keySet()) {
                        if (RESERVED_KEY_SET.contains(key)) {
                            continue;
                        }
                        Object value = traitsMap.get(key);
                        if (value instanceof Boolean) {
                            helper.setUserAttribute(context, key, value);
                        } else if (value instanceof Integer) {
                            helper.setUserAttribute(context, key, value);
                        } else if (value instanceof Double) {
                            helper.setUserAttribute(context, key, value);
                        } else if (value instanceof Float) {
                            helper.setUserAttribute(context, key, value);
                        } else if (value instanceof Long) {
                            helper.setUserAttribute(context, key, value);
                        } else if (value instanceof Date) {
                            long secondsFromEpoch = ((Date) value).getTime() / 1000L;
                            helper.setUserAttribute(context, key, secondsFromEpoch);
                        } else if (value instanceof String) {
                            helper.setUserAttribute(context, key, value);
                        } else {
                            RudderLogger.logDebug("MoEngage can't map rudder value for custom MoEngage user "
                                    + "attribute with key " + key + "and value " + value);
                        }
                    }
                    break;
                // handling ALIAS for merging two different profiles of same user
                case MessageType.ALIAS:
                    String newUserId = element.getUserId();
                    if (!TextUtils.isEmpty(newUserId)) {
                        helper.setAlias(context, newUserId);
                    }
                default:
                    RudderLogger.logWarn("MoEngageIntegrationFactory: MessageType is not specified");
                    break;
            }
        }
    }

    @Override
    public void reset() {
        // logging out user
        if (context != null) {
            MoECoreHelper.INSTANCE.logoutUser(context);
            RudderLogger.logDebug("RESET is called");
        } else {
            RudderLogger.logWarn("RESET is not called since context is not set.");
        }
    }

    @Override
    public MoEAnalyticsHelper getUnderlyingInstance() {
        return helper;
    }

    @Override
    public void dump(@Nullable RudderMessage element) {
        try {
            if (element != null) {
                processRudderEvent(element);
            }
        } catch (Exception e) {
            RudderLogger.logError(e);
        }
    }

    // converting Date from String to Date type
    private static Date dateFromString(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    // converting JSON Object to Properties Object
    @NonNull
    private static Properties jsonToProperties(JSONObject json) {
        try {
            Properties properties = new Properties();
            JSONArray keys = json.names();
            for (int i = 0; i < keys.length(); ++i) {
                String key = keys.getString(i);
                Object value = json.get(key);
                if (value instanceof String || value instanceof Number || value instanceof Boolean) {
                    properties.addAttribute(key, json.get(key));
                }
            }
            return properties;
        } catch (Exception e) {
            RudderLogger.logWarn("Error occurred while converting json to Properties object: "  + e);
            return new Properties();
        }
    }
}