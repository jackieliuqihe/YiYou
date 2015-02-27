package com.yiyouapp;

import java.util.List;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.utils.NotificationUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.ChatMsg;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.UserNews;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 
 *  0 - Success
 *  10001 - Network Problem
 *  30600 - Internal Server Error
 *  30601 - Method Not Allowed 
 *  30602 - Request Params Not Valid
 *  30603 - Authentication Failed 
 *  30604 - Quota Use Up Payment Required 
 *  30605 - Data Required Not Found 
 *  30606 - Request Time Expires Timeout 
 *  30607 - Channel Token Timeout 
 *  30608 - Bind Relation Not Found 
 *  30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class AppMsgReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = FrontiaPushMessageReceiver.class
            .getSimpleName();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (errorCode == 0) {
        	AppConfig.settings.setDeviceUserID(userId);
        	AppConfig.settings.setDeviceChannelID(channelId);

        	AppConfig.settings.setDeviceBind(true);
        	
        	// 通知提交设备ID
			AppFacade.getInstance().sendNotification(AccountCmd.SET_DEVICE_INFO);
			
        }
        
    }

    /**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);

        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(message)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(message);
                
                int action = customJson.getInt("action");
                JSONObject data = customJson.getJSONObject("data");

                // 60 - 89 之间认为是消息
                if(action < 60 || action > 89)
                	return;
                
                UserNews news;

            	String title;
            	String msg;
            	
            	Intent intent;
            	
                switch(action)
                {
                case TypeDef.NT_Message: // 单用户对话
	                news = new UserNews();
	
	                news.news_type = action;
	                news.user_id = data.getInt("user_id");
	                news.user_name = data.getString("user_name");
	                news.avatar64_path = StringUtil.getString(data.getString("avatar_path")).replace("*", "64");
	                news.news = data.getString("msg");
	                
	                NotificationUtil.msgNavigate(context, news);
	                break;
                case TypeDef.NT_CommentWork: // 用户对作品的评论
	                news = new UserNews();
	
	                news.news_type = action;
	                news.user_id = data.getInt("user_id");
	                news.user_name = data.getString("user_name");
	                news.extra_data = data.getInt("work_id");
	                news.news = data.getString("comment");
	                
	                NotificationUtil.commentNavigate(context, news);
	                break;
                case TypeDef.NT_ReplyComment: // 回复评论
	                news = new UserNews();
	
	                news.news_type = action;
	                news.user_id = data.getInt("user_id");
	                news.user_name = data.getString("user_name");
	                news.extra_data = data.getInt("work_id");
	                news.news = data.getString("comment");
	                
	                NotificationUtil.commentNavigate(context, news);
	                break;
                case TypeDef.NT_PublishWork: // 用户发作品的通知
	                
                	int user_id = data.getInt("user_id");
	                String user_name = data.getString("user_name");
	                //data.getInt("work_id");
	                msg = data.getString("msg");

	    			intent = new Intent(context, WorkListActivity.class);
	    			
	    			intent.putExtra(AppConfig.INTENT_USER_ID, user_id);
	    			intent.putExtra(AppConfig.INTENT_USER_NAME, user_name + "的作品");
	    			intent.putExtra(AppConfig.INTENT_COLLECT, false);

	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	    			
    		        NotificationUtil.notify(context, intent, msg, user_name, msg);
                	break;
                case TypeDef.NT_SendMsgToAll: // 对所有人的通告消息
                	title = data.getString("title");
                	msg = data.getString("msg");
                	
                	intent = new Intent(context, MainActivity.class);
                	
    		        NotificationUtil.notify(context, intent, msg, title, msg);
                	break;
                default:
                	break;
                }
                
            } catch (JSONException e) {
            	
            }
        }

    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);

                UserNews news = new UserNews();
                
                news.news_type = customJson.getInt("action");
                news.user_id = customJson.getInt("user_id");
                news.user_name = title;
                news.avatar64_path = StringUtil.getString(customJson.getString("avatar_path")).replace("*", "64");
                news.extra_data = customJson.getInt("data");
                
                NotificationUtil.newsNavigate(context, news);
                
                NotificationUtil.setNewsViewed2(context, news);
                
            } catch (JSONException e) {
            }
        }

    }

    /**
     * setTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * delTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * listTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

    }

    /**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
        	AppConfig.settings.setDeviceBind(false);
        }
        
    }

}
