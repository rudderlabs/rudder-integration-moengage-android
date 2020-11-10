package com.rudderstack.android.integrations.moengage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.moe.pushlibrary.MoEHelper;
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

public class MoengageIntegrationFactory extends RudderIntegration<MoEHelper> {

    private static final String MOENGAGE_KEY = "MoEngage";
    private MoEHelper helper;

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
        // creating MOEHelper Object
        this.helper = MoEHelper.getInstance(RudderClient.getApplication().getApplicationContext());

        //  handling life cycle methods of an Application
        RudderClient.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
                RudderLogger.logVerbose(" onActivityCreated() : ");
                if (helper == null && activity != null) {
                    helper = MoEHelper.getInstance(activity.getApplicationContext());
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                RudderLogger.logVerbose(" onActivityStarted() : ");
                if (helper != null && activity != null)
                    helper.onStartInternal(activity);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                RudderLogger.logVerbose(" onActivityResumed() : ");
                if (helper != null && activity != null)
                    helper.onResumeInternal(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                //nothing to implement
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                RudderLogger.logVerbose(" onActivityStopped() : ");
                if (helper != null && activity != null)
                    helper.onStopInternal(activity);
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @Nullable Bundle bundle) {
                RudderLogger.logVerbose(" onActivitySaveInstanceState() : ");
                if (helper != null)
                    helper.onSaveInstanceState(bundle);
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                // nothing to implement
            }

        });
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
                        helper.setAppStatus(AppStatus.INSTALL);
                    } else if (event.equals("Application Updated")) {
                        helper.setAppStatus(AppStatus.UPDATE);
                    }

                    Map<String, Object> eventProperties = element.getProperties();
                    if (eventProperties == null || eventProperties.size() == 0) {
                        RudderLogger.logDebug("MoEngage event has no properties");
                        helper.trackEvent(element.getEventName(), new Properties());
                        return;
                    }
                    JSONObject propertiesJson = new JSONObject(eventProperties);
                    helper.trackEvent(element.getEventName(), jsonToProperties(propertiesJson));
                    break;
                // identifying a user in the MoEngage and setting attributes in his profile
                case MessageType.IDENTIFY:
                    String userId = element.getUserId();
                    if (!TextUtils.isEmpty(userId)) {
                        // logging in the user into MoEngage
                        helper.setUniqueId(userId);
                    }
                    Map<String, Object> traitsMap = element.getTraits();
                    if (traitsMap == null) {
                        return;
                    }
                    // handling standard attributes of a user on MoEngage
                    Date birthday = dateFromString(RudderTraits.getBirthday(traitsMap));
                    if (birthday != null) {
                        helper.setBirthDate(birthday);
                        traitsMap.remove("birthday");
                    }
                    String email = RudderTraits.getEmail(traitsMap);
                    if (!TextUtils.isEmpty(email)) {
                        helper.setEmail(email);
                        traitsMap.remove("email");
                    }
                    String firstName = RudderTraits.getFirstname(traitsMap);
                    if (!TextUtils.isEmpty(firstName)) {
                        helper.setFirstName(firstName);
                        traitsMap.remove("firstname");
                    }
                    String lastName = RudderTraits.getLastname(traitsMap);
                    if (!TextUtils.isEmpty(lastName)) {
                        helper.setLastName(lastName);
                        traitsMap.remove("lastname");
                    }
                    String fullName = RudderTraits.getName(traitsMap);
                    if (!TextUtils.isEmpty(fullName)) {
                        helper.setFullName(fullName);
                        traitsMap.remove("name");
                    }
                    String gender = RudderTraits.getGender(traitsMap);
                    if (!TextUtils.isEmpty(gender)) {
                        if (MALE_KEYS.contains(gender.toUpperCase())) {
                            helper.setGender(UserGender.MALE);
                        } else if (FEMALE_KEYS.contains(gender.toUpperCase())) {
                            helper.setGender(UserGender.FEMALE);
                        }
                        traitsMap.remove("gender");
                    }
                    String phone = RudderTraits.getPhone(traitsMap);
                    if (!TextUtils.isEmpty(phone)) {
                        helper.setNumber(phone);
                        traitsMap.remove("phone");
                    }
                    String address = RudderTraits.getAddress(traitsMap);
                    if (!TextUtils.isEmpty(address)) {
                        helper.setUserAttribute("address", address);
                        traitsMap.remove("address");
                    }
                    String age = RudderTraits.getAge(traitsMap);
                    if (!TextUtils.isEmpty(age)) {
                        helper.setUserAttribute("age", age);
                        traitsMap.remove("age");
                    }
                    // handling custom attributes of a user on MoEngage
                    for (String key : traitsMap.keySet()) {
                        if (RESERVED_KEY_SET.contains(key)) {
                            continue;
                        }
                        Object value = traitsMap.get(key);
                        if (value instanceof Boolean) {
                            helper.setUserAttribute(key, (Boolean) value);
                        } else if (value instanceof Integer) {
                            helper.setUserAttribute(key, (Integer) value);
                        } else if (value instanceof Double) {
                            helper.setUserAttribute(key, (Double) value);
                        } else if (value instanceof Float) {
                            helper.setUserAttribute(key, (Float) value);
                        } else if (value instanceof Long) {
                            helper.setUserAttribute(key, (Long) value);
                        } else if (value instanceof Date) {
                            long secondsFromEpoch = ((Date) value).getTime() / 1000L;
                            helper.setUserAttribute(key, secondsFromEpoch);
                        } else if (value instanceof String) {
                            helper.setUserAttribute(key, (String) value);
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
                        helper.setAlias(newUserId);
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
        helper.logoutUser();
    }

    @Override
    public MoEHelper getUnderlyingInstance() {
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
    private static Properties jsonToProperties(JSONObject json) {
        try {
            Properties properties = new Properties();
            JSONArray key = json.names();
            for (int i = 0; i < key.length(); ++i) {
                properties.addAttribute(key.getString(i), json.getString(key.getString(i)));
            }
            return properties;
        } catch (Exception e) {
            return null;
        }
    }
}